package com.example.ash.musicbuddybeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

/**
 * Created by Ash on 08-Jan-16.
 */
public class loginChecker extends Activity {

    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {

                accessToken = newAccessToken;

            }
        };
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (accessToken == null) {
                    updateWithToken(AccessToken.getCurrentAccessToken());
                } else {
                    updateWithToken(accessToken);
                }
            }
        }, 0);


    }


    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            startActivity(new Intent("android.intent.action.MAINACTIVITY"));
            finish();
        } else {
            startActivity(new Intent("android.intent.action.LOGINACTIVITY"));//android.intent.action.LOGINACTIVITY
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
