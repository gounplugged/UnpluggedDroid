package es.theedg.hydra;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.Log;

import co.gounplugged.unpluggeddroid.UnpluggedConnectedThread;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;

public class HydraMsg {
	private static final String TAG = "HydraMsg";
	
    public static final String HELLO                 = "HELLO";
    public static final String HELLO_OK              = "HELLO_OK";
//    public static final String GET_TAGS              = 3;
//    public static final String GET_TAGS_OK           = 4;
//    public static final String GET_TAG               = 5;
//    public static final String GET_TAG_OK            = 6;
    public static final String GET_POST              = "GET_POST";
    public static final String GET_POST_OK           = "GET_POST_OK";
//    public static final String GOODBYE               = 9;
//    public static final String GOODBYE_OK            = 10;
    public static final String INVALID               = "INVALID";
    public static final String FAILED                = "FAILED";
    public static final String SEPARATOR = "araARSrstRast";

    //  Structure of our class
    private String id;                     //  HydraMsg message ID
//    private byte[] input;
    private String input;
    private String postId;
    private long timestamp;
    private String content;
    
    public HydraMsg(byte[] input_) {
    	try {
    		this.input =  new String(input_, "UTF-8");
			this.setId(parseId());
		} catch (UnsupportedEncodingException e) {
			this.setId(INVALID);
		}
    }
    
    public static HydraMsg newHelloMsg() {
    	return new HydraMsg(serializeHydraMsg(HELLO));
    }
    
    public void send(UnpluggedConnectedThread output, UnpluggedMesh unpluggedMesh) {
    	ArrayList<HydraPost> posts = unpluggedMesh.getHydraPosts();
    	Log.d(TAG, "RECEIVED msg " + id);
    	if(id.equals(HELLO)) {
    		String postId_ = newestHydraPost(posts);
			output.write(HydraMsg.serializeHydraPostMsg(HELLO_OK, postId_));
			Log.d(TAG, "END HELLO" );
    	} else if (id.equals(HELLO_OK)) {
    		this.setPostId(parsePostId());
    		HydraPost postToReq = haveHydraPost(postId, posts);
    		if (postToReq == null) {
    			output.write(HydraMsg.serializeHydraPostMsg(GET_POST, postId));
    		}
    		Log.d(TAG, "END HELLO_OK" );
    	} else if (id.equals(GET_POST)) {
    		this.setPostId(parsePostId());
    		HydraPost postToReq = haveHydraPost(postId, posts);
    		if (postToReq != null) {
    			output.write(HydraMsg.serializeHydraMsgWPost(GET_POST, postToReq.getId(), postToReq.getTimestamp(), postToReq.getContent()));
    		}
    	} else if (id.equals( GET_POST_OK)) {
    		this.setPostId(parsePostId());
    		this.setTimestamp(parseTimestamp());
    		this.setContent(parseContent());
    		HydraPost postToReq = haveHydraPost(postId, posts);
    		if (postToReq == null) {
    			unpluggedMesh.newHydraPost(new HydraPost(postId, timestamp, content));
    		}
    	}
    }
    
    private static String newestHydraPost(ArrayList<HydraPost> posts) {
    	return null;
    }
    
    private static HydraPost haveHydraPost(String postId_, ArrayList<HydraPost> posts) {
    	for(HydraPost p : posts) {
    		if (p.getId().equals(postId_)) return p;
    	}
    	return null;
    }
    
    public static byte[] serializeHydraPostMsg(String id, String postId_) {
    	return (id + SEPARATOR + postId_).getBytes();
    }
    
    public static byte[] serializeHydraMsg(String id) {
    	return (id).getBytes();
    }
    
    public static byte[] serializeHydraMsgWPost(String id, String postId_, long timestamp_, String content_) {
    	return (id + SEPARATOR + postId_ + SEPARATOR + Long.toString(timestamp_) + SEPARATOR + content_).getBytes();
    }

    public String parseId() {
    	return getMessageSegments()[0];
    }
    
    private String parsePostId() {
    	return getMessageSegments()[1];
    }
    
    private String parseContent() {
    	return getMessageSegments()[3];
    }
    
    private long parseTimestamp() {
    	return Long.getLong(getMessageSegments()[2]);
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String[] getMessageSegments() {
		return input.split(SEPARATOR);
	}
	
	public String getInput() {
		return input;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
