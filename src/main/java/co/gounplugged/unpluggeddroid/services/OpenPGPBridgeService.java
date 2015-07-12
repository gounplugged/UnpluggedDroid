package co.gounplugged.unpluggeddroid.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.openintents.openpgp.IOpenPgpService;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpServiceConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import co.gounplugged.unpluggeddroid.activities.OpenPGPUserInteractionActivity;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;

/**
 * Created by Marvin Arnold on 10/06/15.
 */
public class OpenPGPBridgeService extends Service {
    protected static final String TAG = "OpenPGPBridgeService";
    protected OpenPgpServiceConnection mServiceConnection;
    private final IBinder mBinder = new LocalBinder();
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
                Log.d(TAG, "onBound! to OpenPGPBridgeService");

            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "exception when binding!", e);
            }
        };

        mServiceConnection = new OpenPgpServiceConnection(
            getApplicationContext(),
            "org.sufficientlysecure.keychain.debug",
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
        return ciphertext;
        /*Log.d(TAG, "Attempt decrypt");
        ciphertext = ciphertext.replaceFirst(ThrowParser.THROW_IDENTIFIER, "");
        try {
            ciphertext = new String(Base64.decode(ciphertext));
        } catch (IOException e) {
            throw new EncryptionUnavailableException("Not encoded correctly");
        }
        Log.d(TAG, "CIPHERTEXT: " + ciphertext);
        if(isBound) {
            Intent data = new Intent();
            data.setAction(OpenPgpApi.ACTION_DECRYPT_VERIFY);
            data.putExtra(OpenPgpApi.EXTRA_PASSPHRASE, Profile.getPassword().toCharArray());

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                is = new ByteArrayInputStream(ciphertext.getBytes("UTF-8"));
                Intent result = mAPI.executeApi(data, is, os);
                Log.d(TAG, "Decryption result received");
                return interpretResult(result, os);
            } catch (UnsupportedEncodingException e) {
                throw new EncryptionUnavailableException("unsupported encoding");
            }
        } else {
            throw new EncryptionUnavailableException("Description service not yet bound");
        }*/
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
            // add compression https://stackoverflow.com/questions/6717165/how-can-i-zip-and-unzip-a-string-using-gzipoutputstream-that-is-compatible-with/6718707#6718707
            return interpretResult(result, os);
        } else {
            throw new EncryptionUnavailableException("Encryption service not yet bound");
        }
    }

    private String interpretResult(Intent result, ByteArrayOutputStream os) throws EncryptionUnavailableException {
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
                PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                Log.d(TAG, "Action required " + pi.getCreatorPackage() + " sender " + pi.getIntentSender().toString());

                Intent localUserInteractionIntent = new Intent(this, OpenPGPUserInteractionActivity.class);
                localUserInteractionIntent.putExtra("api_intent", pi);
                localUserInteractionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(localUserInteractionIntent);

                throw new EncryptionUnavailableException("User interaction required");
            }
            case OpenPgpApi.RESULT_CODE_ERROR: {
                OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                Log.d(TAG, "RESULT_CODE_ERROR: " + error.getMessage());
                throw new EncryptionUnavailableException("Encryption error: " + error.getMessage());
            }
            default:
                Log.d(TAG, "Something strange");
                throw new EncryptionUnavailableException("Unknown response from encryption service.");
        }
    }

    public void generatePGPKey() throws EncryptionUnavailableException {
        Log.d(TAG, "Attempt generate key");
        /*if(isBound) {
            recipientAddress = "marvin@gounplugged.co"; //TODO remove
            Intent data = new Intent();
            data.setAction(OpenPgpApi.ACTION_GENERATE_KEY);
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
            // add compression https://stackoverflow.com/questions/6717165/how-can-i-zip-and-unzip-a-string-using-gzipoutputstream-that-is-compatible-with/6718707#6718707
            return interpretResult(result, os);
        } else {
            throw new EncryptionUnavailableException("Encryption service not yet bound");
        }*/
    }

    /*String testDecrypt = "-----BEGIN PGP MESSAGE-----\n" +
            "Version: GnuPG v2\n" +
            "\n" +
            "hQIMAwNJDWvmOi2RARAA2/yL/E2xKAxMRjBF1EYaGJXmiQ1FCg9b5XU97CueuKHh\n" +
            "OUuDHocwcBzzckux77l3F6JEQFb1hBWze3cPepOp3yVcXbITTn43qnhZKTPL204w\n" +
            "DOE4Nzgr8MbbL538X/zmNwXoKcJjQ0neqyMF7SzNE3pJ21hJSju4UY4PB9VxGQbn\n" +
            "KSk7IsF02VFGA7jeja22Ys39aPfdSZiUP37nh2ZVZZwD5wLR9SxG8wUs2WvxPIGQ\n" +
            "rrK9M023xcQ2HB74SyQY4wRK1Nu9eDTeMhJD4NlSYpGuVp9R5Q24kjFpcWTno/h5\n" +
            "EmjkJqrMageFWLFWL4kIK+E1bYA0ssYKFAO32EPPUws2y9jG7stjLIUbn7Tti96t\n" +
            "fJtFe7KjBIyq7aqGGlJexDrJL1PB3LjqU+nF2CKhdxuJjcHdSoJKJTzGh+KxgmRu\n" +
            "ZvhBH+tTM6nabPAreCrqQO0BEdAoYJuvOhMNuPEeIVR6BZRoklEd62Bejnet7ncp\n" +
            "wxOPt4W/9NJ/0KUPt+5TvOZqObPcU7B/BHKUOg+iUuFrQXn3EsaBeq7ThDfUFHue\n" +
            "vPeFoHLRPGYBjw5vyHeEyStrCNQHh10jKxtVgvBhh4sW6CSSVc2kR/O0QVwZWXXE\n" +
            "uoa1nNKCr/S/rpbHBI+afp1l+xxE9Qr9dfR1aiO0EHp6hrih9KonfDlyFhxT2arS\n" +
            "ZQEjNw0SG1d9lOpbFm6WCQFOyKkeEYHp5i4hwRrkTPrBu8YH/3LrN+lagRlHYAm5\n" +
            "H1ttXC6gL286PgWUrm5CHWYQFgFUY68cnHjhhQYzKi9+Q8ArDlP0856TTP2JGnHG\n" +
            "nEbSRMj8\n" +
            "=6E4m\n" +
            "-----END PGP MESSAGE-----";*/
}
