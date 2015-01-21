package co.gounplugged.unpluggeddroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message {

    public static final String CONVERSATION_ID_FIELD_NAME = "conversation_id";


    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_OUTGOING = 2;

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = CONVERSATION_ID_FIELD_NAME)
    private Conversation conversation;

    @DatabaseField
    private String mMessage;

    @DatabaseField
    private int mType;

    @DatabaseField
    private long mTimeStamp;

    public Message() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Message(String message, int type, long timestamp) {
        this.mMessage = message;
        this.mType = type;
        this.mTimeStamp = timestamp;
    }


    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.mTimeStamp = timeStamp;
    }

    public boolean isOutgoing() { return this.mType == TYPE_OUTGOING; }

    public boolean isIncoming() { return this.mType == TYPE_INCOMING; }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
