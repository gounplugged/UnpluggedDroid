package es.theedg.hydra;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import android.util.Log;
import es.theedg.hydra.HydraPost;

public class HydraPostTest extends AndroidTestCase {
	private static final String TAG = "HydraPostTest";

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
    public void testFindHydraPost() {
    	ArrayList<HydraPost> posts = new ArrayList<HydraPost>();
    	for(int i = 0; i < 10; i++) {
    		posts.add(new HydraPost("test"));
    	}
    	HydraPost p2 = posts.get(2);
    	assertEquals(p2, HydraPost.findHydraPost(p2.getId(), posts));
    	assertEquals(HydraPost.findHydraPost("FAIL", posts), null);
	}
    
    public void testNewestHydraPost() {
    	ArrayList<HydraPost> posts = new ArrayList<HydraPost>();
    	assertEquals(HydraPost.newestHydraPost(posts), null);
    	
    	for(int i = 0; i < 10; i++) {
    		posts.add(new HydraPost("test"));
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	HydraPost first = posts.get(9);
    	HydraPost newest = HydraPost.newestHydraPost(posts);
//    	HydraPost.printPosts(posts);
//    	Log.d(TAG, "Actual " + first.getId());
//    	Log.d(TAG, "Decied " + newest.getId());
    	assertEquals(newest, first);
    }
    
    public void testEquals() {
    	HydraPost p1 = new HydraPost("cat", "1", "content");
    	HydraPost p2 = new HydraPost("cat", "2", "dog");
    	
    	assertEquals(p1, p1);
    	assertEquals(p1, p2);
    }
}
