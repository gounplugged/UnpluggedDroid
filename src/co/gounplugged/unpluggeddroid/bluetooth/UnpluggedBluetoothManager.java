package co.gounplugged.unpluggeddroid.bluetooth;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import co.gounplugged.unpluggeddroid.UnpluggedConnectionManager;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;
import co.gounplugged.unpluggeddroid.activity.ChatActivity;
import es.theedg.hydra.HydraMsg;
import es.theedg.hydra.HydraPostDb;

public class UnpluggedBluetoothManager implements UnpluggedConnectionManager {
	private final String TAG = "UnpluggedBluetoothManager";
	
    // States
    private int connectionState;
    public final static int STATE_NONE = 0;
    public final static int STATE_DISCONNECTED = 1;
    public final static int STATE_CONNECTED = 2;
    public final static int STATE_LISTEN = 3;
    public final static int STATE_CONNECTING = 4;
    
	private boolean isBroadcasting;
	private final BluetoothAdapter mBluetoothAdapter;
	private UnpluggedMesh mUnpluggedMesh;
	private UnpluggedBluetoothClient unpluggedBluetoothClient;
	private UnpluggedBluetoothServer unpluggedBluetoothServer;
	private UnpluggedBluetoothHydraMsgOutput unpluggedConnectedThread;
	
	public UnpluggedBluetoothManager(UnpluggedMesh unpluggedMesh) {
		 this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.connectionState = STATE_DISCONNECTED;
		 this.isBroadcasting = false;
		 this.mUnpluggedMesh = unpluggedMesh;
	}
	
	public synchronized int getConnectionState() {
		return connectionState;
	}

	public synchronized void setConnectionState(int connectionState) {
		Log.d(TAG, "setState() " + this.connectionState + " -> " + connectionState);
		this.connectionState = connectionState;
//		if(mHandler != null) mHandler.obtainMessage(UnpluggedMessageHandler.STATE_CHANGED, connectionState, -1).sendToTarget();
	}
	
	 public synchronized void connectClient(BluetoothDevice bluetoothDevice) {
		 Log.d(TAG, "connect to [" + getConnectionState() + "]: " + bluetoothDevice);
		 if(getConnectionState() == STATE_DISCONNECTED) {
			 setConnectionState(STATE_CONNECTING);
			 resetClient();
			 resetConnectedThread();
			 // Start the thread to connect with the given device
			 unpluggedBluetoothClient = new UnpluggedBluetoothClient(this, bluetoothDevice);
			 unpluggedBluetoothClient.start();
			 
			 Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
		 }
	}
	
	public synchronized void restartConnection(ChatActivity parentActivity) {
		Log.d(TAG, "restarClient");
		resetClient();
		resetConnectedThread();
		startAccepting();
		startDiscovery();
		if (!isBroadcasting) {
			parentActivity.startBroadcast();
			isBroadcasting = true;
		}
//		setConnectionState(STATE_DISCONNECTED);
	}
	
	public synchronized void startAccepting() {
		if (unpluggedBluetoothServer == null) {
			unpluggedBluetoothServer = new UnpluggedBluetoothServer(this, mBluetoothAdapter, getServiceName(), getUuid());
			unpluggedBluetoothServer.start();
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
		unpluggedConnectedThread = new UnpluggedBluetoothHydraMsgOutput(socket, this);
		unpluggedConnectedThread.start();
		
		// Send the name of the connected device back to the UI Activity
		setConnectionState(STATE_CONNECTED);
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
	
	public boolean isBluetoothSupported() {
		return !(mBluetoothAdapter == null);
	}
	
	public boolean isBluetoothEnabled() {
		return mBluetoothAdapter.isEnabled();
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

	@Override
	public void ping() {
		HydraMsg ping = HydraMsg.newHelloMsg();
		if (connectionState == STATE_CONNECTED && unpluggedConnectedThread != null){
			Log.d(TAG, "Sending ping through server");
			ping.send(unpluggedConnectedThread, getHydraPostDb());
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getServiceName() {
		return mUnpluggedMesh.getServiceName();
	}

	@Override
	public UUID getUuid() {
		return mUnpluggedMesh.getUuid();
	}

	@Override
	public HydraPostDb getHydraPostDb() {
		return mUnpluggedMesh;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resumeConnection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
}
