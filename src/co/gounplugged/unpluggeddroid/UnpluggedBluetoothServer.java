package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.os.Handler;
import android.util.Log;

public class UnpluggedBluetoothServer extends UnpluggedNode {
	
	// Constants
	private String TAG = "UnpluggedBluetoothServer";
	private String serviceName;
	
	// Bluetooth SDK
	private BluetoothServerSocket mBluetoothServerSocket;
		
	public UnpluggedBluetoothServer(UnpluggedMesh unpluggedMesh, BluetoothAdapter bluetoothAdapter, String serviceName_, UUID uuid, Handler handler) {
		super(unpluggedMesh, handler, bluetoothAdapter, uuid, " server ");
		this.serviceName = serviceName_;
		
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
		mBluetoothSocket = null;
	    // Keep listening until exception occurs or a socket is returned
	    while (true) {
	        try {
	        	Log.d(TAG, "attempting connection");
	        	mBluetoothSocket = mBluetoothServerSocket.accept();
	        	Log.d(TAG, "connection accepted");
	        } catch (IOException e) {
	            break;
	        }
	        // If a connection was accepted
	        if (mBluetoothSocket != null) {
	        	if (state == CONNECTED){
	        		Log.d(TAG, "rejecting");
	        		try {
	        			mBluetoothSocket.close();
					} catch (IOException e) {
						Log.e(TAG, "Could not close unwanted socket", e);
					}
	        	} else {
	        		Log.d(TAG, "connection really accepted");
	        		connectedThread = new UnpluggedConnectedThread(mBluetoothSocket, this);
		        	connectedThread.start();
		        	setState(CONNECTED);
	        	}
	        }
	    }
	}
	
	
    /** Will cancel the listening socket, and cause the thread to finish */
    public synchronized void cancel() {
        try {
        	if (mBluetoothServerSocket != null) { 
        		mBluetoothServerSocket.close();
        		mBluetoothServerSocket = null;
        	}
        	super.cancel();   	
        } catch (IOException e) { Log.e(TAG, "close() of server failed", e); }
    }
    
    public synchronized void accept() {
    	Log.d(TAG, "Accept");
    	if(state == DISCONNECTED) {
    		setState(ACCEPTING);
	    	start();
    	}
    }
    
}
