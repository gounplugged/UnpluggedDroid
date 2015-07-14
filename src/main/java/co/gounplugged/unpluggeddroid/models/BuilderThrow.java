package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

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
            OpenPGPBridgeService openPGPBridgeService)
            throws EncryptionUnavailableException {

        super(adjacentMask);
        setContent(openPGPBridgeService.encrypt(
                BUILDER_THROW_IDENTIFIER + nextPhoneNumber,
                secondToNextPhoneNumber));
    }



    public static boolean isValidBuilderThrow(String unencryptedContent) {
        return unencryptedContent.contains(BUILDER_THROW_IDENTIFIER);
    }

    public static String getThrowToNumber(String unencryptedContent) {
        // Remove identifier, the rest is the number
        return unencryptedContent.replaceFirst(BUILDER_THROW_IDENTIFIER, "");
    }
}