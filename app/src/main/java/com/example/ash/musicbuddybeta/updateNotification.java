package com.example.ash.musicbuddybeta;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Ash on 18-Dec-15.
 */
public class updateNotification {

    static public void updateNotif(Context context, int Id, String Track, String Artist, String Album, NotificationManager notificationManager) {


        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, 0, openApp, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent insertData = new Intent(context, shareData.class);
        insertData.setAction("share");
        insertData.putExtra("track", Track);
        insertData.putExtra("artist", Artist);
        insertData.putExtra("album", Album);
        PendingIntent insertDataPendingIntent = PendingIntent.getService(context, 0, insertData, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(Track)
                        .setContentText(Artist)
                        .setContentIntent(openAppPendingIntent)
                        .addAction(R.drawable.notification_icon, "Share it", insertDataPendingIntent)
                        //.setOngoing(true)
                        .setTicker(Track + " : " + Artist)
                        /*.setContent(remoteView)*/
                        .setAutoCancel(true)/*.setTicker("")*/;

        notificationManager.notify(Id, mBuilder.build());
    }
}
