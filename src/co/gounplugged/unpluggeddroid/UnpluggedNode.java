package co.gounplugged.unpluggeddroid;

import android.bluetooth.BluetoothAdapter;

public class UnpluggedNode extends Thread {
	protected final UnpluggedMesh mUnpluggedMesh;
	protected final BluetoothAdapter mBluetoothAdapter;
	
	public UnpluggedNode(UnpluggedMesh unpluggedMesh, BluetoothAdapter bluetoothAdapter) {
		this.mUnpluggedMesh = unpluggedMesh;
		this.mBluetoothAdapter = bluetoothAdapter;
	}

}
