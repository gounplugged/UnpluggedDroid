package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by Marvin Arnold on 11/07/15.
 */
public class MessageThrow extends Throw {
    public final static String MESSAGE_THROW_IDENTIFIER = "XQYzYQ";


    /**
     * Encrypt content with terminatingRecipientNumber's key.
     * First hop through adjacentMask.
     * @param terminatingRecipientNumber
     * @param unencryptedContent
     * @param adjacentMask
     * @param openPGPBridgeService
     */
    public MessageThrow(
            String unencryptedContent,
            String terminatingRecipientNumber,
            Mask adjacentMask,
            OpenPGPBridgeService openPGPBridgeService)
            throws EncryptionUnavailableException {

        super(adjacentMask);
        setContent(openPGPBridgeService.encrypt(
                MESSAGE_THROW_IDENTIFIER + unencryptedContent,
                terminatingRecipientNumber));
    }

    public static boolean isValidMessageThrow(String encryptedContent) {
        return encryptedContent.contains(MESSAGE_THROW_IDENTIFIER);
    }

    public static String getContent(String unencryptedContent) {
        // Remove identifier, the rest is the number
        return unencryptedContent.replaceFirst(MESSAGE_THROW_IDENTIFIER, "");
    }
}
