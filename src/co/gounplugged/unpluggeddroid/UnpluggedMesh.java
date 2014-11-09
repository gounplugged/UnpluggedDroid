package co.gounplugged.unpluggeddroid;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class UnpluggedMesh {
	private static final String TAG = "UnpluggedMesh";
	
	// Constants
	private final boolean IS_SERVER;
	private final String serviceName;
    private final UUID uuid;
    
    public final static int STATE_NONE = 0;
    public final static int STATE_RUNNING = 1;
    private int state;
    
    // GUI
    // The Handler that gets information back from the ConnectedThread
    private Handler mHandler;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mBroadcastReceiver;
	
	private UnpluggedBluetoothClient unpluggedBluetoothClient;
	private UnpluggedBluetoothServer unpluggedBluetoothServer;
	
	public UnpluggedMesh(boolean isServer, String serviceName_, UUID uuid_) {
		 this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.IS_SERVER = isServer;
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 state = STATE_NONE;
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
    	
		if (IS_SERVER){
			startAccepting();
		}
	}
	
	public void startAccepting() {
		if (unpluggedBluetoothServer == null) unpluggedBluetoothServer = new UnpluggedBluetoothServer(mBluetoothAdapter, serviceName, uuid, mHandler);
		unpluggedBluetoothServer.accept();
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public int getState() {
		return this.state;
	}
	
	public void stop() {
    	if(unpluggedBluetoothClient != null) killUnpluggedBluetoothClient();
    	if(unpluggedBluetoothServer != null) killUnpluggedBluetoothServer();
	}
	
    private void killUnpluggedBluetoothClient() {
    	unpluggedBluetoothClient.cancel();
    	unpluggedBluetoothClient = null;
    }
    
    private void killUnpluggedBluetoothServer() {
    	unpluggedBluetoothServer.cancel();
    	unpluggedBluetoothServer = null;
    }
    
    private void connectClient(BluetoothDevice bluetoothDevice) {
    	if(bluetoothDevice.getName().equals("motop")){
    		if(unpluggedBluetoothClient == null) unpluggedBluetoothClient = new UnpluggedBluetoothClient(bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
    		if (unpluggedBluetoothClient.state == UnpluggedNode.DISCONNECTED) unpluggedBluetoothClient.connect();
	        Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
    	}
    }
   
    
    public void sendMessage(String str) {
		if (unpluggedBluetoothServer != null){
			unpluggedBluetoothServer.chat(str);
		} else if (unpluggedBluetoothClient != null){
			unpluggedBluetoothClient.chat(str);
		}
    }
}
