package co.gounplugged.unpluggeddroid;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChatActivity extends ActionBarActivity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants  
	private static int REQUEST_ENABLE_BT = 1;
	private static int REQUEST_ENABLE_DISCOVERABLE = 2;
	private static int DISCOVERABLE_PERIOD = 300; // 0 = always on
	
	// GUI
	private boolean guiLoaded = false;
	private Button submitButton;
	private Button refreshButton;
	private EditText newPostText;
	private TextView connectionStatus;
	private ArrayAdapter<String> mChatArrayAdapter;
	private ListView mChatView;
	
	// Bluetooth SDK
	private BroadcastReceiver mBroadcastReceiver;
	
	// Unplugged
	private UnpluggedMesh unpluggedMesh;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_chat);       
        unpluggedMesh = new UnpluggedMesh(true, "Unplugged", UUID.nameUUIDFromBytes("Unplugged".getBytes()));
        
        if (!unpluggedMesh.isBluetoothSupported()) {
			 Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			 finish();
			 return;
        }
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if (!unpluggedMesh.isBluetoothEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	} else {
    		loadGui();
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(unpluggedMesh.getState() == UnpluggedMesh.STATE_NONE) unpluggedMesh.start();
    }
    
   @Override
   protected void onStop() {
	   super.onStop();
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
               
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	unpluggedMesh.stop();
    }
    
    private void sendMessage() {
    	String str = newPostText.getText().toString();   
    	unpluggedMesh.sendMessage(str);
		newPostText.setText("");
    }
    
    /*
     * 
     */
    public void loadGui() {
    	if(!guiLoaded) {
	    	Log.d(TAG, "loadGui");
	    	
	    	// Connection Status
	    	connectionStatus = (TextView) findViewById(R.id.connection_status);
	    	
	    	// Submit Button
	    	submitButton = (Button) findViewById(R.id.submit_button);
	        submitButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	sendMessage();
	            }
	        });
	        
	        // Refresh Button
	        refreshButton = (Button) findViewById(R.id.refresh_button);
	        refreshButton.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		unpluggedMesh.start();
	        	}
	        });
	    	
	        // Enter pressed submission
	    	newPostText = (EditText) findViewById(R.id.new_post_text);
	        newPostText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				    // If the action is a key-up event on the return key, send the message
				    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
					    sendMessage();
				    }
				    return true;
			    }
	    	});
	        
	        // Chat log
	        mChatArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
	        mChatView = (ListView) findViewById(R.id.chats);
	        mChatView.setAdapter(mChatArrayAdapter);
	        unpluggedMesh.setHandler(new UnpluggedMessageHandler(mChatArrayAdapter, connectionStatus));
	        
	        guiLoaded = true;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

/*
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
//	            	setGoodMessage();
//	            bluetoothDevice.fetchUuidsWithSdp();
	            if(bluetoothDevice == null) {
	            	Log.d(TAG, "null device");
	            } else {
	            	connectClient(bluetoothDevice);
	            }
	            
	        }
	    }
	};
	
	Log.d(TAG, "Start BroadhcastReceiver");
	// Register the BroadcastReceiver
	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	registerReceiver(mBroadcastReceiver, filter); // Don't forget to unregister during onDestroy
	
	mBluetoothAdapter.startDiscovery();
}
    private void startServerDiscovery() {
    	Log.d(TAG, "startServerDiscovery");
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_PERIOD);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
    }
    

    private void connectClient(BluetoothDevice bluetoothDevice) {
    	if(bluetoothDevice.getName().equals("motop")){
    		if(unpluggedBluetoothClient == null) unpluggedBluetoothClient = new UnpluggedBluetoothClient(bluetoothDevice, mBluetoothAdapter, uuid, mHandler);
    		if (unpluggedBluetoothClient.state == UnpluggedNode.DISCONNECTED) unpluggedBluetoothClient.scan();
	        Log.d(TAG, "useless device " + bluetoothDevice.getName()); 
    	}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Log.d(TAG, "onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode);
    	if (requestCode == REQUEST_ENABLE_BT)  { // Response to Enable Bluetooth
    		if (resultCode == RESULT_OK){
        		loadGui();
    		} else {
    			 Log.d(TAG, "BT not enabled");
//    			 Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
    			 finish();
    		}
    	} else if (requestCode == REQUEST_ENABLE_DISCOVERABLE) { //Response to Enable Bluetooth Discoverable
    		if(resultCode == DISCOVERABLE_PERIOD) {
    			unpluggedMesh.startBroadcast();
    		} else if (resultCode == RESULT_CANCELED){
    		}
    	}
    }
*/