package co.gounplugged.unpluggeddroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import co.gounplugged.unpluggeddroid.activity.ChatActivity;
import es.theedg.hydra.HydraMsg;
import es.theedg.hydra.HydraPost;
import es.theedg.hydra.HydraPostDb;

import java.util.ArrayList;
import java.util.UUID;

public class UnpluggedMesh extends Thread implements HydraPostDb {
	private static final String TAG = "UnpluggedMesh";
	
	// Constants
	private final String serviceName;
    private final UUID uuid;
    
    // States
    private int connectionState;
    public final static int STATE_NONE = 0;
    public final static int STATE_DISCONNECTED = 1;
    public final static int STATE_CONNECTED = 2;
    public final static int STATE_LISTEN = 3;
    public final static int STATE_CONNECTING = 4;
    
    // GUI
    // The Handler that gets information back from the ConnectedThread
    private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private UnpluggedBluetoothClient unpluggedBluetoothClient;
	private UnpluggedBluetoothServer unpluggedBluetoothServer;
	private UnpluggedConnectedThread unpluggedConnectedThread;
	private ChatActivity parentActivity;
	
	protected ArrayList<HydraPost> hydraPosts;
	private boolean isBroadcasting;
	
	public UnpluggedMesh(String serviceName_, UUID uuid_, ChatActivity activity) {
		 this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 this.parentActivity = activity;
		 this.hydraPosts = new ArrayList<HydraPost>();
		 this.isBroadcasting = false;
		 this.connectionState = STATE_DISCONNECTED;
	}
		
	public boolean isBluetoothSupported() {
		return !(mBluetoothAdapter == null);
	}
	
	
	public boolean isBluetoothEnabled() {
		return mBluetoothAdapter.isEnabled();
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public boolean isDiscovering() {
		return mBluetoothAdapter.isDiscovering();
	}
	
	public void stopDiscovery() {
		Log.d(TAG, "stopDiscovery"); 
		if (isDiscovering()) {
			this.mBluetoothAdapter.cancelDiscovery();
    	}
	}
	
	public void startDiscovery() {
		Log.d(TAG, "startDiscovery"); 
		stopDiscovery();
		this.mBluetoothAdapter.startDiscovery();
		Log.d(TAG, "discovering now: " + isDiscovering());
	}
	
    
    public BluetoothDevice isBonded(BluetoothDevice bluetoothDevice) {
    	for(BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
    		if((device.getName()).equals(bluetoothDevice.getName())) return device;
    	}
    	return null;
    }
    
    public BluetoothDevice actualFromBonded(BluetoothDevice bondedBluetoothDevice) {
    	return this.mBluetoothAdapter.getRemoteDevice(bondedBluetoothDevice.getAddress());
    }
	
	public ArrayList<HydraPost> getHydraPosts() {
		return this.hydraPosts;
	}
	
	public void newHydraPost(int msgCode, String content) {
		hydraPosts.add(new HydraPost(content));
		mHandler.obtainMessage(msgCode, -1, -1, content.getBytes()).sendToTarget();
	}
	
	public void newHydraPost(int msgCode, HydraPost p) {
		hydraPosts.add(p);
		mHandler.obtainMessage(msgCode, -1, -1, p.getContent().getBytes()).sendToTarget();
	}
	
	@Override
	public void run() {
		while(true) {
			HydraMsg ping = HydraMsg.newHelloMsg();
			if (connectionState == STATE_CONNECTED && unpluggedConnectedThread != null){
				Log.d(TAG, "Sending ping through server");
				ping.send(unpluggedConnectedThread, this);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	 /**
	* Start the ConnectedThread to begin managing a Bluetooth connection
	* @param socket The BluetoothSocket on which the connection was made
	* @param device The BluetoothDevice that has been connected
	*/
	public synchronized void connectionEstablished(BluetoothSocket socket) {
		Log.d(TAG, "connected");
		
		// Cancel the thread that completed the connection
		resetClient();
		
		// Cancel any thread currently running a connection
		resetConnectedThread();
		
		// Cancel the accept thread because we only want to connect to one device
		resetServer();
		
		// Start the thread to manage the connection and perform transmissions
		unpluggedConnectedThread = new UnpluggedConnectedThread(socket, this);
		unpluggedConnectedThread.start();
		
		// Send the name of the connected device back to the UI Activity
		setConnectionState(STATE_CONNECTED);
	}

	public synchronized int getConnectionState() {
		return connectionState;
	}

	public synchronized void setConnectionState(int connectionState) {
		Log.d(TAG, "setState() " + this.connectionState + " -> " + connectionState);
		this.connectionState = connectionState;
		if(mHandler != null) mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, connectionState, -1).sendToTarget();
	}
	
	 public synchronized void connectClient(BluetoothDevice bluetoothDevice) {
		 Log.d(TAG, "connect to: " + bluetoothDevice);
		 if(getConnectionState() == STATE_DISCONNECTED) {
			 setConnectionState(STATE_CONNECTING);
			 resetClient();
			 resetConnectedThread();
			 // Start the thread to connect with the given device
			 unpluggedBluetoothClient = new UnpluggedBluetoothClient(this, bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
			 unpluggedBluetoothClient.start();
			 
			 Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
		 }
	}
	
	public synchronized void restartConnection() {
		Log.d(TAG, "restarClient");
		resetClient();
		resetConnectedThread();
		startAccepting();
		startDiscovery();
		if (!isBroadcasting) {
			parentActivity.startBroadcast();
			isBroadcasting = true;
		}
		setConnectionState(STATE_DISCONNECTED);
	}
	
	public synchronized void startAccepting() {
		if (unpluggedBluetoothServer == null) {
			unpluggedBluetoothServer = new UnpluggedBluetoothServer(this, mBluetoothAdapter, serviceName, uuid, mHandler);
			unpluggedBluetoothServer.start();
		}
	}
	
	public synchronized void resetClient() { if (unpluggedBluetoothClient != null) {unpluggedBluetoothClient.cancel(); clearClient(); } }
	public synchronized void clearClient() { unpluggedBluetoothClient = null; }
	public synchronized void resetConnectedThread() { if (unpluggedConnectedThread != null) {unpluggedConnectedThread.cancel(); unpluggedConnectedThread = null;} }
	public synchronized void resetServer() { if (unpluggedBluetoothServer != null) {unpluggedBluetoothServer.cancel(); unpluggedBluetoothServer = null; } }
	
	public synchronized void stopAll() {
		Log.d(TAG, "stop");
		resetClient();
		resetConnectedThread();
		resetServer();
		
		setConnectionState(STATE_DISCONNECTED);
	}

}