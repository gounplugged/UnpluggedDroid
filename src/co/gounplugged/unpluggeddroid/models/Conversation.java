package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import co.gounplugged.unpluggeddroid.handlers.MessageHandler;

@DatabaseTable(tableName = "conversations")
public class Conversation {

    private SecondLine currentSecondLine;
    private MessageHandler messageHandler;

    @DatabaseField(generatedId = true)
    public long id;

    @ForeignCollectionField
    private Collection<Message> messages;

    public Conversation() {
        // all persisted classes must define a no-arg constructor with at least package visibility
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
        sms = currentSecondLine.getThrow(sms).getEncryptedContent();

        Message message = new Message(sms, Message.TYPE_OUTGOING, System.currentTimeMillis());
        message.setConversation(this);
        messageHandler.obtainMessage(MessageHandler.MESSAGE_WRITE, -1, -1, message).sendToTarget();
    }

    public SecondLine getAndRefreshSecondLine(Krewe knownMasks) {
        if(currentSecondLine == null) currentSecondLine = new SecondLine(new Contact("Marvin", Contact.DEFAULT_CONTACT_NUMBER), knownMasks);
        return currentSecondLine;
    }

    public void receiveThrow(Throw receivedThrow) {
        String nextMessage = receivedThrow.getEncryptedContent();
        Message message = new Message(nextMessage, Message.TYPE_INCOMING, System.currentTimeMillis());
        message.setConversation(this);

        messageHandler.obtainMessage(MessageHandler.MESSAGE_READ, -1, -1, message).sendToTarget();
    }
}
