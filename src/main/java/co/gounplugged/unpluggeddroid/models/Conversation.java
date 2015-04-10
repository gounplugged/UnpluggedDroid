package co.gounplugged.unpluggeddroid.models;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;

@DatabaseTable(tableName = "conversations")
public class Conversation {

    private SecondLine currentSecondLine;
    private MessageHandler messageHandler;

    @DatabaseField(generatedId = true)
    public long id;

    @ForeignCollectionField
    private Collection<Message> messages;

    @ForeignCollectionField
    private Contact participant;

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
        Throw t = currentSecondLine.getThrow(sms);
        sms = t.getEncryptedContent();
        Message message = new Message(sms, Message.TYPE_OUTGOING, System.currentTimeMillis());
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
            String nextMessage = receivedThrow.getEncryptedContent();
            Message message = new Message(nextMessage, Message.TYPE_INCOMING, System.currentTimeMillis());
            message.setConversation(this);
            messageHandler.obtainMessage(MessageHandler.MESSAGE_READ, -1, -1, message).sendToTarget();
        } catch (InvalidPhoneNumberException e) {
            //TODO Received message but don't know who its from
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
