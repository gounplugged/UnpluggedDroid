package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.query.Not;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

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

    public static Conversation createConversation(Contact participant, Context context, MessageHandler messageHandler) {
        Conversation conversation = new Conversation(participant, messageHandler);
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        conversationAccess.create(conversation);
        return conversation;
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
        Log.d(TAG, "Message handler is " + messageHandler);
        Log.d(TAG, "Message is " + message);
        messageHandler.obtainMessage(MessageHandler.MESSAGE_WRITE, -1, -1, message).sendToTarget();
    }

    public SecondLine getAndRefreshSecondLine(Krewe knownMasks) {
        if(currentSecondLine == null) currentSecondLine = new SecondLine(participant, knownMasks);
        return currentSecondLine;
    }

    public void receiveThrow(Throw receivedThrow) {
        Log.d(TAG, "receiveThrow");
        String receivedMessage = ThrowParser.getMessage(receivedThrow.getEncryptedContent());
        Message message = new Message(receivedMessage, Message.TYPE_INCOMING, System.currentTimeMillis());
        message.setConversation(this);
        messageHandler.obtainMessage(MessageHandler.MESSAGE_READ, -1, -1, message).sendToTarget();
    }

    public static Conversation findOrNew(Contact participant, Context context, MessageHandler messageHandler) throws NotFoundInDatabaseException {
        if(participant == null) {
            // TODO
        } try {
            return findByParticipant(participant, context, messageHandler);
        } catch (NotFoundInDatabaseException e) {
            return createConversation(participant, context, messageHandler);
        }
    }

    public static Conversation findByParticipant(Contact participant, Context context, MessageHandler messageHandler) throws NotFoundInDatabaseException {
        if(participant == null) {
            // TODO
        }
        Log.d(TAG, "Searching for convo with" + participant.getName());
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        for(Conversation conversation : conversationAccess.getAll()) {
            if(conversation.getParticipant().equals(participant)) {
                conversation.setMessageHandler(messageHandler);
                return conversation;
            }
        }
        throw new NotFoundInDatabaseException("No existing conversations with this contact");
    }

    public static Conversation findById(Context context, long conversationId, MessageHandler messageHandler) throws NotFoundInDatabaseException {
        Log.d(TAG, "Searching for Conversation " + conversationId);
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        Conversation conversation = conversationAccess.getById(conversationId);
        if(conversation == null) throw new NotFoundInDatabaseException("No conversation found with that ID");
        conversation.setMessageHandler(messageHandler);
        return conversation;
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

        for (Message message : messages) {
            builder.append(message.toString())
                    .append("\n");
        }

        builder.append("}");
        builder.append("\n");

        return builder.toString();
    }
}
