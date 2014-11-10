package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class UnpluggedBluetoothClient extends UnpluggedNode {
	
	// Constants
	private String TAG = "UnpluggedBluetoothClient";

	// Bluetooth SDK
    private BluetoothDevice mBluetoothDevice;
	
	public UnpluggedBluetoothClient(BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter, UUID uuid, Handler handler) {
		super(handler, bluetoothAdapter, uuid, " client ");
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
	        	setState(CONNECTED);       	
	        } catch (IOException connectException) {
	        	Log.d(TAG, "connection failed");
	            cancel();
	        }
        }
 
        // Do work to manage the connection (in a separate thread)
//        manageConnectedSocket(mBluetoothSocket);
    }


    public synchronized void cancel() {
        super.cancel();
    }
    
    public void connect() {
    	setState(CONNECTING);
    	start();
    }
    

}