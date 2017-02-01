package com.example.ash.musicbuddybeta;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicReceiver extends Service {

    final static String serviceReturn = "serviceReturn";

    public MusicReceiver() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");

        iF.addAction("com.htc.music.metachanged");

        iF.addAction("fm.last.android.metachanged");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        iF.addAction("com.amazon.mp3.metachanged");
        iF.addAction("com.miui.player.metachanged");
        iF.addAction("com.real.IMP.metachanged");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.andrew.apollo.metachanged");

        registerReceiver(mReceiver, iF);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*return super.onStartCommand(intent, flags, startId);*/
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.v("tag", artist + ":" + album + ":" + track);
            Toast.makeText(MusicReceiver.this, track, Toast.LENGTH_SHORT).show();

            Intent returnData = new Intent();
            returnData.setAction(serviceReturn);
            returnData.putExtra("returnTrack", track);
            returnData.putExtra("returnArtist", artist);
            returnData.putExtra("returnAlbum", album);
            sendBroadcast(returnData);
        }
    };

}
