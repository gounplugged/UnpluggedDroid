package co.gounplugged.unpluggeddroid;

import java.util.ArrayList;
import java.util.UUID;

import es.theedg.hydra.HydraMsg;
import es.theedg.hydra.HydraPost;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

public class UnpluggedMesh extends Thread {
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
	protected ArrayList<HydraPost> hydraPosts;
	
	public UnpluggedMesh(String serviceName_, UUID uuid_, ChatActivity activity) {
		 this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 this.parentActivity = activity;
		 this.setBroadcasting(false);
		 this.hydraPosts = new ArrayList<HydraPost>();
	}
		
	public boolean isBluetoothSupported() {
		return !(mBluetoothAdapter == null);
	}
	
	public boolean isBluetoothEnabled() {
		return mBluetoothAdapter.isEnabled();
	}
	
	public synchronized void startAll() {
		Log.d(TAG, ">>START: Broadcasting: " + isBroadcasting() + ", Server: " + isServerConnected() + ", Client: " + isClientConnected() + ", Discovering: " + isDiscovering());

    	Log.d(TAG, "start()");
    	stopAll();
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
		if (unpluggedBluetoothServer == null) unpluggedBluetoothServer = new UnpluggedBluetoothServer(this, mBluetoothAdapter, serviceName, uuid, mHandler);
		if (unpluggedBluetoothServer.state == UnpluggedNode.DISCONNECTED) unpluggedBluetoothServer.accept();
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public synchronized void connectClient(BluetoothDevice bluetoothDevice) {
		if(unpluggedBluetoothClient == null) unpluggedBluetoothClient = new UnpluggedBluetoothClient(this, bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
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
	
	public synchronized void stopAll() {
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
			if (unpluggedBluetoothServer != null && unpluggedBluetoothServer.getConnectionState() == UnpluggedNode.CONNECTED){
				Log.d(TAG, "Sending ping through server");
				ping.send(unpluggedBluetoothServer.getConnectedThread(), this);
			} else if (unpluggedBluetoothClient != null && unpluggedBluetoothClient.getConnectionState() == UnpluggedNode.CONNECTED){
				Log.d(TAG, "Sending ping through client");
				ping.send(unpluggedBluetoothClient.getConnectedThread(), this);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
