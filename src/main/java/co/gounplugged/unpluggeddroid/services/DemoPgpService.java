package co.gounplugged.unpluggeddroid.services;

import android.os.Binder;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;

/**
 * Created by Marvin Arnold on 23/06/15.
 */
public class DemoPgpService extends OpenPGPBridgeService {
    public class LocalBinder extends Binder {
        public OpenPGPBridgeService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DemoPgpService.this;
        }
    }

    public String decrypt(String ciphertext) throws EncryptionUnavailableException {
        return ciphertext;
    }

    public String encrypt(String plaintext, String recipientAddress) throws EncryptionUnavailableException {
        return plaintext;
    }
}
