package co.gounplugged.unpluggeddroid.test.services;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.openintents.openpgp.IOpenPgpService;
import org.openintents.openpgp.util.OpenPgpServiceConnection;

import java.util.concurrent.CountDownLatch;

import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by Marvin Arnold on 10/06/15.
 */
public class OpenPGPBridgeServiceTest extends ServiceTestCase {

    public OpenPGPBridgeServiceTest() {
        super(OpenPGPBridgeService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), OpenPGPBridgeService.class);
        startService(startIntent);
    }

    public void doTestEncrypt() {
        Intent encryptIntent = new Intent();
        encryptIntent.setAction(OpenPGPBridgeService.ACTION_ENCRYPT);
        encryptIntent.putExtra(OpenPGPBridgeService.EXTRA_PLAINTEXT, "cat");
        encryptIntent.setClass(getContext(), OpenPGPBridgeService.class);
        startService(encryptIntent);
    }

    public class TestOpenPGPBridgeService extends OpenPGPBridgeService {
        @Override
        public void onCreate() {
            Log.d(TAG, "Created");

            isBound = false;

            mServiceConnection = new OpenPgpServiceConnection(
                    getApplicationContext(),
                    "org.sufficientlysecure.keychain",
                    new OpenPgpServiceConnection.OnBound() {
                        @Override
                        public void onBound(IOpenPgpService service) {
                            isBound = true;
                            doTestEncrypt();
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
    }

}
