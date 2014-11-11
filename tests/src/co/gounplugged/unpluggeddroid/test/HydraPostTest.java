package co.gounplugged.unpluggeddroid.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;
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
}
