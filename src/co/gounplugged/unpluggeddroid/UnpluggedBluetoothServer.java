package co.gounplugged.unpluggeddroid;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class UnpluggedBluetoothServer extends Thread {
	
	// State
	public static final int DISCONNECTED = 0;
	public static final int CONNECTED = 1;
	private int state;
	
	// Constants
	private String TAG = "UnpluggedBluetoothServer";
	private String serviceName;
	private UUID uuid;
	
	// Bluetooth SDK
	private BluetoothAdapter mBluetoothAdapter;
	private final BluetoothServerSocket mBluetoothServerSocket;
	
	// Unplugged Objects
	ConnectedThread connectedThread;
	private final Handler mHandler;
	
	public UnpluggedBluetoothServer(BluetoothAdapter bluetoothAdapter, String serviceName, UUID uuid, Handler handler) {
		this.mBluetoothAdapter = bluetoothAdapter;
		this.serviceName = serviceName;
		this.uuid = uuid;
		state = DISCONNECTED;
		
        // Use a temporary object that is later assigned to mBluetoothServerSocket,
        // because mBluetoothServerSocket is final
        BluetoothServerSocket tBluetoothServerSocket = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
        	tBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(serviceName, uuid);
        	Log.d(TAG, "listenUsingInsecureRfcommWithServiceRecord");
        } catch (IOException e) {  Log.e(TAG, "listen() failed", e); }
        mBluetoothServerSocket = tBluetoothServerSocket;
        mHandler = handler;
	}
	
	public void run() {
		BluetoothSocket bluetoothSocket = null;
	    // Keep listening until exception occurs or a socket is returned
	    while (true) {
	        try {
	        	Log.d(TAG, "attempting connection");
	        	bluetoothSocket = mBluetoothServerSocket.accept();
	        	Log.d(TAG, "connection accepted");
	        	connectedThread = new ConnectedThread(bluetoothSocket, mHandler);
	        	connectedThread.start();
	        	state = CONNECTED;
	        } catch (IOException e) {
	            break;
	        }
	        // If a connection was accepted
	        if (bluetoothSocket != null) {
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
    		state = DISCONNECTED;
        } catch (IOException e) { Log.e(TAG, "close() of server failed", e); }
    }
    
    public void chat(String str) {
    	Log.d(TAG, "writing chat");
    	if (state == CONNECTED) {
    		connectedThread.write(str.getBytes());		
    	}
    }
}
