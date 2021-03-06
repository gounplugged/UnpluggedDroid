package co.gounplugged.unpluggeddroid.models;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import co.gounplugged.unpluggeddroid.utils.ContactUtil;

@DatabaseTable(tableName = "conversations")
public class Conversation {
    private static final String TAG = "Conversation";
    public static final String PARTICIPANT_ID_FIELD_NAME = "contact_id";

    @DatabaseField(generatedId = true)
    public long id;

    @ForeignCollectionField
    private Collection<Message> mMessages;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = PARTICIPANT_ID_FIELD_NAME, maxForeignAutoRefreshLevel = 3)
    private final Contact mParticipant;
    public Contact getParticipant() {
        return mParticipant;
    }

    public Conversation() {
        // all persisted classes must define a no-arg constructor with at least package visibility
        this.mParticipant = null;
    }

    public Conversation(Contact participant) throws InvalidConversationException {
        if(participant == null) throw new InvalidConversationException("Conversations must always have at least one participant");
        this.mParticipant = participant;
    }

    public Collection<Message> getMessages() {
        return mMessages;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("conversation : { ")
                .append("\n")
                .append("id: " + id)
                .append("\n");
        if (mMessages != null && !mMessages.isEmpty()) {
            for (Message message : mMessages) {
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

        return id == rhs.id;
    }

    public boolean isSecondLineComptabile(Context context) {
        ContactUtil.refresh(context, getParticipant());
        return getParticipant().usesSecondLine();
    }


    public String getName() {
        return (getParticipant() == null) ? "" : getParticipant().getName();
    }

    public class InvalidConversationException extends Exception {
        public InvalidConversationException(String message) {
            super(message);
        }
    }
}
