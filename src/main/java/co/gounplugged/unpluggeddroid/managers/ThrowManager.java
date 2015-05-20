package co.gounplugged.unpluggeddroid.managers;


import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidThrowException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import co.gounplugged.unpluggeddroid.utils.SMSUtil;

public class ThrowManager {

    public static final String TAG = "ThrowManager";

    private Context mContext;

    public ThrowManager(Context context) {
        mContext = context;
    }

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

    private void processThrow(Context context, Throw receivedThrow) {
        String nextText = receivedThrow.getEncryptedContent();
        Log.d(TAG, "Next message: " + nextText);

        if(!receivedThrow.hasArrived()) {
            Log.d(TAG, "Throw again");
            SMSUtil.sendSms(receivedThrow.getThrowTo().getFullNumber(), nextText);
        } else {
            try {
                Contact originator = receivedThrow.getThrowOriginator(context);
                Log.d(TAG, "Chat for contact " + originator.id);
                Conversation conversation = ConversationUtil.findOrNew(originator, context);
                Log.d(TAG, "Conversation for " + conversation.id);
                conversation.receiveThrow(context, receivedThrow);
            } catch (PrematureReadException e) {
                Log.e(TAG, "Premature");

            } catch (NotFoundInDatabaseException e) {
                Log.e(TAG, "Contact not found");
            } catch (InvalidPhoneNumberException e) {
                //TODO recover from problem to ensure message delivery
                Log.d(TAG, "Invalid phone number");
            }
        }
    }

    private void processRegularSMS(SmsMessage receivedSMS) {
        String originatingAddress = receivedSMS.getOriginatingAddress();
        Log.d(TAG, "Received regular SMS from " + originatingAddress);

        Contact participant;
        try {
            participant = ContactUtil.getContact(mContext, originatingAddress);
        } catch (NotFoundInDatabaseException e) {
            try {
                participant = ContactUtil.create(mContext, originatingAddress, originatingAddress);
            } catch (InvalidPhoneNumberException e1) {
                //TODO should not really be adding a contact, just a new conversation.
                // Should be able to have conversations not linked to a contact.
                return;
            }
        }

        Log.d(TAG, "Chat for contact " + participant.id);
        Conversation conversation = null;
        conversation = ConversationUtil.findOrNew(participant, mContext);
        Log.d(TAG, "Conversation for " + conversation.id);
        conversation.receiveMessage(mContext, receivedSMS.getMessageBody().toString());
    }
}
