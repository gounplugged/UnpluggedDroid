package co.gounplugged.unpluggeddroid.models;

import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";

    public String getEncryptedContent() {
        return encryptedContent;
    }

    private final String encryptedContent;

    public Mask getThrowTo() {
        return throwTo;
    }

    private final Mask throwTo;
    /*
        Use to originate a message
     */
    public Throw(String message, String originatorNumber, Krewe maskRoute) {
        this.throwTo = maskRoute.popNextMask();
        this.encryptedContent = encryptedContentFor(message, originatorNumber, maskRoute);
    }

    /*
        Use when receiving a message from someone else
     */
    public Throw(String encryptedContent) throws InvalidPhoneNumberException {
        encryptedContent = decryptContent(encryptedContent);
        this.throwTo = getNextMask(encryptedContent);
        this.encryptedContent = peelOffLayer(encryptedContent);
    }

    private String decryptContent(String encryptedContent) {
        return encryptedContent;
    }

    private String encryptedContentFor(String message, String originatorNumber, Krewe krewe) {
        return ThrowParser.contentFor(message, originatorNumber, krewe);
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
        return encryptedContent.equals(rhs.getEncryptedContent());
    }

    public Contact getThrownFrom() throws InvalidPhoneNumberException, PrematureReadException {
        if(!hasArrived()) throw new PrematureReadException("Only the ultimate recipient may read original sender");
        return new Contact("TODO in Throw", ThrowParser.getOriginatorNumber(encryptedContent));
    }

    public String getDecryptedContent() throws PrematureReadException {
        if(!hasArrived()) throw new PrematureReadException("Only the ultimate recipient may read original content");
        return ThrowParser.getMessage(encryptedContent);
    }
}
