package co.gounplugged.unpluggeddroid.test;

import java.nio.charset.Charset;

import es.theedg.hydra.HydraMsg;
import android.test.AndroidTestCase;
import android.util.Log;

public class HydraMsgTest extends AndroidTestCase {
	private static final String TAG = "HydraMsgTest";

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
	
    public void testInstantiateWithID() {
    	String a = "1";
    	HydraMsg hydraMsg = new HydraMsg( a.getBytes(Charset.forName("UTF-8")) );
	    assertEquals(hydraMsg.getId(), "1");
	    
	    String b = HydraMsg.SEPARATOR;

	    hydraMsg = new HydraMsg( (a+b).getBytes(Charset.forName("UTF-8")) );
	    assertEquals(hydraMsg.getId(), "1");
	}
    
    public void testGetMessageSegments() {
    	String a = "1";
    	HydraMsg hydraMsg = new HydraMsg( a.getBytes(Charset.forName("UTF-8")) );
    	String[] segments = hydraMsg.getMessageSegments();
	    assertEquals(segments[0] , a);
	    assertEquals(segments.length , 1);
	    
	    String b = HydraMsg.SEPARATOR;
	    hydraMsg = new HydraMsg( (a+b).getBytes(Charset.forName("UTF-8")) );
	    segments = hydraMsg.getMessageSegments();
	    assertEquals(segments[0] , "1");
	    assertEquals(segments.length , 1);
	    
	    String c = "bba";
	    hydraMsg = new HydraMsg( (a+b+c).getBytes(Charset.forName("UTF-8")) );
	    segments = hydraMsg.getMessageSegments();
	    assertEquals(segments[0] , a);
	    assertEquals(segments[1] , c);
	    assertEquals(segments.length , 2);
    }

}
