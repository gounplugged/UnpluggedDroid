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
	private final BluetoothSocket mBluetoothSocket;

	// Bluetooth SDK
    private BluetoothDevice mBluetoothDevice;
	
	public UnpluggedBluetoothClient(UnpluggedMesh unpluggedMesh, BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter, UUID uuid, Handler handler) {
		super(unpluggedMesh, bluetoothAdapter);
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket WAS final
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
	
	@Override
	public void run() {
		 Log.d(TAG, "BEGIN mConnectThread");
		 
		 // Always cancel discovery because it will slow down a connection
		 mUnpluggedMesh.stopDiscovery();
		 // Make a connection to the BluetoothSocket
		 try {
			 // This is a blocking call and will only return on a
			 // successful connection or an exception
			 Log.d(TAG, "trying connection");
			 mBluetoothSocket.connect();
			 Log.d(TAG, "socket connected");
		 } catch (IOException e) {
			// Close the socket
			 try {
				 Log.d(TAG, "close it");
				 mBluetoothSocket.close();
			 } catch (IOException e2) {
				 Log.d(TAG, "unable to close() socket during connection failure", e2);
			 }
			 // Start the service over to restart listening mode
			 mUnpluggedMesh.restartConnection();
			 return;
		 }
		 
		 // Reset the ConnectThread because we're done
		 synchronized (mUnpluggedMesh) {
			 Log.d(TAG, "clearing this from memory");
			 mUnpluggedMesh.clearClient();
		 }
		 
		 // Start the connected thread
		 mUnpluggedMesh.connectionEstablished(mBluetoothSocket);
	}
	
	public void cancel() {
		 Log.d(TAG, "cancel " + this);
		 try {
			 mBluetoothSocket.close();
		 } catch (IOException e) {
			 Log.e(TAG, "close() of server failed", e);
		 }
	}

}