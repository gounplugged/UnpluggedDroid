package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by Marvin Arnold on 11/07/15.
 */
public class TerminatingBuilderThrow extends Throw {
    public final static String TERMINATING_BUILDER_THROW_IDENTIFIER = "XZyQy";

    /**
     * The terminating throw contains the originator's phone number encrypted with the key
     * of the terminating recipient.
     * @param terminatingPhoneNumber
     * @param originatorPhoneNumber
     * @param adjacentMask
     * @param openPGPBridgeService
     */
    public TerminatingBuilderThrow(
            String originatorPhoneNumber,
            String terminatingPhoneNumber,
            Mask adjacentMask,
            OpenPGPBridgeService openPGPBridgeService)
            throws EncryptionUnavailableException {

        super(adjacentMask);
        setContent(openPGPBridgeService.encrypt(
                TERMINATING_BUILDER_THROW_IDENTIFIER + originatorPhoneNumber,
                terminatingPhoneNumber));
    }

    public static boolean isValidTerminatingBuilderThrow(String unencryptedContent) {
        return unencryptedContent.contains(
//                        "^" +
                        TERMINATING_BUILDER_THROW_IDENTIFIER //+
//                        PhoneNumberParser.PHONE_NUMBER_REGEX +
//                        ".*"
        );
    }

    public static String getTrueOriginatorNumber(String unencryptedContent) {
        // Remove identifier, the rest is the number
        return unencryptedContent.replaceFirst(TERMINATING_BUILDER_THROW_IDENTIFIER, "");
    }
}
