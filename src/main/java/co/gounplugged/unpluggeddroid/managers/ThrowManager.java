package co.gounplugged.unpluggeddroid.managers;


import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.List;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.BuilderThrow;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.MessageThrow;
import co.gounplugged.unpluggeddroid.models.SecondLine;
import co.gounplugged.unpluggeddroid.models.TerminatingBuilderThrow;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;
import co.gounplugged.unpluggeddroid.utils.MessageUtil;
import co.gounplugged.unpluggeddroid.utils.SMSUtil;
import de.greenrobot.event.EventBus;

public class ThrowManager {

    public static final String TAG = "ThrowManager";

    private Context mContext;

    public ThrowManager(Context context) {
        mContext = context;
    }
    public BaseApplication getBaseApplication() {
        return (BaseApplication) mContext;
    }

    /**
     * Process incoming SMS messages as throw or regular sms
     * @param lastSMSInBundle
     * @param concatenatedText
     */
    public void processUnknownSMS(SmsMessage lastSMSInBundle, String concatenatedText) {
        Log.d(TAG, "Received text: " + concatenatedText);
        if (Throw.isValidThrow(concatenatedText)) {
            try {
                concatenatedText = getBaseApplication().getOpenPGPBridgeService().decrypt(concatenatedText);
                Log.d(TAG, "processed as throw");
                String thrownFromAddress = lastSMSInBundle.getOriginatingAddress();
                Mask thrownFromMask = MaskUtil.getMask(mContext, thrownFromAddress);

                processThrowContent(concatenatedText, thrownFromMask);
            } catch (EncryptionUnavailableException e) {
                // Cannot be a regular SMS because first test is to ensure that it has Throw identifier.
                // If just a relay throw, then can forward it along without decryption.
                String thrownFromAddress = lastSMSInBundle.getOriginatingAddress();
                SecondLine secondLine = getBaseApplication().getSecondLine();

                try {
                    Mask throwTo = secondLine.getKreweResponsibility(thrownFromAddress);
                    Throw newThrow = new Throw(concatenatedText, throwTo);
                    sendThrowOverWire(newThrow);
                } catch (SecondLine.SecondLineException e1) {
                    // TODO fail gracefully. Don't know who to forward it to.
                }
            } catch (InvalidPhoneNumberException e) {
                // TODO, not really a todo but this should never happen.
                // Means that we are not parsing phone numbers well.
                // Maybe we can log and try to improve.
                Log.d(TAG, "Invalid number, not processed");
                return;
            }
        } else { // A regular message, not a Throw
            Log.d(TAG, "processed as SMS");
            processRegularSMS(lastSMSInBundle, concatenatedText);
        }
    }

    private void processThrowContent(String decryptedContent, Mask thrownFromMask) throws InvalidPhoneNumberException {
        if(BuilderThrow.isValidBuilderThrow(decryptedContent)) {
            Log.d(TAG, "processed as BuilderThrow");
            processBuilderThrow(decryptedContent, thrownFromMask);
        } else if(TerminatingBuilderThrow.isValidTerminatingBuilderThrow(decryptedContent)) {
            Log.d(TAG, "processed as TerminatingBuilderThrow");
            processTerminatingBuilderThrow(decryptedContent, thrownFromMask);
        } else if(MessageThrow.isValidMessageThrow(decryptedContent)) {
            Log.d(TAG, "processed as MessageThrow");
            processMessageThrow(decryptedContent, thrownFromMask);
        } else {
            // TODO fail gracefully. Don't know what kind of throw this is.
        }
    }

    private void processBuilderThrow(String decryptedContent, Mask thrownFromMask) throws InvalidPhoneNumberException {
        // add a responsibility
        String throwToNumber = BuilderThrow.getThrowToNumber(decryptedContent);
        Mask throwToMask = MaskUtil.create(mContext, throwToNumber);

        getBaseApplication().getSecondLine().addResponsibility(thrownFromMask, throwToMask);
    }

    private void processTerminatingBuilderThrow(String decryptedContent, Mask thrownFromMask) throws InvalidPhoneNumberException {
        // add an understanding
        String trueOriginatorNumber = TerminatingBuilderThrow.getTrueOriginatorNumber(decryptedContent);
        Contact trueOriginator = ContactUtil.firstOrCreate(mContext, trueOriginatorNumber, trueOriginatorNumber);

        getBaseApplication().getSecondLine().addUnderstanding(thrownFromMask, trueOriginator);
    }

    private void processMessageThrow(String decryptedContent, Mask thrownFromMask) {
        // actually meant for you, read it
        try {
            Contact trueOriginatorContact = getBaseApplication().getSecondLine().getKreweUnderstanding(thrownFromMask);
            Conversation conversation = ConversationUtil.findOrNew(trueOriginatorContact, mContext);
            String content =  MessageThrow.getContent(decryptedContent);
            addTextToConversation(content, conversation);
        } catch (SecondLine.SecondLineException e) {
            // TODO don't know sent this to you.
            Log.d(TAG, "processMessageThrow: SecondLineException");
        } catch (Conversation.InvalidConversationException e) {
            // TODO conversation is null for unknown reason
            Log.d(TAG, "processMessageThrow: InvalidConversationException");
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
            Log.d(TAG, "Conversation " + conversation.id + " tagged as SL compatible: " + conversation.isSecondLineComptabile(mContext));

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
    public void sendMessage(final Conversation conversation, final String text, final OpenPGPBridgeService openPGPBridgeService) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = MessageUtil.create(
                        mContext,
                        conversation,
                        text,
                        Message.TYPE_OUTGOING,
                        System.currentTimeMillis());

                Log.d(TAG, "sendMessage message uses SL: " + message.getConversation().isSecondLineComptabile(mContext) +
                        " conversation uses SL: " + conversation.isSecondLineComptabile(mContext));

                EventBus.getDefault().postSticky(message);

                sendMessageOverWire(message, openPGPBridgeService);
                Log.d(TAG, "Sending message: " + message);
            }
        }).run();
    }

    /**
     * Prepare message to be sent over cell network.
     * @param message
     */
    private void sendMessageOverWire(Message message, OpenPGPBridgeService openPGPBridgeService) {
        String phoneNumber;
        String text;

        Conversation conversation = message.getConversation();

        // SL messages must be encrypted and wrapped in layers
        if(conversation.isSecondLineComptabile(mContext)) {
            SecondLine secondLine = getBaseApplication().getSecondLine();

            try {
                Throw t = secondLine.getMessageThrow(
                        message.getText(),
                        message.getConversation().getParticipant(),
                        openPGPBridgeService);
                sendThrowOverWire(t);
            } catch (EncryptionUnavailableException e) {
                // Encryption unavailable so just send normally
                sendSMSOverWire(message);
            } catch (SecondLine.SecondLineException e) {
                // Krewe not yet established for this recipient.
                // Establish path and then send regular message.
                // Will be able to send Throw next time
                ensureKreweEstablished(message.getConversation(), openPGPBridgeService);
                sendSMSOverWire(message);
            }
        } else {
            sendSMSOverWire(message);
        }
    }

    private void sendThrowOverWire(Throw t) {
        String phoneNumber = t.getAdjacentThrowAddress();
        String content = t.getContent();
        Log.d(TAG, "Send throw to: " + phoneNumber + " content: " + content);
        SMSUtil.sendSms(phoneNumber, content);
    }

    private void sendSMSOverWire(Message message) {
        // Regular messages are mutated to indicate they were created with SL
        Conversation conversation = message.getConversation();
        String phoneNumber = conversation.getParticipant().getFullNumber();

        message.mutateTextToShowSLCompatibility();
        String content = message.getText();

        SMSUtil.sendSms(phoneNumber, content);
    }

    public boolean ensureKreweEstablished(Conversation conversation, OpenPGPBridgeService openPGPBridgeService) {
        Log.d(TAG, "STARTING TO ensureKreweEstablished");
        if(conversation.isSecondLineComptabile(mContext)) {
            Log.d(TAG, "ensureKreweEstablished: isSecondLineCompatible");
            SecondLine secondLine = getBaseApplication().getSecondLine();
            try {
                secondLine.getEstablishedKrewe(conversation.getParticipant());
                Log.d(TAG, "ensureKreweEstablished: krewe already established");
                return true;
            } catch (SecondLine.SecondLineException e) {
                Log.d(TAG, "ensureKreweEstablished: no krewe established");
                return establishNewKrewe(conversation, secondLine, openPGPBridgeService);
            }
        }

        return false;
    }

    public boolean establishNewKrewe(Conversation conversation, SecondLine secondLine, OpenPGPBridgeService openPGPBridgeService) {
        if(conversation.isSecondLineComptabile(mContext)) {
            try {
                List<Throw> builderThrows = secondLine.establishNewKrewe(conversation.getParticipant(), openPGPBridgeService);
                for (Throw t : builderThrows) {
                    Log.d(TAG, "establishNewKrewe: sending out a builder throw");
                    sendThrowOverWire(t);
                }
                return true;
            } catch (Krewe.KreweException e) {
                // TODO seed with more known masks
                Log.d(TAG, "establishNewKrewe: not enough masks");
            } catch (EncryptionUnavailableException e) {
                // TODO display message to user to enable encryption
                Log.d(TAG, "establishNewKrewe: no encryption");
            }
        }

        return false;
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
