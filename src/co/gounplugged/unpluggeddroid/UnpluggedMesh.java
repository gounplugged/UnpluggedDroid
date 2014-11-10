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
	
	public UnpluggedMesh(String serviceName_, UUID uuid_, ChatActivity activity) {
		 this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 this.parentActivity = activity;
	}
		
	public boolean isBluetoothSupported() {
		return !(mBluetoothAdapter == null);
	}
	
	public boolean isBluetoothEnabled() {
		return mBluetoothAdapter.isEnabled();
	}
	
	public synchronized void start() {
    	Log.d(TAG, "start()");
    	stop();
    	
		parentActivity.startBroadcast();
		startAccepting();
		startDiscovery();

    	
//		if (isServer) {
//			parentActivity.startBroadcast();
//			startAccepting();
//		} else {
//			startDiscovery();
//		}
	}
	
	public synchronized void startAccepting() {
		if (unpluggedBluetoothServer == null) unpluggedBluetoothServer = new UnpluggedBluetoothServer(mBluetoothAdapter, serviceName, uuid, mHandler);
		unpluggedBluetoothServer.accept();
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public synchronized void connectClient(BluetoothDevice bluetoothDevice) {
		if(unpluggedBluetoothClient == null) unpluggedBluetoothClient = new UnpluggedBluetoothClient(bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
//		if (unpluggedBluetoothClient.state == UnpluggedNode.DISCONNECTED) 
			unpluggedBluetoothClient.connect();
        Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
	}
	
	public boolean isDiscovering() {
		return mBluetoothAdapter.isDiscovering();
	}
	
	public void stopDiscovery() {
		Log.d(TAG, "stopDiscovery"); 
		if (isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
    	}
	}
	
	public synchronized void startDiscovery() {
		stopDiscovery();
		Log.d(TAG, "startDiscovery"); 
		mBluetoothAdapter.startDiscovery();
	}
	
	public synchronized void stop() {
    	if(unpluggedBluetoothClient != null) killUnpluggedBluetoothClient();
    	if(unpluggedBluetoothServer != null) killUnpluggedBluetoothServer();
	}
	
    private synchronized void killUnpluggedBluetoothClient() {
    	unpluggedBluetoothClient.cancel();
    	unpluggedBluetoothClient = null;
    }
    
    private synchronized void killUnpluggedBluetoothServer() {
    	unpluggedBluetoothServer.cancel();
    	unpluggedBluetoothServer = null;
    }
    
    public void sendMessage(String str) {
		if (unpluggedBluetoothServer != null){
			unpluggedBluetoothServer.chat(str);
		} else if (unpluggedBluetoothClient != null){
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
    	return mBluetoothAdapter.getRemoteDevice(bondedBluetoothDevice.getAddress());
    }
}
