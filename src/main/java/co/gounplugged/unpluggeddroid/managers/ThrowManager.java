package co.gounplugged.unpluggeddroid.managers;


import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.List;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidThrowException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.SecondLine;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.models.ThrowParser;
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
            Throw receivedThrow = new Throw(receivedText);
            processThrow(mContext, receivedThrow);
        }  catch (InvalidThrowException e) {
            processRegularSMS(receivedSMS);
        } catch (InvalidPhoneNumberException e) {
            //TODO recover from problem to ensure message delivery
        }
    }

    /**
     * Decrypt Throw and either send to next person if relay or add to messages if recipient.
     * @param context
     * @param receivedThrow
     */
    private void processThrow(Context context, Throw receivedThrow) {
        if(!receivedThrow.hasArrived()) { // Being used as relay Mask. Throw again.
            String nextText = receivedThrow.getEncryptedContent();
            SMSUtil.sendSms(receivedThrow.getThrowTo().getFullNumber(), nextText);
        } else { // Message has received at ultimate recipient
            try {
                Contact originator = receivedThrow.getThrowOriginator(context);
                Conversation conversation = ConversationUtil.findOrNew(originator, context);
                receiveThrow(receivedThrow, conversation);
            } catch (PrematureReadException e) { // Tried reading message even though not ultimate recipient

            } catch (NotFoundInDatabaseException e) { // Sender not a known contact
                Log.e(TAG, "Contact not found");
            } catch (InvalidPhoneNumberException e) { // Sender number malformed
                //TODO recover from problem to ensure message delivery
                Log.e(TAG, "Invalid phone number");
            }
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
                participant = ContactUtil.create(mContext, originatingAddress, originatingAddress);
            } catch (InvalidPhoneNumberException e1) {
                //TODO should not really be adding a contact, just a new conversation.
                // Should be able to have conversations not linked to a contact.
                return;
            }
        }

        boolean isSLMessage = MessageUtil.isSLCompatible(text);
        if(isSLMessage) {
            participant.setUsesSecondLine(mContext, isSLMessage);
            text = MessageUtil.sanitizeSLCompatibilityText(text);
        }

        // Find or create conversation with participant
        Conversation conversation = ConversationUtil.findOrNew(participant, mContext);

        addTextToConversation(text, conversation);
    }

    /**
     * Send Unplugged message
     * @param conversation
     * @param text
     */
    public void sendMessage(Conversation conversation, String text) {
        Message message = MessageUtil.create(
                mContext,
                conversation,
                text,
                Message.TYPE_OUTGOING,
                System.currentTimeMillis());

        EventBus.getDefault().postSticky(message);

        sendSMSOverWire(message, BaseApplication.getInstance(mContext).getKnownMasks());
        Log.d(TAG, "Sending message: " + message);
    }


    /**
     * Prepare message to be sent over cell network.
     * @param message
     * @param knownMasks
     */
    private void sendSMSOverWire(Message message, List<Mask> knownMasks) {
        String phoneNumber;
        String text;

        Conversation conversation = message.getConversation();

        // SL messages must be encrypted and wrapped in layers
        if(conversation.isSecondLineComptabile()) {
            SecondLine secondLine = conversation.getAndRefreshSecondLine(knownMasks);
            conversation.setCurrentSecondLine(secondLine);
            Throw t = secondLine.getThrow(message.getText(), Profile.getPhoneNumber());
            phoneNumber = t.getThrowTo().getFullNumber();
            text = t.getEncryptedContent();
        } else { // Regular messages are mutated to indicate they were created with SL
            phoneNumber = conversation.getParticipant().getFullNumber();
            message.mutateTextToShowSLCompatibility();
            text = message.getText();
        }

        // Send text to phoneNumber
        SMSUtil.sendSms(phoneNumber, text);
    }

    /**
     *
     * @param receivedThrow
     * @param conversation
     */
    private void receiveThrow(Throw receivedThrow, Conversation conversation) {
        String receivedText = ThrowParser.getMessage(receivedThrow.getEncryptedContent());
        addTextToConversation(receivedText, conversation);
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
