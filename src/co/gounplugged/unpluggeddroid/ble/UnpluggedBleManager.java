package co.gounplugged.unpluggeddroid.ble;

import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.util.Log;
import co.gounplugged.unpluggeddroid.UnpluggedConnectionManager;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;
import es.theedg.hydra.HydraMsg;
import es.theedg.hydra.HydraMsgOutput;
import es.theedg.hydra.HydraPostDb;

public class UnpluggedBleManager implements UnpluggedConnectionManager {
	private static final String TAG = "UnpluggedBleManager";
	
	// Initializes Bluetooth adapter.
	private final BluetoothAdapter mBluetoothAdapter;
	private final UnpluggedBleHydraMsgOutput mUnpluggedBleHydraMsgOutput;
	private final UnpluggedMesh mUnpluggedMesh;
	private final UnpluggedBleAdvertiseCallback mUnpluggedBleAdvertiseCallback;
	
	private static final int ADVERTISE_PERIOD = 1000; // ms

    private boolean isScanning;
	
	public UnpluggedBleManager(BluetoothAdapter bluetoothAdapter, UnpluggedMesh unpluggedMesh) {
		this.mBluetoothAdapter = bluetoothAdapter;
		this.mUnpluggedBleHydraMsgOutput = new UnpluggedBleHydraMsgOutput(bluetoothAdapter);
		this.mUnpluggedMesh = unpluggedMesh;
		this.isScanning = false;
		this.mUnpluggedBleAdvertiseCallback = new UnpluggedBleAdvertiseCallback();
		
	}
	
	@Override
	public void ping() {
		HydraMsg ping = HydraMsg.newHelloMsg();
		ping.send(mUnpluggedBleHydraMsgOutput, getHydraPostDb());
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

	public boolean isEnabled() {
		return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
	}

	@Override
	public void resumeConnection() {
		if(!isScanning) {
			ScanFilter.Builder sfb = new ScanFilter.Builder();
			sfb.setServiceUuid(mUnpluggedMesh.getParcelUuid());
			ArrayList<ScanFilter> sfl = new ArrayList<ScanFilter>();
			sfl.add(sfb.build());
			
			ScanSettings.Builder ssb = new ScanSettings.Builder();
			ssb.setReportDelay(0);
			ssb.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
			ScanSettings ss = ssb.build();
			
			mBluetoothAdapter.getBluetoothLeScanner().startScan(sfl, ss, new ScanCallback() {
				 public void onScanResult(int callbackType, ScanResult result) {
					 ScanRecord sr = result.getScanRecord();
					 byte[] buffer = sr.getServiceData(mUnpluggedMesh.getParcelUuid());
					 HydraMsg hydraMsg = new HydraMsg(buffer);
					 hydraMsg.send(mUnpluggedBleHydraMsgOutput, getHydraPostDb());
				 }
			});
			isScanning = true;
		}
	}
	
	class UnpluggedScanCallback extends ScanCallback {
		public void onScanResult(int callbackType, ScanResult result) {
			 ScanRecord sr = result.getScanRecord();
			 byte[] buffer = sr.getServiceData(mUnpluggedMesh.getParcelUuid());
			 HydraMsg hydraMsg = new HydraMsg(buffer);
			 hydraMsg.send(mUnpluggedBleHydraMsgOutput, getHydraPostDb());
		 }
	}
	
	class UnpluggedBleHydraMsgOutput implements HydraMsgOutput {
		
		private final BluetoothAdapter mBluetoothAdapter;
		
		public UnpluggedBleHydraMsgOutput(BluetoothAdapter bluetoothAdapter) {
			this.mBluetoothAdapter = bluetoothAdapter;
		}
	
		@Override
		public void write(byte[] bytes) {
			Log.d(TAG, "Starting write attempt");
			if(mBluetoothAdapter.isMultipleAdvertisementSupported()){
				BluetoothLeAdvertiser bluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
				bluetoothAdvertiser.stopAdvertising(mUnpluggedBleAdvertiseCallback);
				Log.d(TAG, "bluetoothAdvertiser not null on write attempt");
				AdvertiseSettings.Builder asb = new AdvertiseSettings.Builder();
				asb.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
				asb.setConnectable(false);
				AdvertiseSettings as = asb.build();
				
				AdvertiseData.Builder adb = new AdvertiseData.Builder();
				adb.addServiceData(mUnpluggedMesh.getParcelUuid(), bytes);
				AdvertiseData ad = adb.build();
				
				bluetoothAdvertiser.startAdvertising(as, ad, mUnpluggedBleAdvertiseCallback);
//				new Thread(new Runnable() {
//	                @Override
//	                public void run() {
//	                	try {
//							Thread.sleep(ADVERTISE_PERIOD);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//	                	mBluetoothAdapter.getBluetoothLeAdvertiser().stopAdvertising(mUnpluggedBleAdvertiseCallback);
//	                }
//	            }).run();

			} else {
				Log.d(TAG, "bluetoothAdvertiser NULL on write attempt");
			}
			
		}
	
	}

	@Override
	public void stop() {
		
	}
	
	class UnpluggedBleAdvertiseCallback extends AdvertiseCallback {
		@Override
		public void onStartFailure(int errorCode) {
			Log.d(TAG, "Advertising failure");
		}
		
		@Override
		public void onStartSuccess(AdvertiseSettings settingsInEffect) {
			Log.d(TAG, "Advertising success");
		}
	}
}