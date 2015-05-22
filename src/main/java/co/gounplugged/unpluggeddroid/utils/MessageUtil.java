package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;

/**
 * Created by Marvin Arnold on 19/05/15.
 */
public class MessageUtil {
    public static Message create(Context context, Conversation conversation, String text, int type, long timestamp) {
        DatabaseAccess<Message> messageAccess = new DatabaseAccess<>(context, Message.class);
        Message m = new Message(conversation, text, type, timestamp);
        messageAccess.create(m);
        return m;
    }

    /*
        Mutates text to discreetly inform recipient that they are using SL
     */
    public static String mutateTextToShowSLCompatibility(String text) {
        return text + " ";
    }

    /*
        Guesses is a message was created  by another SL
     */
    public static boolean isSLCompatible(String text) {
        return text.matches(".*\\s$");
    }
}
