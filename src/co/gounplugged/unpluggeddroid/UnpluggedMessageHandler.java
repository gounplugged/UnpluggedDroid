package co.gounplugged.unpluggeddroid;

import android.os.Handler;
import android.os.Message;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

public class UnpluggedMessageHandler extends Handler {
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    
    private ArrayAdapter mArrayAdapter;

	public UnpluggedMessageHandler(ArrayAdapter<String> arrayAdapter) {
    	this.mArrayAdapter = arrayAdapter;
    }
    
	@Override
    public void handleMessage(Message msg) {
	    switch (msg.what) {
		    case MESSAGE_WRITE:
		    byte[] writeBuf = (byte[]) msg.obj;
		    // construct a string from the buffer
		    String writeMessage = new String(writeBuf);
		    mArrayAdapter.add("Me: " + writeMessage);
		    break;
		    
		    case MESSAGE_READ:
		    byte[] readBuf = (byte[]) msg.obj;
		    // construct a string from the valid bytes in the buffer
		    String readMessage = new String(readBuf, 0, msg.arg1);
		    mArrayAdapter.add("SOMEONE: " + readMessage);
		    break;
	    }
    }
}
