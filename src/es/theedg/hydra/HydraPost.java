package es.theedg.hydra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class HydraPost  {
	private final static String TAG = "HydraPost";
	
	private final String id;
	private final long timestamp;
	private final String content;
	
	public HydraPost(String id_, long timestamp_, String content_) {
		this.id = id_;
		this.timestamp = timestamp_;
		this.content = content_;
	}
	
	public HydraPost(String content_) {
		this.content = content_;
		this.timestamp = System.currentTimeMillis();
		this.id = (UUID.randomUUID()).toString();
	}

	public String getId() {
		return this.id;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getContent() {
		return this.content;
	}

	@Override
	public boolean equals(Object obj) {
	       if (!(obj instanceof HydraPost))
	            return false;
	        if (obj == this)
	            return true;
	        return this.id.equals(((HydraPost)obj).getId());
	}
	
    public static HydraPost findHydraPost(String postId_, ArrayList<HydraPost> posts) {
    	for(HydraPost p : posts) {
    		if (p.getId().equals(postId_)) return p;
    	}
    	return null;
    }
    
    public static HydraPost newestHydraPost(ArrayList<HydraPost> posts) {
    	if(posts == null || posts.isEmpty()) return null;
    	Collections.sort(posts, new HydraPostComparator());
    	return posts.get(0);
    }
//    
//    public static void printPosts(ArrayList<HydraPost> posts) {
//    	for (HydraPost p : posts) {
//    		Log.d(TAG, "CREATED AT " + Long.toString(p.getTimestamp()));
//    	}
//    }

	
}
