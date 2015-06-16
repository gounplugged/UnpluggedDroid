package co.gounplugged.unpluggeddroid.managers;


import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.List;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.SecondLine;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ThrowParser;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import co.gounplugged.unpluggeddroid.utils.MessageUtil;
import co.gounplugged.unpluggeddroid.utils.SMSUtil;
import de.greenrobot.event.EventBus;

public class ThrowManager {

    public static final String TAG = "ThrowManager";

    private Context mContext;

    public ThrowManager(Context context) {
        mContext = context;
    }

    /**
     * Process incoming SMS messages as throw or regular sms
     * @param lastSMSInBundle
     * @param concatenatedText
     */
    public void processUnknownSMS(SmsMessage lastSMSInBundle, String concatenatedText) {
        Log.d(TAG, "Received text: " + concatenatedText);
        try {
            Throw receivedThrow = new Throw(concatenatedText, ((BaseApplication) mContext).getOpenPGPBridgeService());
            processThrow(receivedThrow);
            Log.d(TAG, "processed as throw");
        }  catch (Throw.InvalidThrowException e) {
            processRegularSMS(lastSMSInBundle, concatenatedText);
            Log.d(TAG, "processed as SMS");
        } catch (InvalidPhoneNumberException e) {
            //TODO recover from problem to ensure message delivery
            Log.d(TAG, "process interrupted for invalid phone number");
        } catch (EncryptionUnavailableException e) {
            // TODO first assume regular SMS. If fail, wait for decryption then try again.
            Log.d(TAG, "processed interuppted because no encryption available");
        }
    }

    /**
     * Decide whether to throw again or process as received message.
     * @param receivedThrow
     */
    private void processThrow(Throw receivedThrow) {
        try {
            String content = receivedThrow.getContent();
            if (receivedThrow.isAtDestination()) {
                // Throw reached its final destination.
                processThrowAtDestination(receivedThrow);
                Log.d(TAG, "processed throw at destination");
            } else if (receivedThrow.isRelay()) {
                // Just a relay. Throw to next Mask.
                SMSUtil.sendSms(receivedThrow.getThrowToAddress(), content);
                Log.d(TAG, "processed throw as relay");
            } else {
                // Should never be the case.
            }
        } catch (Throw.InvalidStateException e) {
            // Received a weird throw. Ignore.
        }
    }

    public void processThrowAtDestination(Throw throwAtDestination) {
        try {
            String originatorAddress = throwAtDestination.getOriginatorAddress();
            Contact originator = ContactUtil.firstOrCreate(mContext, originatorAddress, originatorAddress);
            Conversation conversation = ConversationUtil.findOrNew(originator, mContext);
            String receivedText = ThrowParser.getMessage(throwAtDestination.getContent());
            addTextToConversation(receivedText, conversation);
        } catch (Throw.InvalidStateException e) {
            // Should never be here
        } catch (InvalidPhoneNumberException e) {
            // TODO throw was created with a bad from address. Reply impossible. Display anyways?
            // For now, ignore.
        } catch (Conversation.InvalidConversationException e) {
            // TODO Originator null
        }
    }

    /**
     * Find or create appropriate conversation for this message.
     * @param lastSMSInBundle
     * @param concatenatedText
     */
    private void processRegularSMS(SmsMessage lastSMSInBundle, String concatenatedText) {
        String originatingAddress = lastSMSInBundle.getOriginatingAddress();

        Contact participant;
        // Find or create Contact based on sender's phone number
        try { // Existing contact
            participant = ContactUtil.getContact(mContext, originatingAddress);
        } catch (NotFoundInDatabaseException e) { // New contact
            try {
                participant = ContactUtil.firstOrCreate(mContext, originatingAddress, originatingAddress);
            } catch (InvalidPhoneNumberException e1) {
                //TODO should not really be adding a contact, just a new conversation.
                // Should be able to have conversations not linked to a contact.
                return;
            }
        }

        boolean isSLMessage = MessageUtil.isSLCompatible(concatenatedText);
        Log.d(TAG, "Message tagged as SL compatible: " + isSLMessage);
        if(isSLMessage) {
            participant.setUsesSecondLine(mContext, isSLMessage);
            concatenatedText = MessageUtil.sanitizeSLCompatibilityText(concatenatedText);
        }

        // Find or create conversation with participant
        Conversation conversation = null;
        try {
            Log.d(TAG, "Participant tagged as SL compatible: " + participant.usesSecondLine());
            conversation = ConversationUtil.findOrNew(participant, mContext);
            Log.d(TAG, "Conversation tagged as SL compatible: " + conversation.isSecondLineComptabile());

        } catch (Conversation.InvalidConversationException e) {
            // TODO can participant ever be null?
        }

        addTextToConversation(concatenatedText, conversation);
    }

    /**
     * Send Unplugged message
     * @param conversation
     * @param text
     */
    public void sendMessage(Conversation conversation, String text, OpenPGPBridgeService openPGPBridgeService) {
        Message message = MessageUtil.create(
                mContext,
                conversation,
                text,
                Message.TYPE_OUTGOING,
                System.currentTimeMillis());

        EventBus.getDefault().postSticky(message);

        sendSMSOverWire(message, BaseApplication.getInstance(mContext).getKnownMasks(), openPGPBridgeService);
        Log.d(TAG, "Sending message: " + message);
    }


    /**
     * Prepare message to be sent over cell network.
     * @param message
     * @param knownMasks
     */
    private void sendSMSOverWire(Message message, List<Mask> knownMasks, OpenPGPBridgeService openPGPBridgeService) {
        String phoneNumber;
        String text;

        Conversation conversation = message.getConversation();

        // SL messages must be encrypted and wrapped in layers
        if(conversation.isSecondLineComptabile()) {
            SecondLine secondLine = conversation.getAndRefreshSecondLine(knownMasks);
            conversation.setCurrentSecondLine(secondLine);
            Throw t = null;
            try {
                t = secondLine.getThrow(message.getText(), Profile.getPhoneNumber(), openPGPBridgeService);
            } catch (EncryptionUnavailableException e) {
                // Encryption unavailable so just send normally
                phoneNumber = conversation.getParticipant().getFullNumber();
                message.mutateTextToShowSLCompatibility();
                text = message.getText();
                SMSUtil.sendSms(phoneNumber, text);
            } catch (ThrowParser.KreweException e) {
                // TODO make sure krewe is never too short
                return;
            }

            try {
                phoneNumber = t.getThrowToAddress();
                text = t.getContent();
                SMSUtil.sendSms(phoneNumber, text);
                return;
            } catch (Throw.InvalidStateException e) {
                // TODO something went wrong with encryption
                return;
            }

        } else { // Regular messages are mutated to indicate they were created with SL
            phoneNumber = conversation.getParticipant().getFullNumber();
            message.mutateTextToShowSLCompatibility();
            text = message.getText();
            SMSUtil.sendSms(phoneNumber, text);
        }
    }

    /**
     *
     * @param text
     * @param conversation
     */
    private void addTextToConversation(String text, Conversation conversation) {
        Message message = MessageUtil.create(
                mContext,
                conversation,
                text,
                Message.TYPE_INCOMING,
                System.currentTimeMillis());

        EventBus.getDefault().postSticky(message);
        Log.d(TAG, "Received message: " + message);
    }

}
