package com.example.ash.musicbuddybeta;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
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

public class shareData extends Service {

    String id, track, artist, album;
    String insertURL = "http://mush.000webhostapp.com/insert.php";
    /*String showURL = "http://edmstreet.com/show.php";*/
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;

    public shareData() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences("MySharedString", 0);
//        handle = sharedPreferences.getString("handle", "");
//        name = sharedPreferences.getString("name", "Name NA");
//        email = sharedPreferences.getString("email", "Email NA");
         id = "1";// for testing
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        track = intent.getStringExtra("track");
        artist = intent.getStringExtra("artist");
        album = intent.getStringExtra("album");

        StringRequest request = new StringRequest(Request.Method.POST, insertURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Song Shared!" + response, Toast.LENGTH_SHORT).show();
                Log.v("Song Shared Service", response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.v("Song Shared Service", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("id", id);
                parameters.put("track", track);
                parameters.put("artist", artist);
                parameters.put("album", album);
                return parameters;
            }
        };
        requestQueue.add(request);
        this.stopSelf();
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
