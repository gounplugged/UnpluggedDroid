package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class UnpluggedBluetoothServer implements Runnable {
	
	// Constants
	private String TAG = "UnpluggedBluetoothServer";
	private String serviceName;
	private UUID uuid;
	
	// Bluetooth SDK
	private BluetoothAdapter mBluetoothAdapter;
	private final BluetoothServerSocket mBluetoothServerSocket;
	
	public UnpluggedBluetoothServer(BluetoothAdapter bluetoothAdapter, String serviceName, UUID uuid_) {
		this.mBluetoothAdapter = bluetoothAdapter;
		this.serviceName = serviceName;
		this.uuid = uuid_;
		
        // Use a temporary object that is later assigned to mBluetoothServerSocket,
        // because mBluetoothServerSocket is final
        BluetoothServerSocket tBluetoothServerSocket = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
        	tBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(serviceName, uuid_);
        	Log.d(TAG, "listenUsingInsecureRfcommWithServiceRecord");
        } catch (IOException e) { }
        mBluetoothServerSocket = tBluetoothServerSocket;
	}
	
	public void run() {
		BluetoothSocket socket = null;
	    // Keep listening until exception occurs or a socket is returned
	    while (true) {
	        try {
	        	Log.d(TAG, "attempting connection");
	            socket = mBluetoothServerSocket.accept();
	        	Log.d(TAG, "connection accepted");

	        } catch (IOException e) {
	            break;
	        }
	        // If a connection was accepted
	        if (socket != null) {
	        	Log.d(TAG, "connection accepted");
	            // Do work to manage the connection (in a separate thread)
//	            manageConnectedSocket(socket);
	            cancel();
	            break;
	        }
	    }
	}
	
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
        	mBluetoothServerSocket.close();
        } catch (IOException e) { }
    }
}
