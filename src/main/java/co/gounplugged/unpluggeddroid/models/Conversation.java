package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.List;

@DatabaseTable(tableName = "conversations")
public class Conversation {
    private static final String TAG = "Conversation";
    public static final String PARTICIPANT_ID_FIELD_NAME = "contact_id";

    private SecondLine currentSecondLine;

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    public boolean isSecondLineCompatibile;

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
    }

    public Conversation(Contact participant) {
        this.participant = participant;
    }

    public Collection<Message> getMessages() {
        return messages;
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

        return id == rhs.id;
    }

    public boolean isSecondLineComptabile() {
        return isSecondLineCompatibile;
    }

    public SecondLine getCurrentSecondLine() {
        return currentSecondLine;
    }

    public SecondLine getAndRefreshSecondLine(List<Mask> knownMasks) {
        if(currentSecondLine == null) currentSecondLine = new SecondLine(participant, knownMasks);
        return currentSecondLine;
    }

    public void setCurrentSecondLine(SecondLine currentSecondLine) {
        this.currentSecondLine = currentSecondLine;
    }
}
