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
public class OpenPGPBridgeServiceTest extends ServiceTestCase<OpenPGPBridgeService> {
    private static final String TAG = "PGPBridgeServiceTest";

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

//    public void testEncrypt() {
//        Intent encryptIntent = new Intent();
//        encryptIntent.setAction(OpenPGPBridgeService.ACTION_ENCRYPT);
//        encryptIntent.putExtra(OpenPGPBridgeService.EXTRA_PLAINTEXT, "test");
//        encryptIntent.putExtra(OpenPGPBridgeService.EXTRA_RECIPIENT, "marvin@gounplugged.co");
//        encryptIntent.setClass(getContext(), OpenPGPBridgeService.class);
//        startService(encryptIntent);
//    }


}
