package co.gounplugged.unpluggeddroid;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UnpluggedMessageHandler extends Handler {
	private static final String TAG = "UnpluggedMessageHandler";
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int STATE_CHANGED = 3;
    
    private ArrayAdapter<String> mArrayAdapter;
    private TextView connectionStatus;

    private MenuItem mItemConnectionStatus;

	public UnpluggedMessageHandler(ArrayAdapter<String> arrayAdapter, TextView connectionStatus_, MenuItem itemConnectionStatus) {
    	this.mArrayAdapter = arrayAdapter;
    	this.connectionStatus = connectionStatus_;
        mItemConnectionStatus = itemConnectionStatus;
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
			    String readMessage = new String(readBuf);
			    mArrayAdapter.add("SOMEONE: " + readMessage);
			    break;
		    
		    case STATE_CHANGED:
		    	 switch (msg.arg1) {
			    	 case UnpluggedMesh.STATE_DISCONNECTED:
			    		 connectionStatus.setText("Disconnected");
                         if (mItemConnectionStatus != null)
                            mItemConnectionStatus.setTitle("Disconnected");
				    	 break;
			    	 case UnpluggedMesh.STATE_CONNECTED:
			    		 connectionStatus.setText("Connected");
                         if (mItemConnectionStatus != null)
                             mItemConnectionStatus.setTitle("Connected");
                         break;
		    	 }
		    	 Log.d(TAG, msg.what + " " + msg.arg1);
		    	 break;
	    }
    }
}
