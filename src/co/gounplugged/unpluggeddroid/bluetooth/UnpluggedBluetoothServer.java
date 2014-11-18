package co.gounplugged.unpluggeddroid.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class UnpluggedBluetoothServer extends Thread {
	
	// Constants
	private String TAG = "UnpluggedBluetoothServer";
	
	// Bluetooth SDK
	private final BluetoothServerSocket mBluetoothServerSocket;
	private final UnpluggedBluetoothManager mUnpluggedBluetoothManager;
	private final BluetoothAdapter mBluetoothAdapter;
		
	public UnpluggedBluetoothServer(UnpluggedBluetoothManager unpluggedBluetoothManager, BluetoothAdapter bluetoothAdapter, String serviceName, UUID uuid) {
		this.mUnpluggedBluetoothManager = unpluggedBluetoothManager;
		this.mBluetoothAdapter = bluetoothAdapter;
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
	        	if (mUnpluggedBluetoothManager.getConnectionState() != UnpluggedBluetoothManager.STATE_CONNECTED) {
	        		mUnpluggedBluetoothManager.connectionEstablished(bluetoothSocket);
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
