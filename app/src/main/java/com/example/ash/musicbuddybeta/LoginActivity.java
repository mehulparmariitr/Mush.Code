package com.example.ash.musicbuddybeta;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.lang.Package;

/**
 * Created by Ash on 29-Dec-15.
 */
public class LoginActivity extends Activity {
    CallbackManager callbackManager;
    public SharedPreferences loginData;
    public static String filename = "MySharedString";
    String id, name, email;
    RequestQueue requestQueue;
    String handleURL = "http://edmstreet.com/friendlist.php";
    String profilePicUrl = null;
    String profilePicture = null;
    String EncodedProfilePic = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());



        requestQueue = Volley.newRequestQueue(getApplicationContext());

        loginData = getSharedPreferences(filename, 0);

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.facebook_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONArray friendlist = response.getJSONObject().getJSONArray("data");
                                    //Log.v("fuckingfb", friendlist.toString());
                                    SharedPreferences.Editor editor = loginData.edit();
                                    editor.putString("friendlist", friendlist.toString());
                                    editor.apply();
                                    getHandles(friendlist);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                ).executeAsync();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                id = object.optString("id");
                                name = object.optString("name");
                                email = object.optString("email");


                                try {
                                    profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    //Bitmap profilePic = getFacebookProfilePicture(profilePicUrl);
                                    FacebookLogin task = new FacebookLogin();
                                    task.execute(profilePicUrl);
                                    /*EncodedProfilePic = task.sendEncodedProfilePic();
                                    Log.v("ImageSting","login activity: "+ EncodedProfilePic);
*/
                                    /*URL facebookProfileURL = new URL(profilePicUrl);
                                    Bitmap bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());*/

                                    //profilePicture = encodeToBase64(bitmap);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                /*try {
                                    JSONArray friends =response.getJSONObject().getJSONArray("/me/friends");
                                    Toast.makeText(getApplicationContext(), "tried", Toast.LENGTH_LONG).show();


                                    for (int l=0; l < friends.length(); l++) {
                                        Log.v("fuckingfb",friends.getJSONArray(l).getString(l));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/

                                SharedPreferences.Editor editor = loginData.edit();
                                editor.putString("id", id);
                                editor.putString("name", name);
                                editor.putString("email", email);
                                //editor.putString("picture", profilePicture);
                                editor.apply();

                                /*try {
                                    Log.v("LoginActivity", String.valueOf(object.getJSONArray("user_friends")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
                                Toast.makeText(getApplicationContext(), "Result: " + id + " " + name + " " + email, Toast.LENGTH_LONG).show();
                                Log.v("LoginActivity", String.valueOf(response));

                                startActivity(new Intent("android.intent.action.CREATEUSERNAME"));
                                finish();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "check your internet bitch cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private class FacebookLogin extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            URL facebookProfileURL = null;
            try {
                facebookProfileURL = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profilePicture = encodeToBase64(bitmap);
            SharedPreferences.Editor editor = loginData.edit();
            editor.putString("picture", profilePicture);
            editor.apply();

        }

        public String sendEncodedProfilePic() {
            return profilePicture;
        }
    }

    private static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    private Bitmap getFacebookProfilePicture(String profilePicUrl) {
        URL facebookProfileURL = null;
        try {
            facebookProfileURL = new URL(profilePicUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            /*assert facebookProfileURL != null;
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) facebookProfileURL.openConnection();
            HttpsURLConnection.setFollowRedirects(true);
            httpsURLConnection.setInstanceFollowRedirects(true);*/
            bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    private void getHandles(final JSONArray friendlist) {
        Log.v("fuckingfb", friendlist.toString());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, handleURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray handleList = jsonObject.getJSONArray("friendlist");
                    ArrayList<String> handleArrayList = new ArrayList<>();
                    for (int i = handleList.length() - 1; i >= 0; i--) {
                        JSONObject handles = handleList.getJSONObject(i);
                        handleArrayList.add(handles.getString("handle"));
                        Log.v("fuckingfb", handles.getString("handle"));
                    }
                    SharedPreferences.Editor editor = loginData.edit();
                    editor.putString("handlelist", handleArrayList.toString());
                    editor.apply();


                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    Log.v("fuckingfb", response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, handleURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //JSONArray feeds = response.getJSONArray("friendlist");
                        Log.v("fuckingfb", response.toString());
                    }*/

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("fuckingfb", error.getMessage());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("handle", friendlist.toString());
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

   /* public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
