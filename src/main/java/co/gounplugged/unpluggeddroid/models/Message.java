package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import co.gounplugged.unpluggeddroid.utils.MessageUtil;

@DatabaseTable(tableName = "messages")
public class Message {

    public static final String CONVERSATION_ID_FIELD_NAME = "conversation_id";
    public static final String MASK_ID_FIELD_NAME = "mask_id";

    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_OUTGOING = 2;

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = CONVERSATION_ID_FIELD_NAME)
    private Conversation mConversation;

    @DatabaseField
    private String mText;

    @DatabaseField
    private int mType;

    @DatabaseField
    private long mTimeStamp;

    public Message() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Message(Conversation conversation, String text, int type, long timestamp) {
        this.mConversation = conversation;
        this.mText = text;
        this.mType = type;
        this.mTimeStamp = timestamp;
    }

    public String getText() {
        return mText;
    }

    public int getType() {
        return mType;
    }

    public boolean isOutgoing() { return this.mType == TYPE_OUTGOING; }

    public Conversation getConversation() {
        return mConversation;
    }

    public void mutateTextToShowSLCompatibility() {
        this.mText = MessageUtil.mutateTextToShowSLCompatibility(mText);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("message : { ")
                .append("\n")
                .append("id: " + id)
                .append("\n")
                .append("type: " + (mType == 1 ? "incoming" : "outgoing"))
                .append("\n")
                .append("timestamp: " + mTimeStamp)
                .append("\n")
                .append("}")
                .toString();
    }
}
