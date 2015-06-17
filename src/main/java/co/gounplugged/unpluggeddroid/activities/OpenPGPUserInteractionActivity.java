package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Marvin Arnold on 16/06/15.
 */
public class OpenPGPUserInteractionActivity extends Activity {
    private final static String TAG = "InteractionActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        Intent startIntent = getIntent();
        PendingIntent pi = startIntent.getParcelableExtra("api_intent");
        if(pi != null){
            Log.d(TAG, "pi no null");
            try {
                startIntentSenderForResult(pi.getIntentSender(), 42, null, 0, 0, 0);
                Log.d(TAG, "started pi");
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }
}
