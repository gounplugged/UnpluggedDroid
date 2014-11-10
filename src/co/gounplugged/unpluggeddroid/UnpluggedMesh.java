package co.gounplugged.unpluggeddroid;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

public class UnpluggedMesh {
	private static final String TAG = "UnpluggedMesh";
	
	// Constants
	private final String serviceName;
    private final UUID uuid;
    
    // GUI
    // The Handler that gets information back from the ConnectedThread
    private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private UnpluggedBluetoothClient unpluggedBluetoothClient;
	private UnpluggedBluetoothServer unpluggedBluetoothServer;
	private ChatActivity parentActivity;
	
	private boolean isBroadcasting;
	
	public UnpluggedMesh(String serviceName_, UUID uuid_, ChatActivity activity) {
		 this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 this.parentActivity = activity;
		 this.setBroadcasting(false);
	}
		
	public boolean isBluetoothSupported() {
		return !(mBluetoothAdapter == null);
	}
	
	public boolean isBluetoothEnabled() {
		return mBluetoothAdapter.isEnabled();
	}
	
	public synchronized void start() {
		Log.d(TAG, ">>START: Broadcasting: " + isBroadcasting() + ", Server: " + isServerConnected() + ", Client: " + isClientConnected() + ", Discovering: " + isDiscovering());

    	Log.d(TAG, "start()");
    	stop();
    	restart();
	}
	
	public synchronized void restart() {
		Log.d(TAG, "RESTART: Broadcasting: " + isBroadcasting() + ", Server: " + isServerConnected() + ", Client: " + isClientConnected() + ", Discovering: " + isDiscovering());
		if (!isBroadcasting()) parentActivity.startBroadcast();
		if (!isServerConnected()) startAccepting();
		if (!isClientConnected() && !isDiscovering()) startDiscovery();
		Log.d(TAG, "<<<<RESTART: Broadcasting: " + isBroadcasting() + ", Server: " + isServerConnected() + ", Client: " + isClientConnected() + ", Discovering: " + isDiscovering());

	}
	
	public synchronized void startAccepting() {
		if (unpluggedBluetoothServer == null) unpluggedBluetoothServer = new UnpluggedBluetoothServer(mBluetoothAdapter, serviceName, uuid, mHandler);
		if (unpluggedBluetoothServer.state == UnpluggedNode.DISCONNECTED) unpluggedBluetoothServer.accept();
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public synchronized void connectClient(BluetoothDevice bluetoothDevice) {
		if(unpluggedBluetoothClient == null) unpluggedBluetoothClient = new UnpluggedBluetoothClient(bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
		if (unpluggedBluetoothClient.state == UnpluggedNode.DISCONNECTED) unpluggedBluetoothClient.connect();
        Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
	}
	
	private boolean isServerConnected() {
		return (unpluggedBluetoothServer != null) && 
				(unpluggedBluetoothServer.state != UnpluggedNode.DISCONNECTED);
	}
	
	private boolean isClientConnected() {
		return (unpluggedBluetoothClient != null) && 
				(unpluggedBluetoothClient.state == UnpluggedNode.DISCONNECTED);
	}
	
	public boolean isDiscovering() {
		return mBluetoothAdapter.isDiscovering();
	}
	
	public synchronized void stopDiscovery() {
		Log.d(TAG, "stopDiscovery"); 
		if (isDiscovering()) {
			this.mBluetoothAdapter.cancelDiscovery();
    	}
	}
	
	public synchronized void startDiscovery() {
		stopDiscovery();
		Log.d(TAG, "startDiscovery"); 
		this.mBluetoothAdapter.startDiscovery();
		Log.d(TAG, "discovering now: " + isDiscovering());
	}
	
	public synchronized void stop() {
    	if(unpluggedBluetoothClient != null) killUnpluggedBluetoothClient();
    	if(unpluggedBluetoothServer != null) killUnpluggedBluetoothServer();
    	stopDiscovery();
	}
	
    private synchronized void killUnpluggedBluetoothClient() {
    	this.unpluggedBluetoothClient.cancel();
    	this.unpluggedBluetoothClient = null;
    }
    
    private synchronized void killUnpluggedBluetoothServer() {
    	this.unpluggedBluetoothServer.cancel();
    	this.unpluggedBluetoothServer = null;
    }
    
    public void sendMessage(String str) {
		if (unpluggedBluetoothServer != null && unpluggedBluetoothServer.getConnectionState() == UnpluggedNode.CONNECTED){
			unpluggedBluetoothServer.chat(str);
		} else if (unpluggedBluetoothClient != null && unpluggedBluetoothClient.getConnectionState() == UnpluggedNode.CONNECTED){
			unpluggedBluetoothClient.chat(str);
		}
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

	public boolean isBroadcasting() {
		return this.isBroadcasting;
	}

	public void setBroadcasting(boolean isBroadcasting) {
		this.isBroadcasting = isBroadcasting;
	}

}
