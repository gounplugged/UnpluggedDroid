package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Profile;

public class SplashActivity extends BaseActivity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2 * 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Take user to new Activity depending on state of Application.
                int applicationState = Profile.getApplicationState();
                switch (applicationState) {
                    // Go to ProfileActivity in order to be able to send messages
                    case Profile.APPLICATION_STATE_UNINITALIZED:
                        Intent profileIntent = new Intent(SplashActivity.this, ProfileActivity.class);
                        SplashActivity.this.startActivity(profileIntent);
                        SplashActivity.this.finish();
                        break;
                    // Use saved settings and go to ChatActivity
                    case Profile.APPLICATION_STATE_INITALIZED:
                        Intent chatIntent = new Intent(SplashActivity.this, ChatActivity.class);
                        SplashActivity.this.startActivity(chatIntent);
                        SplashActivity.this.finish();
                        break;
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
