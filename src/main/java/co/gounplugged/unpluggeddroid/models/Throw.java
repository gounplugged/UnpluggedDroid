package co.gounplugged.unpluggeddroid.models;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String TAG = "Throw";
    public final static String THROW_IDENTIFIER = "qZYZqQQQwZZqfQ";
    private final static int STATE_UNINITIALIZED = 0;
    private final static int STATE_READY_TO_THROW = 1;
    private final static int STATE_RECEIVED = 2; // if received a throw and could only decrypt a layer
    private final static int STATE_AT_DESTINATION = 3;
    private int mState = STATE_UNINITIALIZED;

    private final String mAdjacentThrowAddress; // originator address when arrives
    private String mContent;

    /**
     *
     * @return content to be thrown over wire or decrypted message.
     */
    public String getContent() {
        return mContent;
    }

    /**
     *
     * @return address of next Mask in Second Line.
     */
    public String getAdjacentThrowAddress() {
        return mAdjacentThrowAddress;
    }

    public Throw(Mask adjacentMask) {
        this.mAdjacentThrowAddress = adjacentMask.getFullNumber();
    }

    public Throw(String encryptedContent, Mask adjacentMask) {
        this(adjacentMask);
        this.mContent = THROW_IDENTIFIER + encryptedContent;
    }

    public static boolean isValidThrow(String encryptedContent) {
        return encryptedContent.matches("^" + THROW_IDENTIFIER + ".*");
    }

    public void setContent(String content) {
        this.mContent = THROW_IDENTIFIER + content;
    }
}

