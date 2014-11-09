package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class UnpluggedConnectedThread extends Thread {
	
	// Constants
	private String TAG = "UnpluggedConnectedThread";
	
	// Bluetooth SDK
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private final UnpluggedNode unpluggedNode;
 
    public UnpluggedConnectedThread(BluetoothSocket bluetoothSocket, UnpluggedNode unpluggedNode_) {
        InputStream tInputStream = null;
        OutputStream tOutputStream = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
        	tInputStream = bluetoothSocket.getInputStream();
        	tOutputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) { }
 
        this.mInputStream = tInputStream;
        this.mOutputStream = tOutputStream;
        this.unpluggedNode = unpluggedNode_;
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
            	bytes = mInputStream.read(buffer);
                // Send the obtained bytes to the UI activity
            	unpluggedNode.getHandler().obtainMessage(UnpluggedMessageHandler.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                String str = new String(buffer, "UTF-8");
            	Log.d(TAG, "reveived chat" + str);
            } catch (IOException e) {
            	unpluggedNode.setState(UnpluggedNode.DISCONNECTED);
                break;
            }
        }
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
    	Log.d(TAG, "writing chat");
        try {
        	mOutputStream.write(bytes);
        	// Share the sent message back to the UI Activity
        	unpluggedNode.getHandler().obtainMessage(UnpluggedMessageHandler.MESSAGE_WRITE, -1, -1, bytes).sendToTarget();
        	Log.d(TAG, "chat wrote");
        } catch (IOException e) { }
    }
    
}