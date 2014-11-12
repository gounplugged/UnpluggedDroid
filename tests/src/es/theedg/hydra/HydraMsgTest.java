package es.theedg.hydra;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import android.test.AndroidTestCase;
import android.util.Log;
import co.gounplugged.unpluggeddroid.test.TestUnpluggedConnectedThread;

public class HydraMsgTest extends AndroidTestCase {
	private static final String TAG = "HydraMsgTest";
	
	PipedInputStream pipeInput;
	BufferedReader reader;
	BufferedOutputStream out;
	TestUnpluggedConnectedThread t;
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		reset();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
    public void testSimpleHello() {
    	TestHydraPostDb db = new TestHydraPostDb();
    	HydraPost p = new HydraPost("cat");
    	db.newHydraPost(0, p);
      	HydraMsg m = HydraMsg.newHelloMsg();
    	
    	String l = testHydraMsg(m, db);
    	assertEquals(l, HydraMsg.HELLO_OK + HydraMsg.SEPARATOR + p.getId());
	}
    
    public void testComplexHello() {
    	TestHydraPostDb db = new TestHydraPostDb();
    	db.newHydraPost(0, new HydraPost("cat"));
    	db.newHydraPost(0, new HydraPost("cat"));
    	sleep();
    	HydraPost old = new HydraPost("cat");
    	sleep();
    	HydraPost p = new HydraPost("cat");
    	db.newHydraPost(0, p);
    	db.newHydraPost(0, old);
    	
    	HydraMsg m = HydraMsg.newHelloMsg();
    	
    	String l = testHydraMsg(m, db);
    	assertEquals(l, HydraMsg.HELLO_OK + HydraMsg.SEPARATOR + p.getId());
	}
    
    public void testNullHello() {
    	TestHydraPostDb db = new TestHydraPostDb();
     	HydraMsg m = HydraMsg.newHelloMsg();
    	
    	String l = testHydraMsg(m, db);
    	assertEquals(l, HydraMsg.HELLO_OK + HydraMsg.SEPARATOR + "null");
	}
    
    public void testHelloOK() {
    	TestHydraPostDb db = new TestHydraPostDb();
    	HydraMsg m = new HydraMsg(HydraMsg.serializeHydraPostMsg(HydraMsg.HELLO_OK, "cat"));
    	
    	String l = testHydraMsg(m, db);
    	assertEquals(l, HydraMsg.GET_POST + HydraMsg.SEPARATOR + "cat");
	}
    
    public void testGetPost() {
    	TestHydraPostDb db = new TestHydraPostDb();
    	HydraPost p = new HydraPost("cat");
    	db.newHydraPost(0, p);
    	HydraMsg m = new HydraMsg(HydraMsg.serializeHydraPostMsg(HydraMsg.GET_POST, p.getId()));
    	
    	String l = testHydraMsg(m, db);
    	assertEquals(l, HydraMsg.GET_POST_OK + HydraMsg.SEPARATOR +  p.getId() + HydraMsg.SEPARATOR +  p.getTimestamp() + HydraMsg.SEPARATOR +  p.getContent());
    }
    
    public void testGetPostOk() {
    	reset();
    	TestHydraPostDb db = new TestHydraPostDb();
    	HydraPost p = new HydraPost("cat");
    	HydraMsg m = new HydraMsg(HydraMsg.serializeHydraMsg(HydraMsg.GET_POST_OK, p.getId(), p.getTimestamp(), p.getContent()));
    	
    	testHydraMsg(m, db);
    	assertEquals(HydraPost.findHydraPost(p.getId(), db.getHydraPosts()), p);
    }
    
    public void testParsing() {
    	String s = "id" + HydraMsg.SEPARATOR + "post_id" + HydraMsg.SEPARATOR + "12321" + HydraMsg.SEPARATOR + "contents";
    	HydraMsg m = new HydraMsg(s.getBytes());
    	assertEquals(m.parseId(), "id");
    	assertEquals(m.parsePostId(), "post_id");
    	
    	assertEquals(m.parseTimestamp(), "12321");
    	assertEquals(m.parseContent(), "contents");
    }
    
    public String testHydraMsg(HydraMsg m, TestHydraPostDb db) {
    	m.send(t, db);
    	t.close();
    	String l = null;
    	
    	try { l = reader.readLine(); } catch (IOException e) { }
    	
    	return l;
    }
    
    public void reset() {
	    try {
	    	pipeInput = new PipedInputStream();
		    reader = new BufferedReader(new InputStreamReader(pipeInput));
			out = new BufferedOutputStream(new PipedOutputStream(pipeInput));
		} catch (IOException e) { }
		
		t = new TestUnpluggedConnectedThread(out);
    }
    
    public void sleep() {
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
    }
}
