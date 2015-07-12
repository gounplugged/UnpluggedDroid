package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;

/**
 * Created by Marvin Arnold on 11/07/15.
 */
public class BuilderThrow extends Throw {
    public final static String BUILDER_THROW_IDENTIFIER = "QYYzYQ";


    /**
     * Contains the next Mask to be added to the Krewe. Encrypted with secondToNextPhoneNumber's key.
     * @param nextPhoneNumber
     * @param secondToNextPhoneNumber
     * @param adjacentMask
     * @param openPGPBridgeService
     */
    public BuilderThrow(
            String nextPhoneNumber,
            String secondToNextPhoneNumber,
            Mask adjacentMask,
            OpenPGPBridgeService openPGPBridgeService) {

        super(BUILDER_THROW_IDENTIFIER + nextPhoneNumber, adjacentMask);
    }

    public static boolean isValidBuilderThrow(String unencryptedContent) {
        return unencryptedContent.matches("^" + BUILDER_THROW_IDENTIFIER + PhoneNumberParser.PHONE_NUMBER_REGEX + "$");
    }

    public static String getThrowToNumber(String unencryptedContent) {
        // Remove identifier, the rest is the number
        return unencryptedContent.replaceFirst("^" + BUILDER_THROW_IDENTIFIER, "");
    }
}