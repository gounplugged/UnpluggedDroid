package co.gounplugged.unpluggeddroid;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	// Debug
	private final String TAG = "MainActivity";
	
	// Constants
	private static boolean IS_SERVER = false;
	private static int REQUEST_ENABLE_BT = 1;
	private static int REQUEST_ENABLE_DISCOVERABLE = 2;
	private static int DISCOVERABLE_PERIOD = 300; // 0 = always on
	private final String serviceName = "Unplugged";
    private final UUID uuid = UUID.nameUUIDFromBytes(serviceName.getBytes());
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
	
	// GUI
	private Button submitButton;
	private EditText newPostText; 
	private ArrayAdapter<String> mChatArrayAdapter;
	private ListView mChatView;
	// The Handler that gets information back from the BluetoothChatService
    private Handler mHandler;
	
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
                
        submitButton = (Button) findViewById(R.id.submit_button);
        newPostText = (EditText) findViewById(R.id.new_post_text);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        mChatArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mChatView = (ListView) findViewById(R.id.chats);
        mChatView.setAdapter(mChatArrayAdapter);
        mHandler = new Handler() {
    	    @Override
    	    public void handleMessage(Message msg) {
    		    switch (msg.what) {
    			    case MESSAGE_WRITE:
    			    byte[] writeBuf = (byte[]) msg.obj;
    			    // construct a string from the buffer
    			    String writeMessage = new String(writeBuf);
    			    mChatArrayAdapter.add("Me: " + writeMessage);
    			    break;
    			    
    			    case MESSAGE_READ:
    			    byte[] readBuf = (byte[]) msg.obj;
    			    // construct a string from the valid bytes in the buffer
    			    String readMessage = new String(readBuf, 0, msg.arg1);
    			    mChatArrayAdapter.add("SOMEONE: " + readMessage);
    			    break;
    		    }
    	    }
        };

        if (isBluetoothSupported()) {
        	requestBluetoothAndStart();
        } else {

        }
    }
    
   @Override
   protected void onStop() {
	   super.onStop();
	   if (unpluggedBluetoothServer != null){
			unpluggedBluetoothServer.cancel();
			unpluggedBluetoothServer = null;
		} else if (unpluggedBluetoothClient != null) {
			unpluggedBluetoothClient.cancel();
			unpluggedBluetoothClient = null;
		}
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
        return !(mBluetoothAdapter == null);
    }
    
    private void startApplication() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String str = newPostText.getText().toString();        	
        		if (unpluggedBluetoothServer != null){
        			unpluggedBluetoothServer.chat(str);
        		} else if (unpluggedBluetoothClient != null){
        			unpluggedBluetoothClient.chat(str);
        		}
        		newPostText.setText("");
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
    		}
    	} else if (requestCode == REQUEST_ENABLE_DISCOVERABLE) { //Response to Enable Bluetooth Discoverable
    		if(resultCode == DISCOVERABLE_PERIOD){
        		unpluggedBluetoothServer = new UnpluggedBluetoothServer(mBluetoothAdapter, serviceName, uuid, mHandler);
            	unpluggedBluetoothServer.start();
    		} else if (resultCode == RESULT_CANCELED){
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
//		            	setGoodMessage();
//		            bluetoothDevice.fetchUuidsWithSdp();
		            connectClient(bluetoothDevice);
		        }
		    }
		};
		
		Log.d(TAG, "Start BroadhcastReceiver");
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mBroadcastReceiver, filter); // Don't forget to unregister during onDestroy
		
		mBluetoothAdapter.startDiscovery();
    }
    
    private void connectClient(BluetoothDevice bluetoothDevice) {
    	if(bluetoothDevice.getName().equals("motop")){
	    	unpluggedBluetoothClient = new UnpluggedBluetoothClient(bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
	    	unpluggedBluetoothClient.start();
	        Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
    	}
    }

}
