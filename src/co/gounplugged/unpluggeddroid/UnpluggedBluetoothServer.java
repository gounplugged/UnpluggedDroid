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
	
	// Bluetooth SDK
	private final BluetoothServerSocket mBluetoothServerSocket;
		
	public UnpluggedBluetoothServer(UnpluggedMesh unpluggedMesh, BluetoothAdapter bluetoothAdapter, String serviceName, UUID uuid, Handler handler) {
		super(unpluggedMesh, bluetoothAdapter);
		
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
	        	if (mUnpluggedMesh.getConnectionState() != UnpluggedMesh.STATE_CONNECTED) {
	        		mUnpluggedMesh.connectionEstablished(bluetoothSocket);
	        	} else {
	        		try {
		        		 bluetoothSocket.close();
		        	 } catch (IOException e) {
		        		 Log.e(TAG, "Could not close unwanted socket", e);
		        	 }
	        	}
	        }
	    }
	}
	
	
    /** Will cancel the listening socket, and cause the thread to finish */
    public synchronized void cancel() {
        try {
        	mBluetoothServerSocket.close();	
        } catch (IOException e) { Log.e(TAG, "close() of server failed", e); }
    }
    
}
