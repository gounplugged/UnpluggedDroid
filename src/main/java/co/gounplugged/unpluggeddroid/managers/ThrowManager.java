package co.gounplugged.unpluggeddroid.managers;


import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.List;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
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
     * @param receivedSMS
     */
    public void processUnknownSMS(SmsMessage receivedSMS) {
        String receivedText = receivedSMS.getMessageBody().toString();
        Log.d(TAG, "Received text: " + receivedText);
        try {
            Throw receivedThrow = new Throw(receivedText, null);
            processThrow(receivedThrow);
        }  catch (Throw.InvalidThrowException e) {
            processRegularSMS(receivedSMS);
        } catch (InvalidPhoneNumberException e) {
            //TODO recover from problem to ensure message delivery
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
            } else if (receivedThrow.isRelay()) {
                // Just a relay. Throw to next Mask.
                SMSUtil.sendSms(receivedThrow.getThrowToAddress(), content);
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
     * @param receivedSMS
     */
    private void processRegularSMS(SmsMessage receivedSMS) {
        String originatingAddress = receivedSMS.getOriginatingAddress();
        String text = receivedSMS.getMessageBody();

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

        boolean isSLMessage = MessageUtil.isSLCompatible(text);
        Log.d(TAG, "Message tagged as SL compatible: " + isSLMessage);
        if(isSLMessage) {
            participant.setUsesSecondLine(mContext, isSLMessage);
            text = MessageUtil.sanitizeSLCompatibilityText(text);
        }

        // Find or create conversation with participant
        Conversation conversation = null;
        try {
            conversation = ConversationUtil.findOrNew(participant, mContext);
        } catch (Conversation.InvalidConversationException e) {
            // TODO can participant ever be null?
        }

        addTextToConversation(text, conversation);
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
            } catch (OpenPGPBridgeService.EncryptionUnavailableException e) {
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
