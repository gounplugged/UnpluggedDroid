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
	
	PipedInputStream pipeInput;
	BufferedReader reader;
	BufferedOutputStream out;
	TestUnpluggedConnectedThread t;
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
	    pipeInput = new PipedInputStream();
	    reader = new BufferedReader(new InputStreamReader(pipeInput));
	    out = new BufferedOutputStream(new PipedOutputStream(pipeInput));
		
		t = new TestUnpluggedConnectedThread(out);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
    public void testHello() {
    	TestHydraPostDb db = new TestHydraPostDb();
    	HydraPost p = new HydraPost("cat");
    	db.newHydraPost(0, p);
    	
    	HydraMsg m = HydraMsg.newHelloMsg();
    	m.send(t, db);
    	t.close();
    	
    	String l = null;
//    	pipeInput.read();
    	
    	try {
    		l = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.d("HydraMsgTest", l);
    	assertEquals(l, HydraMsg.HELLO_OK + HydraMsg.SEPARATOR + p.getId());
	}
}
