package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;
import android.util.Log;

import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;

/**
 * Created by Marvin Arnold on 18/05/15.
 */
public class ConversationUtil extends DbUtil {
    private static final String TAG = "ConversationUtil";

    public static Conversation createConversation(Contact participant, Context context) throws Conversation.InvalidConversationException {
        Conversation conversation = new Conversation(participant);
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        conversationAccess.create(conversation);
        return conversation;
    }

    public static Conversation findOrNew(Contact participant, Context context) throws Conversation.InvalidConversationException {
        try {
            return findByParticipant(participant, context);
        } catch (NotFoundInDatabaseException e) {
            return createConversation(participant, context);
        }
    }

    public static Conversation findByParticipant(Contact participant, Context context) throws NotFoundInDatabaseException {
        if(participant == null) {
            // TODO
        }
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        for(Conversation conversation : conversationAccess.getAll()) {
            if(conversation.getParticipant().equals(participant)) {
                return conversation;
            }
        }
        throw new NotFoundInDatabaseException("No existing conversations with this contact");
    }

    public static Conversation findById(Context context, long conversationId) throws NotFoundInDatabaseException {
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        Conversation conversation = conversationAccess.getById(conversationId);
        if(conversation == null) throw new NotFoundInDatabaseException("No conversation found with that ID");
        return conversation;
    }

    public static int refresh(Context context, Conversation conversation) {
        return refresh(context, Conversation.class, conversation);
    }

    public static int update(Context context, Conversation conversation) {
        return update(context, Conversation.class, conversation);
    }

    public static List<Conversation> getAll(Context context) {
        return getAll(context, Conversation.class);
    }

    public static void deleteAll(Context context) {
        deleteAll(context, Conversation.class);
    }

}
