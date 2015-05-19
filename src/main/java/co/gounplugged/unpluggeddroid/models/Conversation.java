package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.query.Not;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
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
    private final Contact participant;
    public Contact getParticipant() {
        return participant;
    }

    public Conversation() {
        // all persisted classes must define a no-arg constructor with at least package visibility
        participant = null;
        messageHandler = null;
    }

    public Conversation(Contact participant, MessageHandler messageHandler) {
        this.participant = participant;
        this.messageHandler = messageHandler;
    }

    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void sendMessage(String sms, List<Mask> knownMasks) {
        currentSecondLine = getAndRefreshSecondLine(knownMasks);
        Log.d(TAG, "This many masks " + knownMasks.size());
        Throw t = currentSecondLine.getThrow(sms, Profile.getPhoneNumber());
        Message message = new Message(sms, Message.TYPE_OUTGOING, System.currentTimeMillis());
        message.sendOverWire = t.getEncryptedContent();
        message.setConversation(this);
        message.setMaskOnOtherEnd(t.getThrowTo());
        Log.d(TAG, "Message handler is " + messageHandler);
        Log.d(TAG, "Message is " + message);
        messageHandler.obtainMessage(MessageHandler.MESSAGE_WRITE, -1, -1, message).sendToTarget();
    }

    public SecondLine getAndRefreshSecondLine(List<Mask> knownMasks) {
        if(currentSecondLine == null) currentSecondLine = new SecondLine(participant, knownMasks);
        return currentSecondLine;
    }

    public void receiveThrow(Throw receivedThrow) {
        Log.d(TAG, "receiveThrow");
        String receivedMessage = ThrowParser.getMessage(receivedThrow.getEncryptedContent());
        receiveMessage(receivedMessage);
    }

    public void receiveMessage(String receivedMessage) {
        Message message = new Message(receivedMessage, Message.TYPE_INCOMING, System.currentTimeMillis());
        message.setConversation(this);
        messageHandler.obtainMessage(MessageHandler.MESSAGE_READ, -1, -1, message).sendToTarget();
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("conversation : { ")
                .append("\n")
                .append("id: " + id)
                .append("\n");
        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                builder.append(message.toString())
                        .append("\n");
            }
        }

        builder.append("}");
        builder.append("\n");

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Conversation))
            return false;
        if (obj == this)
            return true;
        Conversation rhs = (Conversation) obj;
        Log.d(TAG, "Comparing Conversation " + id + " against " + rhs.id);

        return id == rhs.id;
    }
}
