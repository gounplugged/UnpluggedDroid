package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;

@DatabaseTable(tableName = "conversations")
public class Conversation {
    private static final String TAG = "Conversation";
    public static final String PARTICIPANT_ID_FIELD_NAME = "contact_id";

    private SecondLine currentSecondLine;
    private MessageHandler messageHandler;

    @DatabaseField(generatedId = true)
    public long id;

    @ForeignCollectionField
    private Collection<Message> messages;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = PARTICIPANT_ID_FIELD_NAME)
    private Contact participant;
    public Contact getParticipant() {
        return participant;
    }

    public Conversation() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Conversation(Contact participant) {
        this.participant = participant;
    }

    public void setMessageHandler(MessageHandler h) {
        this.messageHandler = h;
    }

    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void sendMessage(String sms, Krewe knownMasks) {
        currentSecondLine = getAndRefreshSecondLine(knownMasks);
        Throw t = currentSecondLine.getThrow(sms);
        Message message = new Message(sms, Message.TYPE_OUTGOING, System.currentTimeMillis());
        message.sendOverWire = t.getEncryptedContent();
        message.setConversation(this);
        message.setMaskOnOtherEnd(t.getThrowTo());
        messageHandler.obtainMessage(MessageHandler.MESSAGE_WRITE, -1, -1, message).sendToTarget();
    }

    public SecondLine getAndRefreshSecondLine(Krewe knownMasks) {
        if(currentSecondLine == null) currentSecondLine = new SecondLine(participant, knownMasks);
        return currentSecondLine;
    }

    public void receiveThrow(Throw receivedThrow) {
        try {
            try {
                participant = receivedThrow.getThrownFrom();
            } catch (PrematureReadException e) {

            }
            String receivedMessage = ThrowParser.getMessage(receivedThrow.getEncryptedContent());
            Message message = new Message(receivedMessage, Message.TYPE_INCOMING, System.currentTimeMillis());
            message.setConversation(this);
            messageHandler.obtainMessage(MessageHandler.MESSAGE_READ, -1, -1, message).sendToTarget();
        } catch (InvalidPhoneNumberException e) {
            //TODO Received message but don't know who its from
        }
    }

    public static Conversation findOrNew(Contact participant, Context context, MessageHandler messageHandler) {
        if(participant == null) {
            Log.d(TAG, "NOTHING FOUND");
            return null;
        } else {
            Log.d(TAG, "Found contact " + participant.getName());
            DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
            for(Conversation conversation : conversationAccess.getAll()) {
                if(conversation.getParticipant().equals(participant)) {
                    return conversation;
                }
            }
            Conversation conversation = new Conversation(participant);
            conversation.setMessageHandler(messageHandler);
            conversationAccess.create(conversation);
            return conversation;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("conversation : { ")
                .append("\n")
                .append("id: " + id)
                .append("\n");

        for (Message message : messages) {
            builder.append(message.toString())
                    .append("\n");
        }

        builder.append("}");
        builder.append("\n");

        return builder.toString();
    }
}
