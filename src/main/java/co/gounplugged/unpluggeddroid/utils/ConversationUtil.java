package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;
import android.util.Log;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;

/**
 * Created by Marvin Arnold on 18/05/15.
 */
public class ConversationUtil {
    private static final String TAG = "ConversationUtil";

    public static Conversation createConversation(Contact participant, Context context) {
        Conversation conversation = new Conversation(participant);
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        conversationAccess.create(conversation);
        return conversation;
    }

    public static Conversation findOrNew(Contact participant, Context context) {
        if(participant == null) {
            // TODO
        } try {
            return findByParticipant(participant, context);
        } catch (NotFoundInDatabaseException e) {
            return createConversation(participant, context);
        }
    }

    public static Conversation findByParticipant(Contact participant, Context context) throws NotFoundInDatabaseException {
        if(participant == null) {
            // TODO
        }
        Log.d(TAG, "Searching for convo with" + participant.getName());
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        for(Conversation conversation : conversationAccess.getAll()) {
            if(conversation.getParticipant().equals(participant)) {
                return conversation;
            }
        }
        throw new NotFoundInDatabaseException("No existing conversations with this contact");
    }

    public static Conversation findById(Context context, long conversationId) throws NotFoundInDatabaseException {
        Log.d(TAG, "Searching for Conversation " + conversationId);
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        Conversation conversation = conversationAccess.getById(conversationId);
        if(conversation == null) throw new NotFoundInDatabaseException("No conversation found with that ID");
        return conversation;
    }


}
