package co.gounplugged.unpluggeddroid;

import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.ParcelUuid;
import co.gounplugged.unpluggeddroid.activity.ChatActivity;
import co.gounplugged.unpluggeddroid.ble.UnpluggedBleManager;
import co.gounplugged.unpluggeddroid.bluetooth.UnpluggedBluetoothManager;
import es.theedg.hydra.HydraPost;
import es.theedg.hydra.HydraPostDb;

public class UnpluggedMesh extends Thread implements HydraPostDb {
	private static final String TAG = "UnpluggedMesh";
	
	// Constants
	private final String serviceName;
    private final UUID uuid;
    
    // GUI
    private Handler mHandler;
	private ChatActivity parentActivity;
	protected ArrayList<HydraPost> hydraPosts;
	private UnpluggedBluetoothManager mUnpluggedBluetoothManager;
	private final UnpluggedBleManager mUnpluggedBleManager;
	
	public UnpluggedMesh(String serviceName_, UUID uuid_, ChatActivity activity, BluetoothAdapter bluetoothAdapter) {
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 this.parentActivity = activity;
		 this.hydraPosts = new ArrayList<HydraPost>();
		 
		 if(bluetoothAdapter != null) {
//			 this.mUnpluggedBluetoothManager = new UnpluggedBluetoothManager(this);
			 this.mUnpluggedBleManager = new UnpluggedBleManager(bluetoothAdapter);
		 } else {
			 this.mUnpluggedBleManager = null;
		 }
	}
	
	public void ping() {
		start();
	}
		
	@Override
	public void run() {
		while(true) {
//			mUnpluggedBluetoothManager.ping();
			if(mUnpluggedBleManager != null) mUnpluggedBleManager.ping();
		}
	}
	
	public void resumeConnections() {
		if(mUnpluggedBleManager != null) {
			if(mUnpluggedBleManager.isEnabled() ) {
				mUnpluggedBleManager.resumeConnection();
			} else {
				parentActivity.enableBle();
			}
		}
	}
	
	///////////////////////////////////////////////////////////////
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public ArrayList<HydraPost> getHydraPosts() {
		return this.hydraPosts;
	}
	
	public void addHydraPost(int msgCode, String content) {
		hydraPosts.add(new HydraPost(content));
		mHandler.obtainMessage(msgCode, -1, -1, content.getBytes()).sendToTarget();
	}
	
	public void addHydraPost(int msgCode, HydraPost p) {
		hydraPosts.add(p);
		mHandler.obtainMessage(msgCode, -1, -1, p.getContent().getBytes()).sendToTarget();
	}

	public boolean areConnectionsEnabled() {
		return mUnpluggedBleManager.isEnabled();
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public String getServiceName() {
		return serviceName;
	}

	public boolean isBluetoothEnabled() {
		return mUnpluggedBluetoothManager.isBluetoothEnabled();
	}

	public void stopAll() {
		mUnpluggedBluetoothManager.stopAll();	
	}

	public void stopDiscovery() {
		mUnpluggedBluetoothManager.stopDiscovery();
	}

	public void connectClient(BluetoothDevice bluetoothDevice) {
		mUnpluggedBluetoothManager.connectClient(bluetoothDevice);
	}

	public BluetoothDevice isBonded(BluetoothDevice bluetoothDevice) {
		return mUnpluggedBluetoothManager.isBonded(bluetoothDevice);
	}

	public BluetoothDevice actualFromBonded(BluetoothDevice bondedBluetoothDevice) {
		return mUnpluggedBluetoothManager.actualFromBonded(bondedBluetoothDevice);
	}

	public void restartConnection(ChatActivity parentActivity) {
		mUnpluggedBluetoothManager.restartConnection(parentActivity);		
	}
}