package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class UnpluggedBluetoothClient implements Runnable {
	
	// Constants
	private String TAG = "UnpluggedBluetoothClient";
	
	// Bluetooth SDK
    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mBluetoothDevice;
	private BluetoothAdapter mBluetoothAdapter;
	
	public UnpluggedBluetoothClient(BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter, UUID uuid) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.mBluetoothDevice = bluetoothDevice;
        this.mBluetoothAdapter = bluetoothAdapter;
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
 
        try {
        	Log.d(TAG, "attempting connection");
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
        	mBluetoothSocket.connect();
        	Log.d(TAG, "connection accepted");

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
            	mBluetoothSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mBluetoothSocket);
    }
 

    public void cancel() {
        try {
        	mBluetoothSocket.close();
        } catch (IOException e) { }
    }
}