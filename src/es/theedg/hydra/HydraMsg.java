package es.theedg.hydra;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.util.Log;
import co.gounplugged.unpluggeddroid.UnpluggedMessageHandler;

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
    public static final String SEPARATOR = "^^vvv^^^vvv^^^";

    //  Structure of our class
    private String id;                     //  HydraMsg message ID
    private String input;
    private String postId;
    private String timestamp;
    private String content;
    
    public HydraMsg(byte[] input_) {
    	String id_;
    	String in_;
    	try {
    		this.input =  new String(input_, "UTF-8");
			this.id = parseId();
		} catch (UnsupportedEncodingException e) {
			this.input = HydraMsg.INVALID;
			this.id = HydraMsg.INVALID;
		}
    }
    
    public HydraMsg(String s) {
		this.input =  s;
		this.id = parseId();
		Log.d(TAG, "ID: " + id);
		Log.d(TAG, "INPUT: " + input);
    }
    
    public static HydraMsg newHelloMsg() {
    	return new HydraMsg(serializeHydraMsg(HELLO));
    }
    
    public void send(HydraMsgOutput output, HydraPostDb unpluggedMesh) {
    	Log.d(TAG, "RECEIVED msg " + id);
    	if(id.equals(HELLO)) {
    		respondToHello(output, unpluggedMesh);
    	} else if (id.equals(HELLO_OK)) {
    		respondToHelloOk(output, unpluggedMesh);
    	} else if (id.equals(GET_POST)) {
    		respondToGetPost(output, unpluggedMesh);
    	} else if (id.equals(GET_POST_OK)) {
    		respondToGetPostOk(output, unpluggedMesh);
    	}
    }
    
    public void respondToHello(HydraMsgOutput output, HydraPostDb unpluggedMesh) {
    	ArrayList<HydraPost> posts = unpluggedMesh.getHydraPosts();
    	HydraPost newestPost = HydraPost.newestHydraPost(posts);
		String newestPostId;
		if(newestPost == null) {
			newestPostId = "null";
		} else {
			newestPostId = newestPost.getId();
//			Log.d(TAG, "HELLO_OK " + newestPost.getContent() );
		}
		output.write(HydraMsg.serializeHydraMsg(HELLO_OK, newestPostId));
    }
    
    public void respondToHelloOk(HydraMsgOutput output, HydraPostDb unpluggedMesh) {
    	ArrayList<HydraPost> posts = unpluggedMesh.getHydraPosts();
        this.setPostId(parsePostId());
		if(!postId.equals("null")) {
			HydraPost postToReq = HydraPost.findHydraPost(postId, posts);
			if (postToReq == null) {
				output.write(HydraMsg.serializeHydraMsg(GET_POST, postId));
//				Log.d(TAG, "REPLIED TO HELLO_OK: " + GET_POST + postId);
			}
		}
    }
    
    public void respondToGetPost(HydraMsgOutput output, HydraPostDb unpluggedMesh) {
    	ArrayList<HydraPost> posts = unpluggedMesh.getHydraPosts();
		this.setPostId(parsePostId());
		HydraPost postToReq = HydraPost.findHydraPost(postId, posts);
		if (postToReq != null) {
			output.write(HydraMsg.serializeHydraMsg(GET_POST_OK, postToReq.getId(), postToReq.getTimestamp(), postToReq.getContent()));
//			Log.d(TAG, "REPLIED TO GET_POST: " + GET_POST_OK + " " + postToReq.getContent());
		}
    }
    
    public void respondToGetPostOk(HydraMsgOutput output, HydraPostDb unpluggedMesh) {
    	ArrayList<HydraPost> posts = unpluggedMesh.getHydraPosts();
		Log.d(TAG, "ENTERED GET_POST_OK");
		this.setPostId(parsePostId());
		this.setTimestamp(parseTimestamp());
		this.setContent(parseContent());
		HydraPost postToReq = HydraPost.findHydraPost(postId, posts);
//		Log.d(TAG, "weirdness " + content);
		if (postToReq == null) {
			Log.d(TAG, "PLZ ADD");
			unpluggedMesh.addHydraPost(UnpluggedMessageHandler.MESSAGE_READ, new HydraPost(postId, timestamp, content));
//			Log.d(TAG, "ADDED NEW POST id " + postId);
//			Log.d(TAG, "ADDED NEW POST time" + timestamp);
//			Log.d(TAG, "ADDED NEW POST content" + content);
		}
    }
    
    public static byte[] serializeHydraMsg(String... sections) {
	    String serialized = "";
	    serialized += sections[0];
	    for (int i = 1; i < sections.length; i++) {   
	      serialized += SEPARATOR + sections[i];
	    }
//	    Log.d(TAG, "SERIALIZING" + serialized);
	    return HydraMsg.getFormattedBytes(serialized);
	 }
    

    public String parseId() {
    	return getMessageSegments()[0];
    }
    
    public String parsePostId() {
    	return getMessageSegments()[1];
    }
    
    public String parseContent() {
    	return getMessageSegments()[3];
    }
    
    public String parseTimestamp() {
    	return getMessageSegments()[2];
    }

	public String getId() {
		return id;
	}
	
	public String[] getMessageSegments() {
		return input.split(Pattern.quote(HydraMsg.SEPARATOR));
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public static byte[] getFormattedBytes(String s) {
		return s.getBytes(Charset.forName("UTF-8"));
	}
}
