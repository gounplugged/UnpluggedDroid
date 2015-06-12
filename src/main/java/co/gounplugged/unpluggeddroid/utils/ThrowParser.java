package co.gounplugged.unpluggeddroid.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by pili on 10/04/15.
 */
public class ThrowParser {
    public final static String MASK_SEPARATOR = "zQpQQ";
    public final static String MESSAGE_SEPARATOR = "WIxff";
    public final static String ORIGINATOR_SEPARATOR = "YzLqQ";

    private final static String THROW_REGEX = "(.*" + MESSAGE_SEPARATOR + ")(" +
    PhoneNumberParser.PHONE_NUMBER_REGEX + ORIGINATOR_SEPARATOR + ")";

    public static String getNextMask(String content) {
        return content.split(MASK_SEPARATOR)[0];
    }

    /**
     * Is this formatted to be thrown to somebody else?
     * @param partiallyEncryptedContent
     * @return
     */
    public static boolean isValidRelayThrow(String partiallyEncryptedContent){
        return partiallyEncryptedContent.matches(
                "(" + PhoneNumberParser.PHONE_NUMBER_REGEX + MASK_SEPARATOR +
                 ".*");
    }

    /**
     * Is this thing received from somebody else valid?
     * @param encryptedContent
     * @return
     */
    public static boolean isValidThrow(String encryptedContent){
        return encryptedContent.matches(
                THROW_REGEX
        );
    }

    public static String contentFor(String message,
                                    String originatorNumber,
                                    Krewe krewe,
                                    OpenPGPBridgeService openPGPBridgeService)
                                    throws OpenPGPBridgeService.EncryptionUnavailableException{

        List<Mask> masks = krewe.getMasks();

        String recipientThrow = openPGPBridgeService.encrypt(
            message + ThrowParser.MESSAGE_SEPARATOR + originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR,
            krewe.getRecipientNumber()
        );

        String penultimateThrow = openPGPBridgeService.encrypt(
           krewe.getRecipientNumber() + ThrowParser.MASK_SEPARATOR + recipientThrow,
           masks.get(masks.size() - 1).getFullNumber()
        );

        String previousThrow = penultimateThrow;

        // Don't include first mask in content.
        for(int i = masks.size()-1; i > 0; i--) {
            previousThrow = openPGPBridgeService.encrypt(
                masks.get(i).getFullNumber() + ThrowParser.MASK_SEPARATOR + previousThrow,
                masks.get(i-1).getFullNumber()
            );
        }

        return  previousThrow;
    }

    public static String removeNextMask(String content) {
        return content.replaceFirst(PhoneNumberParser.PHONE_NUMBER_REGEX + MASK_SEPARATOR, "");
    }

    public static String getOriginatorNumber(String content) {
        Matcher m = Pattern.compile("(.*)(" + MESSAGE_SEPARATOR +")(" + PhoneNumberParser.PHONE_NUMBER_REGEX +")(.*)").matcher(content);
        m.matches();
        return m.group(3);
    }

    public static String getMessage(String content) {
        Matcher m = Pattern.compile("(.*)(" + MESSAGE_SEPARATOR +")(.*)").matcher(content);
        m.matches();
        return m.group(1);
    }
}
