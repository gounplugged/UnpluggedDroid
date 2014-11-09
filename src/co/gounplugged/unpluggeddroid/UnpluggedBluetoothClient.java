package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class UnpluggedBluetoothClient extends Thread {
	
	// State
	public static final int DISCONNECTED = 0;
	public static final int CONNECTED = 1;
	private int state;
	
	// Constants
	private String TAG = "UnpluggedBluetoothClient";
	
	// Bluetooth SDK
    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mBluetoothDevice;
	private BluetoothAdapter mBluetoothAdapter;
	
	// Unplugged Objects
	ConnectedThread connectedThread;
	
	public UnpluggedBluetoothClient(BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter, UUID uuid) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.mBluetoothDevice = bluetoothDevice;
        this.mBluetoothAdapter = bluetoothAdapter;
        BluetoothSocket tBluetoothSocket = null;
		state = DISCONNECTED;
		
 
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
        	connectedThread = new ConnectedThread(mBluetoothSocket);
        	connectedThread.start();
        	state = CONNECTED;
//        	Connected

        } catch (IOException connectException) {
            cancel();
        }
 
        // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mBluetoothSocket);
    }
    
    public void chat(String str) {
    	if (state == CONNECTED) {
    		connectedThread.write(str.getBytes());		
    	}
    }
 

    public void cancel() {
        try {
        	mBluetoothSocket.close();
    		state = DISCONNECTED;
        } catch (IOException e) { }
    }
}