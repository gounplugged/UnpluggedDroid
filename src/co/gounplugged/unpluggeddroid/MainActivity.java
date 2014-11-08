package co.gounplugged.unpluggeddroid;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	// Debug
	private final String TAG = "MainActivity";
	
	// Constants
	private static boolean IS_SERVER = true;
	private static int REQUEST_ENABLE_BT = 1;
	private static int REQUEST_ENABLE_DISCOVERABLE = 2;
	private static int DISCOVERABLE_PERIOD = 300; // 0 = always on
	private final String serviceName = "Unplugged";
    private final UUID uuid = UUID.nameUUIDFromBytes(serviceName.getBytes());
	
	// GUI
	private TextView lastPost;
	private Button submitButton;
	private EditText newPostText; 
	
	// Bluetooth SDK
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mBroadcastReceiver;
	
	// Unplugged Objects
	private UnpluggedBluetoothClient unpluggedBluetoothClient;
	private UnpluggedBluetoothServer unpluggedBluetoothServer;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        boolean isBluetoothSupported = isBluetoothSupported();
        
        if (isBluetoothSupported) {
        	requestBluetoothAndStart();
        } else {
        	setErrorMessage();
        }
    }
    
   @Override
   protected void onStop() {
	   unpluggedBluetoothClient.cancel();
	   unpluggedBluetoothServer.cancel();	   
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private boolean isBluetoothSupported() {
    	boolean isSupported = true;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	isSupported = false;
        }
        return isSupported;
    }
    
    private void setErrorMessage() {
    	lastPost = (TextView) findViewById(R.id.last_post);
    	String errorMsg = "Sorry Bluetooth is not supported on your phone";
    	lastPost.setText(errorMsg);
    }
    
    private void setGoodMessage() {
    	lastPost = (TextView) findViewById(R.id.last_post);
    	String errorMsg = "This is good";
    	lastPost.setText(errorMsg);
    }
    
    private void startApplication() {
        submitButton = (Button) findViewById(R.id.submit_button);
        lastPost = (TextView) findViewById(R.id.last_post);
        newPostText = (EditText) findViewById(R.id.new_post_text);
        
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	lastPost.setText(newPostText.getText());
            }
        });
        
        startMesh();
    }
    
    private void requestBluetoothAndStart() {
    	if (!mBluetoothAdapter.isEnabled()) { //sets mBluetoothAdapter
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	} else {
    		startApplication();
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Log.d(TAG, "onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode);
    	if (requestCode == REQUEST_ENABLE_BT)  { // Response to Enable Bluetooth
    		if (resultCode == RESULT_OK){
        		startApplication();
    		} else {
    			setErrorMessage();
    		}
    	} else if (requestCode == REQUEST_ENABLE_DISCOVERABLE) { //Response to Enable Bluetooth Discoverable
    		if(resultCode == DISCOVERABLE_PERIOD){
        		unpluggedBluetoothServer = new UnpluggedBluetoothServer(mBluetoothAdapter, serviceName, uuid);
            	new Thread(unpluggedBluetoothServer).start();
    		} else if (resultCode == RESULT_CANCELED){
    			setErrorMessage();
    		}
    	}
    }
    
    private void startMesh() {
    	Log.d(TAG, "startMesh");

		if (IS_SERVER){
			startServerDiscovery();
		} else {
			startClientDiscovery();
		}
    }
    
    private void startServerDiscovery() {
    	Log.d(TAG, "startServerDiscovery");
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_PERIOD);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
    }
    
    private void startClientDiscovery() {
		Log.d(TAG, "startClientDiscovery");
		// Create a BroadcastReceiver for ACTION_FOUND
		mBroadcastReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		    	Log.d(TAG, "onReceive BroadcastReceiver");
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		        	Log.d(TAG, "ACTION_FOUND BroadcastReceiver");
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            setGoodMessage();
		        	unpluggedBluetoothClient = new UnpluggedBluetoothClient(bluetoothDevice, mBluetoothAdapter, uuid);
		        	new Thread(unpluggedBluetoothClient).start();
		        }
		    }
		};
		
		Log.d(TAG, "Start BroadhcastReceiver");
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mBroadcastReceiver, filter); // Don't forget to unregister during onDestroy
		
		mBluetoothAdapter.startDiscovery();
    }
}
