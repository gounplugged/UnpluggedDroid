package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class UnpluggedBluetoothClient extends UnpluggedNode {
	
	// Constants
	private String TAG = "UnpluggedBluetoothClient";

	// Bluetooth SDK
    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mBluetoothDevice;
	
	public UnpluggedBluetoothClient(BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter, UUID uuid, Handler handler) {
		super(handler, bluetoothAdapter, uuid);
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.mBluetoothDevice = bluetoothDevice;
   
        BluetoothSocket tBluetoothSocket = null;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
        	tBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { }
        mBluetoothSocket = tBluetoothSocket;

    	Log.d(TAG, "socket set");
	}
	
    public void run() {
    	Log.d(TAG, "run");

        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
 
        if(state == CONNECTING) {
        	try {
	        	Log.d(TAG, "attempting connection w " + mBluetoothDevice.getName());
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	        	mBluetoothSocket.connect();
	        	Log.d(TAG, "connection accepted");
	        	connectedThread = new UnpluggedConnectedThread(mBluetoothSocket, this);
	        	connectedThread.start();
	        	state = CONNECTED;
	        	mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, state, -1).sendToTarget();
	//        	Connected
        	
	        } catch (IOException connectException) {
	        	Log.d(TAG, "connection failed");
	            cancel();
	        }
        }
 
        // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mBluetoothSocket);
    }


    public void cancel() {
        try {
        	mBluetoothSocket.close();
        	connectedThread = null;
    		state = DISCONNECTED;
    		mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, state, -1).sendToTarget();
        } catch (IOException e) { Log.e(TAG, "close() of client failed", e); }
    }
    
    public void connect() {
    	state = CONNECTING;
    	mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, state, -1).sendToTarget();
    	start();
    }
    

}