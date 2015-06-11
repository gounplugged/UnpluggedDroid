package co.gounplugged.unpluggeddroid.test.services;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.concurrent.CountDownLatch;

import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by Marvin Arnold on 10/06/15.
 */
public class OpenPGPBridgeServiceTest extends ServiceTestCase {
    final CountDownLatch signal = new CountDownLatch(1);
    public OpenPGPBridgeServiceTest() {
        super(OpenPGPBridgeService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();


    }

    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), OpenPGPBridgeService.class);
        startService(startIntent);
    }

    public void testEncrypt() {
        Intent encryptIntent = new Intent();
        encryptIntent.setAction(OpenPGPBridgeService.ACTION_ENCRYPT);
        encryptIntent.putExtra(OpenPGPBridgeService.EXTRA_PLAINTEXT, "cat");
        encryptIntent.setClass(getContext(), OpenPGPBridgeService.class);
        startService(encryptIntent);
    }
}
