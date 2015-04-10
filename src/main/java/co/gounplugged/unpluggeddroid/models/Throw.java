package co.gounplugged.unpluggeddroid.models;

import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";
    public final static String MASK_SEPARATOR = "zQpQQ";
    public final static String COUNTRY_CODE_SEPARATOR = "VwvaQ";
    public final static String MESSAGE_SEPARATOR = "WIxff";
    public final static String ORIGINATOR_SEPARATOR = "YzLqQ";

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
        StringBuilder stringBuilder = new StringBuilder();

        for(Mask m : krewe.getMasks()) {
            stringBuilder.append(m.getCountryCode());
            stringBuilder.append(COUNTRY_CODE_SEPARATOR);
            stringBuilder.append(m.getPhoneNumber());
            stringBuilder.append(MASK_SEPARATOR);
        }

        stringBuilder.append(message);
        stringBuilder.append(MESSAGE_SEPARATOR);

        stringBuilder.append(originatorNumber);
        stringBuilder.append(ORIGINATOR_SEPARATOR);

        return  stringBuilder.toString();
    }

    private Mask getNextMask(String decryptedContent) throws InvalidPhoneNumberException {
        String nextPhoneNumber;
        String nextCountryCode;
        Log.d(TAG, "Has throw arrived: " + String.valueOf(Throw.isValidWThrowTo(decryptedContent)));
        if(Throw.isValidWThrowTo(decryptedContent)) {
            String fullNumber = decryptedContent.split(MASK_SEPARATOR)[0];
            nextCountryCode = fullNumber.split(COUNTRY_CODE_SEPARATOR)[0];
            nextPhoneNumber = fullNumber.split(COUNTRY_CODE_SEPARATOR)[1];
        } else {
            nextPhoneNumber = null;
            nextCountryCode = null;
        }
        return new Mask(nextCountryCode+nextPhoneNumber);
    }

    private static boolean isValidWThrowTo(String decryptedContent){
        return decryptedContent.matches(
                "(\\+\\d{1,3}" + COUNTRY_CODE_SEPARATOR + "\\d+" + MASK_SEPARATOR +
                ")+\\w+" + MESSAGE_SEPARATOR +
                "\\+\\d+" + ORIGINATOR_SEPARATOR
        );
    }

    private String peelOffLayer(String receivedThrowContent) {
        String r = "\\+\\d{1,3}" + COUNTRY_CODE_SEPARATOR + "\\d+" + MASK_SEPARATOR;
        Log.d(TAG, String.valueOf(receivedThrowContent.matches(r)));
        if(throwTo.hasArrived()) {
            return receivedThrowContent.split(MESSAGE_SEPARATOR)[0];
        } else {
            return receivedThrowContent.replaceFirst(r, "");
        }
    }

    public boolean hasArrived() {
        return throwTo.hasArrived();
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

    public Contact getThrownFrom() throws InvalidPhoneNumberException {
        String thrownFrom = encryptedContent;
        thrownFrom = thrownFrom.replaceAll(".*"+MESSAGE_SEPARATOR, "");
        thrownFrom = thrownFrom.replace(ORIGINATOR_SEPARATOR, "");
        return new Contact("TODO in Throw", thrownFrom);
    }
}
