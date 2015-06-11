package co.gounplugged.unpluggeddroid.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.openintents.openpgp.IOpenPgpService;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpServiceConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Marvin Arnold on 10/06/15.
 */
public class OpenPGPBridgeService extends Service {
    private static final String TAG = "OpenPGPBridgeService";
    private OpenPgpServiceConnection mServiceConnection;
    public static final String ACTION_ENCRYPT = "OpenPGPBridgeService_ACTION_ENCRYPT";
    public static final String EXTRA_PLAINTEXT = "OpenPGPBridgeService_EXTRA_PLAINTEXT";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Created");

        mServiceConnection = new OpenPgpServiceConnection(
            getApplicationContext(),
            "org.sufficientlysecure.keychain",
            new OpenPgpServiceConnection.OnBound() {
                @Override
                public void onBound(IOpenPgpService service) {
                    Log.d(TAG, "onBound!");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "exception when binding!", e);
                }
            }
        );
        mServiceConnection.bindToService();
    }

    @Override
    public void onDestroy() {
        if (mServiceConnection != null) {
            mServiceConnection.unbindFromService();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_ENCRYPT)) {
            Log.d(TAG, "Attempt encrypt");
            Intent data = new Intent();
            data.setAction(OpenPgpApi.ACTION_ENCRYPT);
            data.putExtra(OpenPgpApi.EXTRA_USER_IDS, new String[]{"marvin@gounplugged.co"});
            data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);

            InputStream is = null;
            try {
                is = new ByteArrayInputStream("Hello world!".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "FUCK4");
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            OpenPgpApi api = new OpenPgpApi(this, mServiceConnection.getService());
            if(api == null) Log.d(TAG, "FUCK");
            if(is == null) Log.d(TAG, "FUCK2");
            if(os == null) Log.d(TAG, "FUCK3");
            if(data == null) Log.d(TAG, "FUCK5");

            Intent result = api.executeApi(data, is, os);

            switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
                case OpenPgpApi.RESULT_CODE_SUCCESS: {
                    try {
                        Log.d(TAG, "output: " + os.toString("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "UnsupportedEncodingException", e);
                    }
                    break;
                }
                case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                    break;
                }
                case OpenPgpApi.RESULT_CODE_ERROR: {
                    break;
                }
            }
        }

        return 0;
    }

    private void encrypt() {
        Log.d(TAG, "Attempt encrypt");
        Intent data = new Intent();
        data.setAction(OpenPgpApi.ACTION_ENCRYPT);
        data.putExtra(OpenPgpApi.EXTRA_USER_IDS, new String[]{"marvinmarnold@gmail.com"});
        data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);

        InputStream is = null;
        try {
            is = new ByteArrayInputStream("Hello world!".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "FUCK4");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        OpenPgpApi api = new OpenPgpApi(this, mServiceConnection.getService());
        if(api == null) Log.d(TAG, "FUCK");
        if(is == null) Log.d(TAG, "FUCK2");
        if(os == null) Log.d(TAG, "FUCK3");
        if(data == null) Log.d(TAG, "FUCK5");

        Intent result = api.executeApi(data, is, os);

        switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
            case OpenPgpApi.RESULT_CODE_SUCCESS: {
                try {
                    Log.d(TAG, "output: " + os.toString("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "UnsupportedEncodingException", e);
                }
                break;
            }
            case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                try {
//                    startIntentSenderForResult(pi.getIntentSender(), 42, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "SendIntentException", e);

                }
                break;
            }
            case OpenPgpApi.RESULT_CODE_ERROR: {
                OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);

                Log.e(TAG, "why b " + error.getMessage());
                break;
            }
        }


    }
}
