package co.gounplugged.unpluggeddroid.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by pili on 10/04/15.
 */
public class ThrowParser {
    public final static String THROW_IDENTIFIER = "qZYZqQQQwZZqfQ";
    public final static String MASK_SEPARATOR = "zQpQQ";
    public final static String MESSAGE_SEPARATOR = "WIxff";
    public final static String ORIGINATOR_SEPARATOR = "YzLqQ";
    private final static int MIN_NUM_RELAY_MASKS = 1;

    private final static String THROW_REGEX = "(.*" + MESSAGE_SEPARATOR + ")(" +
    PhoneNumberParser.PHONE_NUMBER_REGEX + ORIGINATOR_SEPARATOR + ")";

    public static String getNextMaskAddress(String partiallyDecryptedContent) {
        return partiallyDecryptedContent.split(MASK_SEPARATOR)[0];
    }

    /**
     * Throws are encrypted and prepended with throw identifier.
     * @param encryptedContent
     * @return
     */
    public static boolean isValidThrow(String encryptedContent){
        // TODO should look like .matches("^" + THROW_IDENTIFIER + ".*") but, IDK
        return encryptedContent.contains(
                THROW_IDENTIFIER
        );
    }

    /**
     * If all layers have been decrypted, can read message and originator.
     * @param partiallyDecryptedContent
     * @return
     */
    public static boolean isFullyDecrypted(String partiallyDecryptedContent){
        return partiallyDecryptedContent.matches(
                THROW_REGEX
        );
    }

    /**
     * Encrypts message and originator number in sucessive layers.
     * Starting with the ultimate recipient.
     * Then working backwards to the first Mask in the Krewe.
     * @param message
     * @param originatorNumber
     * @param krewe
     * @param openPGPBridgeService
     * @return encrypted String
     * @throws EncryptionUnavailableException
     * @throws KreweException
     */
    public static String contentFor(String message,
                                    String originatorNumber,
                                    Krewe krewe,
                                    OpenPGPBridgeService openPGPBridgeService)
                                    throws EncryptionUnavailableException,
                                    KreweException {

        if(krewe.getMasks().size() < MIN_NUM_RELAY_MASKS) throw new KreweException("Additional Masks required");
        List<Mask> masks = krewe.getMasks();

        String recipientThrow = openPGPBridgeService.encrypt(
            message + MESSAGE_SEPARATOR + originatorNumber + ORIGINATOR_SEPARATOR,
            krewe.getRecipientNumber()
        );

        String penultimateThrow = openPGPBridgeService.encrypt(
           krewe.getRecipientNumber() + MASK_SEPARATOR + recipientThrow,
           masks.get(masks.size() - 1).getFullNumber()
        );

        String previousThrow = penultimateThrow;

        // Don't include first mask in content.
        for(int i = masks.size()-1; i > 0; i--) {
            previousThrow = openPGPBridgeService.encrypt(
                masks.get(i).getFullNumber() + MASK_SEPARATOR + previousThrow,
                masks.get(i-1).getFullNumber()
            );
        }

        return  THROW_IDENTIFIER + previousThrow;
    }

    /**
     * Remove the decrypted parts (the mask and separators). Add the Throw identifier
     * @param partiallyDecryptedContent
     * @return
     */
    public static String contentFor(String partiallyDecryptedContent) {
        return  THROW_IDENTIFIER + partiallyDecryptedContent.replaceFirst(PhoneNumberParser.PHONE_NUMBER_REGEX + MASK_SEPARATOR, "");
    }

    public static String getOriginatorNumber(String fullyDecryptedContent) {
        Log.d("ThrowParser", "orignator number from: " + fullyDecryptedContent);
        Matcher m = Pattern.compile("(.*)(" + MESSAGE_SEPARATOR +")(" + PhoneNumberParser.PHONE_NUMBER_REGEX +")(.*)").matcher(fullyDecryptedContent);
        m.matches();
        return m.group(3);
    }

    public static String getMessage(String fullyDecryptedContent) {
        Matcher m = Pattern.compile("(.*)(" + MESSAGE_SEPARATOR +")(.*)").matcher(fullyDecryptedContent);
        m.matches();
        return m.group(1);
    }

    public static class KreweException extends Exception {
        public KreweException(String message) {
            super(message);
        }
    }
}
