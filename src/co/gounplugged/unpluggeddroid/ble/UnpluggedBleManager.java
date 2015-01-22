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

import co.gounplugged.unpluggeddroid.UnpluggedConnectionManager;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;
import es.theedg.hydra.HydraMsg;
import es.theedg.hydra.HydraMsgOutput;
import es.theedg.hydra.HydraPostDb;

public class UnpluggedBleManager implements UnpluggedConnectionManager {
	// Initializes Bluetooth adapter.
	private final BluetoothAdapter mBluetoothAdapter;
	private final UnpluggedBleHydraMsgOutput mUnpluggedBleHydraMsgOutput;
	private final UnpluggedMesh mUnpluggedMesh;

    private boolean isScanning;
	
	public UnpluggedBleManager(BluetoothAdapter bluetoothAdapter, UnpluggedMesh unpluggedMesh) {
		this.mBluetoothAdapter = bluetoothAdapter;
		this.mUnpluggedBleHydraMsgOutput = new UnpluggedBleHydraMsgOutput(bluetoothAdapter);
		this.mUnpluggedMesh = unpluggedMesh;
		 this.isScanning = false;
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
			BluetoothLeAdvertiser bluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
			AdvertiseSettings.Builder asb = new AdvertiseSettings.Builder();
			asb.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
			asb.setConnectable(false);
			AdvertiseSettings as = asb.build();
			
			AdvertiseData.Builder adb = new AdvertiseData.Builder();
			adb.addServiceData(mUnpluggedMesh.getParcelUuid(), bytes);
			AdvertiseData ad = adb.build();

            if (bluetoothAdvertiser != null) {
                bluetoothAdvertiser.startAdvertising(as, ad, new AdvertiseCallback() {
                    @Override
                    public void onStartFailure(int errorCode) {

                    }

                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {

                    }
                });
            }
			
		}
	
	}

	@Override
	public void stop() {
		
	}
}
