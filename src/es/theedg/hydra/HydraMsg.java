package es.theedg.hydra;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class HydraMsg {
	private static final String TAG = "HydraMsg";
	
    public static final int HELLO                 = 1;
    public static final int HELLO_OK              = 2;
    public static final int GET_TAGS              = 3;
    public static final int GET_TAGS_OK           = 4;
    public static final int GET_TAG               = 5;
    public static final int GET_TAG_OK            = 6;
    public static final int GET_POST              = 7;
    public static final int GET_POST_OK           = 8;
    public static final int GOODBYE               = 9;
    public static final int GOODBYE_OK            = 10;
    public static final int INVALID               = 11;
    public static final int FAILED                = 12;
    
    public static final String SEPARATOR = "araARSrstRast";

    //  Structure of our class
    private int id;                     //  HydraMsg message ID
//    private byte[] input;
    private String input;
    private String post_id;
    private long timestamp;
    private String content;
    
    public HydraMsg(byte[] input_) {
    	try {
    		this.input =  new String(input_, "UTF-8");
			this.setId(getId(input));
		} catch (UnsupportedEncodingException e) {
			this.setId(INVALID);
		}
    }

    public int getId(String str) {
    	int i = Integer.parseInt(getMessageSegments()[0]);
    	if (i >= HELLO && i <= FAILED) return i;
    	return INVALID;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String[] getMessageSegments() {
		return input.split(SEPARATOR);
	}
}
