package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";
    public final static String THROW_IDENTIFIER = "qZYZqQQQwZZqfQ";
    private final static int STATE_UNINITIALIZED = 0;
    private final static int STATE_READY_TO_THROW = 1;
    private final static int STATE_RECEIVED = 2; // if received a throw and could only decrypt a layer
    private final static int STATE_AT_DESTINATION = 3;
    private int mState = STATE_UNINITIALIZED;

    private final String mAdjacentThrowAddres; // originator address when arrives
    private final String mContent;

    /**
     *
     * @return content to be thrown over wire or decrypted message.
     */
    public String getContent() {
        return mContent;
//        if(mState == STATE_READY_TO_THROW || mState == STATE_AT_DESTINATION) return mContent;
//        throw new InvalidStateException("Throw not ready to be read");
    }
//
    /**
     *
     * @return address of next Mask in Second Line.
     */
    public String getAdjacentThrowAddress() {
        return mAdjacentThrowAddres;
//        if(mState == STATE_READY_TO_THROW) return mThrowToAddress;
//        throw new InvalidStateException("Throw not ready to be thrown");
    }
//
//    public int getState() {
//        return this.mState;
//    }
//
//    public boolean isAtDestination() {
//        return this.mState == STATE_AT_DESTINATION;
//    }
//
//    public boolean isRelay() {
//        return this.mState == STATE_READY_TO_THROW;
//    }

//    /**
//     * Use this if generating a new message as originator.
//     * @param message
//     * @param originatorNumber
//     * @param krewe
//     * @param openPGPBridgeService
//     * @throws EncryptionUnavailableException
//     * @throws ThrowParser.KreweException
//     */
//    public Throw(
//            String message,
//            String originatorNumber,
//            OpenPGPBridgeService openPGPBridgeService)
//            throws EncryptionUnavailableException {
//
//        this.mOpenPGPBridgeService = openPGPBridgeService;
//        assertEncryption();
//        this.mThrowToAddress = krewe.getNextMask().getFullNumber();
//        this.mContent = encryptedContentFor(message, originatorNumber, krewe);
//        this.mState = STATE_READY_TO_THROW;
//    }

//    /**
//     * When receiving a message from somebody else.
//     * @param encryptedContent
//     * @param openPGPBridgeService
//     * @throws InvalidPhoneNumberException
//     * @throws InvalidThrowException
//     */
//    public Throw(
//            String encryptedContent,
//            Mask recipientMask,
//            OpenPGPBridgeService openPGPBridgeService)
//            throws InvalidPhoneNumberException,
//            InvalidThrowException,
//            EncryptionUnavailableException {
//
//
//        this.mState = STATE_RECEIVED;
//        this.mOpenPGPBridgeService = openPGPBridgeService;
//        assertEncryption();
//        if(!ThrowParser.isValidThrow(encryptedContent)) throw new InvalidThrowException("Message is not formatted like a throw");
//        String partiallyDecryptedContent = openPGPBridgeService.decrypt(encryptedContent);
//
//        if(ThrowParser.isFullyDecrypted(partiallyDecryptedContent)) {
//            this.mThrowToAddress = ThrowParser.getOriginatorNumber(partiallyDecryptedContent);
//            this.mContent = ThrowParser.getMessage(partiallyDecryptedContent);
//            this.mState = STATE_AT_DESTINATION;
//        } else {
//            this.mThrowToAddress = ThrowParser.getNextMaskAddress(partiallyDecryptedContent);
//            this.mContent = ThrowParser.contentFor(partiallyDecryptedContent);
//            this.mState =  STATE_READY_TO_THROW;
//        }
//    }

//    private void assertEncryption() throws EncryptionUnavailableException {
//        if(mOpenPGPBridgeService == null) throw new EncryptionUnavailableException("null");
//    }
//
//    /**
//     *
//     * @param message
//     * @param originatorNumber
//     * @param krewe
//     * @return
//     * @throws EncryptionUnavailableException
//     */
//    private String encryptedContentFor(
//            String message,
//            String originatorNumber,
//            Krewe krewe)
//            throws EncryptionUnavailableException {
//
//        return ThrowParser.contentFor(message, originatorNumber, krewe, mOpenPGPBridgeService);
//    }
//
//    public String getOriginatorAddress() throws InvalidStateException {
//        if(mState == STATE_AT_DESTINATION) return this.mThrowToAddress;
//        throw new InvalidStateException("Throw is not at its destination. Unable to read originating address");
//    }

    public Throw(String encryptedContent, Mask adjacentMask) {
        this.mContent = THROW_IDENTIFIER + encryptedContent;
        this.mAdjacentThrowAddres = adjacentMask.getFullNumber();
    }

    public Throw(String encryptedContent, String receivedFromAddress, OpenPGPBridgeService openPGPBridgeService)
            throws InvalidThrowException, EncryptionUnavailableException {
        if(!isValidThrow(encryptedContent)) throw new InvalidThrowException("Message is not formatted like a throw");

        encryptedContent = decrypt(encryptedContent, receivedFromAddress, openPGPBridgeService);
    }

    public static boolean isValidThrow(String encryptedContent) {
        return encryptedContent.matches("^" + THROW_IDENTIFIER + ".*");
    }

    public String decrypt(String encryptedContent, String receivedFromAddress, OpenPGPBridgeService openPGPBridgeService)
        throws EncryptionUnavailableException {
        return encryptedContent;
    }
//    public class InvalidStateException extends Exception {
//        public InvalidStateException(String message) {
//            super(message);
//        }
//    }
//
    public class InvalidThrowException extends Exception {
        public InvalidThrowException(String message) {
            super(message);
        }
    }

}

