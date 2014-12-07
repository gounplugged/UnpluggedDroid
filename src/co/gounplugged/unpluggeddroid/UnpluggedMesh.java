package co.gounplugged.unpluggeddroid;

import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.ParcelUuid;
import co.gounplugged.unpluggeddroid.activity.ChatActivity;
import co.gounplugged.unpluggeddroid.ble.UnpluggedBleManager;
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
	
	// HydraPostDb
	protected ArrayList<HydraPost> hydraPosts;
	
	// Connection Managers
//	private UnpluggedBluetoothManager mUnpluggedBluetoothManager;
	private final UnpluggedBleManager mUnpluggedBleManager;
	
	private boolean pinging;
	
	private static final int PING_RATE = 2000;
	
	public UnpluggedMesh(String serviceName_, UUID uuid_, ChatActivity activity, BluetoothAdapter bluetoothAdapter) {
		 this.serviceName = serviceName_;
		 this.uuid = uuid_;
		 this.parentActivity = activity;
		 this.hydraPosts = new ArrayList<HydraPost>();
		 this.pinging = false;
		 
		 if(bluetoothAdapter != null) {
//			 this.mUnpluggedBluetoothManager = new UnpluggedBluetoothManager(this);
			 this.mUnpluggedBleManager = new UnpluggedBleManager(bluetoothAdapter, this);
		 } else {
			 this.mUnpluggedBleManager = null;
		 }
	}
	
	public void ping() {
		if(!pinging) {
			pinging = true;
			start();
		}
	}
		
	@Override
	public void run() {
		while(pinging) {
//			mUnpluggedBluetoothManager.ping();
			if(mUnpluggedBleManager != null) mUnpluggedBleManager.ping();
			try {
				Thread.sleep(PING_RATE);
			} catch (InterruptedException e) {
				pinging = false;
			}
		}
	}
	
    //////////////////////////////////////   Connection lifecycle    ////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void resumeConnections() {
		if(mUnpluggedBleManager != null) {
			if(mUnpluggedBleManager.isEnabled() ) {
				mUnpluggedBleManager.resumeConnection();
			} else {
				parentActivity.enableBle();
			}
		}
	}
	
	public void stopAll() {
//		mUnpluggedBluetoothManager.stopAll();	
		if(mUnpluggedBleManager != null) mUnpluggedBleManager.stop();
		pinging = false;
	}
	
    ///////////////////////////////////////   Hydra stuff    ////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
    /////////////////////////////////////   Connection helpers   ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public boolean areConnectionsEnabled() {
		return mUnpluggedBleManager.isEnabled();
	}

    ///////////////////////////////////////   Getters & Setters  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public UUID getUuid() {
		return uuid;
	}

	public String getServiceName() {
		return serviceName;
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public ParcelUuid getParcelUuid() {
		return new ParcelUuid(ChatActivity.Uuid);
	}

}