package co.gounplugged.unpluggeddroid.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;
import es.theedg.hydra.HydraMsg;
import es.theedg.hydra.HydraMsgOutput;

public class UnpluggedBluetoothHydraMsgOutput extends Thread implements HydraMsgOutput {
	
	// Constants
	private static String TAG = "UnpluggedConnectedThread";
	
	// Bluetooth SDK
    protected final InputStream mInputStream;
    protected final OutputStream mOutputStream;
    private final UnpluggedBluetoothManager mUnpluggedBluetoothManager;
    private final BluetoothSocket mBluetoothSocket;
 
    public UnpluggedBluetoothHydraMsgOutput(BluetoothSocket bluetoothSocket, UnpluggedBluetoothManager unpluggedBluetoothManager) {
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
        this.mBluetoothSocket = bluetoothSocket;
        this.mUnpluggedBluetoothManager = unpluggedBluetoothManager;
    }
 
    @Override
    public void run() {
    	Log.d(TAG, "running chat stream");
        byte[] buffer = new byte[2048];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
        	Log.d(TAG, "new list_item_message_outgoing received");
            try {
                // Read from the InputStream
            	bytes = mInputStream.read(buffer);
            	byte[] trimmedBuffer = trim(buffer);
//            	logBuffer(trimmedBuffer);
            	handleRead(bytes, trimmedBuffer);
            } catch (IOException e) {
            	cancel();
                break;
            }
        }
    }
    
    public static void logBuffer(byte[] buffer) {
    	int i = 0;
    	for(byte b : buffer) {
    		Log.d(TAG, "Byte " + Integer.toString(i) + ": " + b);
    		i++;
    	}
    }
    
    public void handleRead(int bytes, byte[] buffer) {
        // Send the obtained bytes to the UI activity 	
		try {
			String str = new String(buffer, "UTF-8");
			Log.d(TAG, "reveived chat: " + str);
			HydraMsg hydraMsg = new HydraMsg(buffer);
			hydraMsg.send(this, mUnpluggedBluetoothManager.getHydraPostDb());
		} catch (UnsupportedEncodingException e) {	}
    	
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
    	Log.d(TAG, "writing HydraMsg");
        try {
        	mOutputStream.write(bytes);
//        	unpluggedNode.sendHydraMsg(bytes);
        	Log.d(TAG, "chat wrote");
        } catch (IOException e) { }
    }
    
    public void cancel() {
    	try {
			mOutputStream.close();
			mInputStream.close();
			mBluetoothSocket.close();
			mUnpluggedBluetoothManager.setConnectionState(UnpluggedBluetoothManager.STATE_DISCONNECTED);
		} catch (IOException e) {}
    }
    
    static byte[] trim(byte[] bytes)
    {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) { --i; }
        return Arrays.copyOf(bytes, i + 1);
    }
    
}