package co.gounplugged.unpluggeddroid.test;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;
import es.theedg.hydra.HydraMsgOutput;

public class TestUnpluggedConnectedThread implements HydraMsgOutput {
    protected final OutputStream mOutputStream;

	public TestUnpluggedConnectedThread(OutputStream outputStream) {
		this.mOutputStream = outputStream;
	}

	/* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
    	Log.d("TestUnpluggedConnectedThread", "write");
        try {
        	mOutputStream.write(bytes, 0, bytes.length);
        } catch (IOException e) { }
        Log.d("TestUnpluggedConnectedThread", "wrote");
    }

    public void close() {
    	try {
			this.mOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
