package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ThrowParser;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";
    private final static int STATE_UNINITIALIZED = 0;
    private final static int STATE_READY_TO_THROW = 1;
    private final static int STATE_RECEIVED = 2; // if received a throw and could only decrypt a layer
    private final static int STATE_AT_DESTINATION = 3;
    private int mState = STATE_UNINITIALIZED;

    private final String mThrowToAddress;
    private final String mContent;
    private final OpenPGPBridgeService mOpenPGPBridgeService;

    /**
     *
     * @return content to be thrown over wire or decrypted message.
     */
    public String getContent() throws InvalidStateException {
        if(mState == STATE_READY_TO_THROW || mState == STATE_AT_DESTINATION) return mContent;
        throw new InvalidStateException("Throw not ready to be read");
    }

    /**
     *
     * @return address of next Mask in Second Line.
     */
    public String getThrowToAddress() throws InvalidStateException {
        if(mState == STATE_READY_TO_THROW) return mThrowToAddress;
        throw new InvalidStateException("Throw not ready to be thrown");
    }

    public int getState() {
        return this.mState;
    }

    public boolean isAtDestination() {
        return this.mState == STATE_AT_DESTINATION;
    }

    public boolean isRelay() {
        return this.mState == STATE_READY_TO_THROW;
    }

    /**
     * Use this if generating a new message as originator.
     * @param message
     * @param originatorNumber
     * @param krewe
     * @param openPGPBridgeService
     * @throws OpenPGPBridgeService.EncryptionUnavailableException
     * @throws ThrowParser.KreweException
     */
    public Throw(
            String message,
            String originatorNumber,
            Krewe krewe,
            OpenPGPBridgeService openPGPBridgeService)
            throws OpenPGPBridgeService.EncryptionUnavailableException,
            ThrowParser.KreweException {

        this.mThrowToAddress = krewe.getNextMask().getFullNumber();
        this.mOpenPGPBridgeService = openPGPBridgeService;
        this.mContent = encryptedContentFor(message, originatorNumber, krewe);
        this.mState = STATE_READY_TO_THROW;
    }

    /**
     * When receiving a message from somebody else.
     * @param encryptedContent
     * @param openPGPBridgeService
     * @throws InvalidPhoneNumberException
     * @throws InvalidThrowException
     */
    public Throw(
            String encryptedContent,
            OpenPGPBridgeService openPGPBridgeService)
            throws InvalidPhoneNumberException, InvalidThrowException {

        this.mState = STATE_RECEIVED;
        this.mOpenPGPBridgeService = openPGPBridgeService;
        if(!ThrowParser.isValidThrow(encryptedContent)) throw new InvalidThrowException("Message is not formatted like a throw");
        String partiallyDecryptedContent = decryptContent(encryptedContent);

        if(ThrowParser.isFullyDecrypted(partiallyDecryptedContent)) {
            this.mThrowToAddress = null;
            this.mContent = ThrowParser.getMessage(partiallyDecryptedContent);
            this.mState = STATE_AT_DESTINATION;
        } else {
            this.mThrowToAddress = ThrowParser.getNextMaskAddress(partiallyDecryptedContent);
            this.mContent = ThrowParser.contentFor(partiallyDecryptedContent);
            this.mState =  STATE_READY_TO_THROW;
        }
    }

    /**
     *
     * @param encryptedContent
     * @return
     */
    private String decryptContent(String encryptedContent) {
        return encryptedContent;
    }

    /**
     *
     * @param message
     * @param originatorNumber
     * @param krewe
     * @return
     * @throws OpenPGPBridgeService.EncryptionUnavailableException
     * @throws ThrowParser.KreweException
     */
    private String encryptedContentFor(
            String message,
            String originatorNumber,
            Krewe krewe)
            throws OpenPGPBridgeService.EncryptionUnavailableException,
            ThrowParser.KreweException {

        return ThrowParser.contentFor(message, originatorNumber, krewe, mOpenPGPBridgeService);
    }

    public String getOriginatorAddress() throws InvalidStateException {
        if(mState == STATE_AT_DESTINATION) return ThrowParser.getOriginatorNumber(mContent);
        throw new InvalidStateException("Throw is not at its destination. Unable to read originating address");
    }

    public class InvalidStateException extends Exception {
        public InvalidStateException(String message) {
            super(message);
        }
    }

    public class InvalidThrowException extends Exception {
        public InvalidThrowException(String message) {
            super(message);
        }
    }
}

