package co.gounplugged.unpluggeddroid.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Binder;
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

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;

/**
 * Created by Marvin Arnold on 10/06/15.
 */
public class OpenPGPBridgeService extends Service {
    protected static final String TAG = "OpenPGPBridgeService";
    protected OpenPgpServiceConnection mServiceConnection;
    private final IBinder mBinder = new LocalBinder();
    public static final String ACTION_ENCRYPT = "OpenPGPBridgeService_ACTION_ENCRYPT";
    public static final String EXTRA_PLAINTEXT = "OpenPGPBridgeService_EXTRA_PLAINTEXT";
    public static final String EXTRA_RECIPIENT = "OpenPGPBridgeService_EXTRA_RECIPIENT";
    protected boolean isBound;
    private OpenPgpServiceConnection.OnBound onBoundCallback;
    private OpenPgpApi mAPI;

    public class LocalBinder extends Binder {
        public OpenPGPBridgeService getService() {
            // Return this instance of LocalService so clients can call public methods
            return OpenPGPBridgeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Created");

        isBound = false;

        onBoundCallback = new OpenPgpServiceConnection.OnBound() {
            @Override
            public void onBound(IOpenPgpService service) {
                isBound = true;
                mAPI = new OpenPgpApi(OpenPGPBridgeService.this, mServiceConnection.getService());
                Log.d(TAG, "onBound!");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "exception when binding!", e);
            }
        };

        mServiceConnection = new OpenPgpServiceConnection(
            getApplicationContext(),
            "org.sufficientlysecure.keychain",
            onBoundCallback
        );
        mServiceConnection.bindToService();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        if (mServiceConnection != null) {
            mServiceConnection.unbindFromService();
        }
    }

    public String decrypt(String ciphertext) throws EncryptionUnavailableException {
        Log.d(TAG, "Attempt encrypt");
        if(isBound) {
            Intent data = new Intent();
            data.setAction(OpenPgpApi.ACTION_DECRYPT_VERIFY);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                is = new ByteArrayInputStream(ciphertext.getBytes("UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // TODO
            }

            mAPI.executeApi(data, is, os);
            return "decrypted";
        } else {
            throw new EncryptionUnavailableException("Description service not yet bound");
        }
    }

    /**
     * Currently throws an exception unless key matching recipientAddress
     * exists and encryption performed without error.
     * @param plaintext
     * @param recipientAddress phone number, email, or whatever
     * @return
     * @throws EncryptionUnavailableException
     */
    public String encrypt(String plaintext, String recipientAddress) throws EncryptionUnavailableException {
        Log.d(TAG, "Attempt encrypt");
        if(isBound) {
//        String recipient = encryptIntent.getStringExtra(EXTRA_RECIPIENT);
//        String plaintext = encryptIntent.getStringExtra(EXTRA_PLAINTEXT);

            recipientAddress = "marvin@gounplugged.co"; //TODO remove
            Intent data = new Intent();
            data.setAction(OpenPgpApi.ACTION_ENCRYPT);
            data.putExtra(OpenPgpApi.EXTRA_USER_IDS, new String[]{recipientAddress});
            data.putExtra(OpenPgpApi.EXTRA_REQUEST_ASCII_ARMOR, true);

            InputStream is = null;
            try {
                is = new ByteArrayInputStream(plaintext.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                //TODO
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Intent result = mAPI.executeApi(data, is, os);

            switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
                case OpenPgpApi.RESULT_CODE_SUCCESS: {
                    try {
                        String encrypted = os.toString("UTF-8");
                        Log.d(TAG, "output: " + encrypted);
                        return encrypted;
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "UnsupportedEncodingException", e);
                        throw new EncryptionUnavailableException("Invalid encoding");
                    }
                }
                case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                    Log.d(TAG, "Action required ");
    //                PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
    //                try {
    ////                    startIntentSenderForResult(pi.getIntentSender(), 42, null, 0, 0, 0);
    //                } catch (IntentSender.SendIntentException e) {
    //                    Log.e(TAG, "SendIntentException", e);
    //
    //                }
                    throw new EncryptionUnavailableException("User interaction required");
                }
                case OpenPgpApi.RESULT_CODE_ERROR: {
                    OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                    throw new EncryptionUnavailableException("Encryption error: " + error.getMessage());
                }
            }
            throw new EncryptionUnavailableException("Unknown response from encryption service.");
        } else {
            throw new EncryptionUnavailableException("Encryption service not yet bound");
        }
    }


}