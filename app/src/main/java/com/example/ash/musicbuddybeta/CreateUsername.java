package com.example.ash.musicbuddybeta;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ash on 08-Feb-16.
 */
public class CreateUsername extends Activity {
    EditText etUsername;
    Button btCreateUsername;
    String handle = "";
    String fbid = "";
    String fbname = "";
    String fbgender = "";
    String fbemail = "";
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    public static String filename = "MySharedString";
    String userURL = "http://edmstreet.com/user.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_username);
        etUsername = (EditText) findViewById(R.id.etUsername);
        btCreateUsername = (Button) findViewById(R.id.bCreateUsername);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences(filename, 0);
        btCreateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle = etUsername.getText().toString();
                fbid = sharedPreferences.getString("id", "0");
                /*fbid="1234567890";*/
                fbname = sharedPreferences.getString("name", "Name NA");
                fbemail = sharedPreferences.getString("email", "Email NA");
                checkUsername(handle, fbid, fbname, fbemail);
            }
        });
    }

    private void checkUsername(final String handle, final String id, final String name, final String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, userURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String reverseString = response;
                if (reverseString.equals("exists")) {
                    etUsername.setText("");
                    Toast.makeText(getApplicationContext(), "Username exists " + reverseString, Toast.LENGTH_LONG).show();
                }
                if (reverseString.equals("created")) {
                    etUsername.setText("");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("handle", handle);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Username Created " + reverseString, Toast.LENGTH_LONG).show();
                    startActivity(new Intent("android.intent.action.MAINACTIVITY"));
                    finish();
                }
                Toast.makeText(getApplicationContext(),  reverseString, Toast.LENGTH_LONG).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Check your Internet Connection![createusername act]", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("handle", handle);
                parameters.put("fbid", id);
                parameters.put("fbname", name);
                parameters.put("fbemail", email);


                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }

/*
    protected void createUsername(final String username, final String id, final String name, final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, usernameURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Username Created!", Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Check your Internet Connection![username act]", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("handle", username);
                parameters.put("fbid", id);
                parameters.put("fbname", name);
                parameters.put("fbemail", email);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }
*/

}







