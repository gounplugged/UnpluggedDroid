package co.gounplugged.unpluggeddroid.models;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";
    private final static String MASK_SEPERATOR = "zQpQQ";
    private final static String MESSAGE_SEPERATOR = "*+#";

    public String getEncryptedContent() {
        return encryptedContent;
    }

    private final String encryptedContent;
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

        for(Mask m : krewe.getMasks()) {
            stringBuilder.append(m.getPhoneNumber());
            stringBuilder.append(MASK_SEPERATOR);
        }

        stringBuilder.append(message);
        stringBuilder.append(MESSAGE_SEPERATOR);

        return  stringBuilder.toString();
    }

    private Mask getNextMask(String decryptedContent) {
        String nextPhoneNumber = decryptedContent.split(MASK_SEPERATOR)[0];
        return new Mask(nextPhoneNumber);
    }

    private String peelOffLayer(String receivedThrowContent) {
        Log.d(TAG, String.valueOf(receivedThrowContent.matches("\\d" + MASK_SEPERATOR)));
        return receivedThrowContent.replaceFirst("\\d" + MASK_SEPERATOR, "");
    }
}
