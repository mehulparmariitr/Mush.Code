package com.example.ash.musicbuddybeta;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //android.intent.action.MAINACTIVITY

    TextView mainTrack;
    TextView mainArtist;
    TextView mainName;

    int mId = 001211;
    NetworkImageView networkImageView;
    String nameTrack = "ayush", nameArtist = "sharma", nameAlbum;
    ListView mListView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String showURL = "http://mush.000webhostapp.com/show.php";
    RequestQueue requestQueue;
    public static String filename = "MySharedString";
    SharedPreferences sharedPreferences;
    Bitmap imageBitmap;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            nameTrack = intent.getStringExtra("returnTrack");
            nameArtist = intent.getStringExtra("returnArtist");
            nameAlbum = intent.getStringExtra("returnAlbum");

            updateNotification.updateNotif(getApplicationContext(), mId, nameTrack, nameArtist, nameAlbum, mNotificationManager);
            Toast.makeText(MainActivity.this, nameArtist, Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        networkImageView = (NetworkImageView) findViewById(R.id.profilePic);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences(filename, 0);
        String imageString = sharedPreferences.getString("picture", "");
        Log.v("ImageSting", "there should be sth here: " + imageString);
        imageBitmap = decodeToBase64(imageString);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true); //For Performace Improvement

        refresh();
        setupFeed();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                populateListView();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        Intent I = new Intent(this, MusicReceiver.class);
        this.startService(I);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicReceiver.serviceReturn);
        registerReceiver(myReceiver, intentFilter);

        /*printHashKey();*/

        refresh();
        populateListView();
        Toast.makeText(MainActivity.this, "fuckk you fuck this fuck all", Toast.LENGTH_SHORT).show();


        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),getCursorAdp());
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            startActivity(new Intent("android.intent.action.MAINACTIVITY"));
            finish();
        } else {
            startActivity(new Intent("android.intent.action.MAINFRAGMENT"));
            finish();
        }

            /*new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, MainFragment.class);
                    startActivity(i);

                    finish();
                }
            }, 0);
        }*/
    }

    private void refresh() {

        String handleString = sharedPreferences.getString("handlelist", "NA");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gs = new Gson();
        //ArrayList<String> handlelist = gs.fromJson(handleString, type);
        ArrayList<String> handlelist = new ArrayList<>();
        handlelist.add("1");
        //handlelist.add("ayush");

        for (int counter = 0; counter < handlelist.size(); counter++) {
            final String handle = handlelist.get(counter);
            Log.v("motherfuck", handle);
            getFeeds(handle);
            //for getting indivisual feeds for each handle
        }
    }

    private void getFeeds(final String handle) {
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, showURL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray feeds = jsonObject.getJSONArray("feed"); //key of encoded returned JSONarray
                    SQLiteDB sqliteObject = new SQLiteDB(MainActivity.this);
                    sqliteObject.open();
                        /*sqliteObject.deleteAll();*/
                        /*JSONArray feeds = feed.getJSONArray("feed"); //key of encoded returned JSONarray*/
                    Log.v("fuckingfb", response);

                    for (int i = feeds.length() - 1; i >= 0; i--) {
                        JSONObject feed = feeds.getJSONObject(i);

                        int sid = feed.getInt("sid");
                        int id = feed.getInt("id");
                        String track = feed.getString("track");
                        String artist = feed.getString("artist");
                        String timestamp = feed.getString("timestamp");
                        Long timeMilli = timeStampinMilli(timestamp);
                        //Log.v("time", "fromServer " + timestamp);
                        timestamp = timeStamp(timestamp);

                        sqliteObject.insert(id, "Mehul", timestamp, track, artist, timeMilli);
                    }
                    sqliteObject.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("handle", handle);
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);

    }

    private String timeStamp(String timestamp) {
/*        String milliseconds1 = "0";
        String currentDateandTime = "0";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDateandTime = sdf.format(new Date());
        Date date;
        try {
            date = sdf.parse(currentDateandTime);


            milliseconds1 = String.valueOf(new Date().getTime());
            Toast.makeText(MainActivity.this, "sdf" + milliseconds1, Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, currentDateandTime
                , Toast.LENGTH_LONG).show();
        String msimi = String.valueOf(System.currentTimeMillis());
        Toast.makeText(MainActivity.this, "current" + msimi, Toast.LENGTH_LONG).show();*/
        //Log.v("time", "currenttime " + System.currentTimeMillis());
        long milliseconds = Math.abs(System.currentTimeMillis() - timeStampinMilli(timestamp));


        String sharedTime = "";
        long seconds = milliseconds / 1000;
        //Log.v("time", "difference " + seconds);
        if (seconds < 60)
            sharedTime = String.valueOf(seconds) + "sec ago";
        else if (seconds >= 60 && seconds < 3600)
            sharedTime = String.valueOf((int) seconds / 60) + " min ago";
        else if (seconds >= 3600 && seconds < 86400)
            sharedTime = String.valueOf((int) seconds / 3600) + " hours ago";
        else if (seconds >= 86400 && seconds < 172800)
            sharedTime = " Yesterday";
        else if (seconds >= 172800 && seconds < 2592000)
            sharedTime = String.valueOf((int) (seconds / (24 * 3600))) + " days ago";
        else if (seconds >= 2592000)
            sharedTime = String.valueOf((int) (seconds / (30 * 24 * 3600))) + " months ago";

        return sharedTime;
    }

    private long timeStampinMilli(String timestamp) {
        long milliseconds = 0;
//        String milliseconds1="0";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = simpleDateFormat.parse(timestamp);

//            Toast.makeText(MainActivity.this, date.toString(), Toast.LENGTH_LONG).show();

            milliseconds = date.getTime();
//            milliseconds1= String.valueOf(date.getTime());
//            Toast.makeText(MainActivity.this,  milliseconds1, Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            milliseconds = 0;
            e.printStackTrace();
        }
        //Log.v("time", "serverMili " + milliseconds);
        return milliseconds;
    }

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.ash.musicbuddybeta",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private SimpleCursorAdapter getCursorAdp(){
        SQLiteDB getDatabase = new SQLiteDB(MainActivity.this);
        getDatabase.open();
        SimpleCursorAdapter cursor = getDatabase.getCursorAdapter();
        getDatabase.close();
        Log.v("cursor", "reached");
        return cursor;
    }

    private void populateListView() {
        /*SQLiteDB getDatabase = new SQLiteDB(MainActivity.this);
        getDatabase.open();
        SimpleCursorAdapter cursorAdapter = getDatabase.getCursorAdapter();
        getDatabase.close();
        //networkImageView.setImageBitmap(imageBitmap);
        mListView.setAdapter(cursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_LONG).show();
            }
        });
        mListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Item Long Pressed", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),getCursor());
        recyclerView.setAdapter(recyclerViewAdapter);*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatemen
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.ash.musicbuddybeta/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.ash.musicbuddybeta/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}