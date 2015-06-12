package co.gounplugged.unpluggeddroid.models;

import android.content.Context;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidThrowException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ThrowParser;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";

    public String getEncryptedContent() {
        return mEncryptedContent;
    }

    private final String mEncryptedContent;
    private final OpenPGPBridgeService mOpenPGPBridgeService;

    public Mask getThrowTo() {
        return throwTo;
    }

    private final Mask throwTo;
    /*
        Use to originate a message
     */
    public Throw(
            String message,
            String originatorNumber,
            Krewe krewe,
            OpenPGPBridgeService openPGPBridgeService)
            throws OpenPGPBridgeService.EncryptionUnavailableException {

        this.throwTo = krewe.getNextMask();
        this.mOpenPGPBridgeService = openPGPBridgeService;
        this.mEncryptedContent = encryptedContentFor(message, originatorNumber, krewe);
    }

    /*
        Use when receiving a message from someone else
     */
    public Throw(String encryptedContent, OpenPGPBridgeService openPGPBridgeService) throws InvalidPhoneNumberException, InvalidThrowException {
        this.mOpenPGPBridgeService = openPGPBridgeService;
        if(!ThrowParser.isValidThrow(encryptedContent)) throw new InvalidThrowException("Message is not valid throw");
        encryptedContent = decryptContent(encryptedContent);
        this.throwTo = getNextMask(encryptedContent);
        this.mEncryptedContent = peelOffLayer(encryptedContent);
    }

    /*
        TODO: Add encryption
     */
    private String decryptContent(String encryptedContent) {
        return encryptedContent;
    }

    /*
        TODO: Add encryption
     */
    private String encryptedContentFor(
            String message,
            String originatorNumber,
            Krewe krewe)
            throws OpenPGPBridgeService.EncryptionUnavailableException {

        return ThrowParser.contentFor(message, originatorNumber, krewe, mOpenPGPBridgeService);
    }

    private Mask getNextMask(String decryptedContent) throws InvalidPhoneNumberException {
        if(ThrowParser.isValidRelayThrow(decryptedContent)) {
            String nextPhoneNumber = ThrowParser.getNextMask(decryptedContent);
            return new Mask(nextPhoneNumber);
        } else {
            return null;
        }
    }

    private String peelOffLayer(String receivedThrowContent) {
        if(hasArrived()) {
            return receivedThrowContent;
        } else {
            return ThrowParser.removeNextMask(receivedThrowContent);
        }
    }

    public boolean hasArrived() {
        return throwTo == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Throw))
            return false;
        if (obj == this)
            return true;

        Throw rhs = (Throw) obj;
        return mEncryptedContent.equals(rhs.getEncryptedContent());
    }

    public Contact getThrowOriginator(Context context)
            throws InvalidPhoneNumberException, PrematureReadException, NotFoundInDatabaseException {
        if(!hasArrived()) throw new PrematureReadException("Only the ultimate recipient may read original sender");
        return ContactUtil.getContact(context, ThrowParser.getOriginatorNumber(mEncryptedContent));
    }

    public String getDecryptedContent() throws PrematureReadException {
        if(!hasArrived()) throw new PrematureReadException("Only the ultimate recipient may read original content");
        return ThrowParser.getMessage(mEncryptedContent);
    }
}

