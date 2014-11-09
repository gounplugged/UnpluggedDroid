package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class UnpluggedBluetoothServer extends UnpluggedNode {
	
	// Constants
	private String TAG = "UnpluggedBluetoothServer";
	private String serviceName;
	
	// Bluetooth SDK
	private final BluetoothServerSocket mBluetoothServerSocket;
		
	public UnpluggedBluetoothServer(BluetoothAdapter bluetoothAdapter, String serviceName, UUID uuid, Handler handler) {
		super(handler, bluetoothAdapter, uuid);
		this.serviceName = serviceName;
		
        // Use a temporary object that is later assigned to mBluetoothServerSocket,
        // because mBluetoothServerSocket is final
        BluetoothServerSocket tBluetoothServerSocket = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
        	tBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(serviceName, uuid);
        	Log.d(TAG, "listenUsingInsecureRfcommWithServiceRecord");
        } catch (IOException e) {  Log.e(TAG, "listen() failed", e); }
        mBluetoothServerSocket = tBluetoothServerSocket;
	}
	
	public void run() {
		BluetoothSocket bluetoothSocket = null;
	    // Keep listening until exception occurs or a socket is returned
	    while (true) {
	        try {
	        	Log.d(TAG, "attempting connection");
	        	bluetoothSocket = mBluetoothServerSocket.accept();
	        	Log.d(TAG, "connection accepted");
	        } catch (IOException e) {
	            break;
	        }
	        // If a connection was accepted
	        if (bluetoothSocket != null) {
	        	if (state != ACCEPTING){
	        		try {
						bluetoothSocket.close();
					} catch (IOException e) {
						Log.e(TAG, "Could not close unwanted socket", e);
					}
	        	} else {
	        		connectedThread = new UnpluggedConnectedThread(bluetoothSocket, this);
		        	connectedThread.start();
		        	state = CONNECTED;
		        	mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, state, -1).sendToTarget();
	        	}
	        }
	    }
	}
	
	
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
        	mBluetoothServerSocket.close();
        	connectedThread = null;
    		state = DISCONNECTED;
    		mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, state, -1).sendToTarget();
        } catch (IOException e) { Log.e(TAG, "close() of server failed", e); }
    }
    
    public void accept() {
    	if(state == DISCONNECTED) {
	    	state = ACCEPTING;
	    	mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, state, -1).sendToTarget();
	    	start();
    	}
    }
}
