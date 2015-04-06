package co.gounplugged.unpluggeddroid.models;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";
    public final static String MASK_SEPERATOR = "zQpQQ";
    public final static String COUNTRY_CODE_SEPERATOR = "VwvaQ";
    public final static String MESSAGE_SEPERATOR = "WIxff";

    public String getEncryptedContent() {
        return encryptedContent;
    }

    private final String encryptedContent;

    public Mask getThrowTo() {
        return throwTo;
    }

    private final Mask throwTo;

    public Throw(String message, Krewe maskRoute) {
        this.throwTo = maskRoute.nextMask();
        this.encryptedContent = encryptedContentFor(message, maskRoute);
    }

    public Throw(String encryptedContent) {
        encryptedContent = decryptContent(encryptedContent);
        this.throwTo = getNextMask(encryptedContent);
        this.encryptedContent = peelOffLayer(encryptedContent);
    }

    private String decryptContent(String encryptedContent) {
        return encryptedContent;
    }

    private String encryptedContentFor(String message, Krewe krewe) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean skippedFirst = false;
        for(Mask m : krewe.getMasks()) {
            if(skippedFirst) {
                stringBuilder.append(m.getPhoneNumber());
                stringBuilder.append(MASK_SEPERATOR);
            } else {
                skippedFirst = true;
            }
        }

        stringBuilder.append(message);
        stringBuilder.append(MESSAGE_SEPERATOR);

        return  stringBuilder.toString();
    }

    private Mask getNextMask(String decryptedContent) {
        String nextPhoneNumber;
        Log.d(TAG, "Has throw arrived: " + String.valueOf(Throw.isValidWThrowTo(decryptedContent)));
        if(Throw.isValidWThrowTo(decryptedContent)) {
            nextPhoneNumber = decryptedContent.split(MASK_SEPERATOR)[0];
        } else {
            nextPhoneNumber = null;
        }
        return new Mask(nextPhoneNumber, Contact.DEFAULT_COUNTRY_CODE);
    }

    private static boolean isValidWThrowTo(String decryptedContent){
        return decryptedContent.matches("(\\d+" + MASK_SEPERATOR + ")+\\w+" + MESSAGE_SEPERATOR);
    }

    private String peelOffLayer(String receivedThrowContent) {
        String r = "\\d+" + MASK_SEPERATOR;
        Log.d(TAG, String.valueOf(receivedThrowContent.matches(r)));
        if(throwTo.hasArrived()) {
            return receivedThrowContent.split(MESSAGE_SEPERATOR)[0];
        } else {
            return receivedThrowContent.replaceFirst(r, "");
        }
    }

    public boolean hasArrived() {
        return throwTo.hasArrived();
    }
}
