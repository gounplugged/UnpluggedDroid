package co.gounplugged.unpluggeddroid.model;

public class UnpluggedMessage {

    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_OUTGOING = 2;

    private String mMessage;
    private int mType;
    private long mTimeStamp;

    public UnpluggedMessage() {

    }

    public UnpluggedMessage(String message, int type, long timestamp) {
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
}
