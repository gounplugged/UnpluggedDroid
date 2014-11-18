package co.gounplugged.unpluggeddroid.activity;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;
import co.gounplugged.unpluggeddroid.UnpluggedMessageHandler;
import co.gounplugged.unpluggeddroid.adapter.MessageAdapter;


public class ChatActivity extends ActionBarActivity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants  
	private static int REQUEST_ENABLE_BT = 1;
	private static int REQUEST_ENABLE_DISCOVERABLE = 2;
	private static int DISCOVERABLE_PERIOD = 0; // 0 = always on
	
	// GUI
	private boolean guiLoaded = false;
	private ImageButton submitButton;
	private EditText newPostText;
//	private ArrayAdapter<String> mChatArrayAdapter;
	private MessageAdapter mChatArrayAdapter;
	private ListView mChatView;
	
	private boolean syncing;
	
	// Bluetooth SDK
	private BroadcastReceiver mDiscoveryBroadcastReceiver;
	
	// Unplugged
	private UnpluggedMesh unpluggedMesh;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncing = false;
        
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_chat);
        unpluggedMesh = new UnpluggedMesh("Unplugged", UUID.nameUUIDFromBytes("Unplugged".getBytes()), this);
        
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
//    		unpluggedMesh.start();
    	}
    }
    
    @Override
    protected synchronized void onResume() {
    	super.onResume();
    	if(!syncing) {
    		unpluggedMesh.start();
    		syncing = true;
    	}
//    	unpluggedMesh.restart();
//    	unpluggedMesh.start();

        // Discovered broadcast receiver
//	        if(!unpluggedMesh.isServer()) {
        mDiscoveryBroadcastReceiver = newDiscoveryBroadcastReceiver();
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mDiscoveryBroadcastReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mDiscoveryBroadcastReceiver, filter);
//	        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mDiscoveryBroadcastReceiver);
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
        switch (item.getItemId()) {
            case R.id.action_refresh:
                final MenuItem menuItem = item;
                menuItem.setActionView(R.layout.actionbar_progress);
                menuItem.expandActionView();

                unpluggedMesh.restartConnection(this);

                //AsyncTask used to 'fake' progress by showing spinner for x seconds
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        menuItem.collapseActionView();
                        menuItem.setActionView(null);
                    }
                }.execute();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private MenuItem mItemConnectionStatus;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mItemConnectionStatus = menu.findItem(R.id.action_connection_status);
        // Hack: load gui again to make sure mItemConnectionStatus is passed on to UnpluggedMessageHandler
        // so it can receive connection-updates
        guiLoaded = false;
        loadGui();

        return true;
    }
               
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	unpluggedMesh.stopAll();
    	// Unregister broadcast listeners
//    	if(mDiscoveryBroadcastReceiver != null) 
//    	this.unregisterReceiver(mDiscoveryBroadcastReceiver);
    }
    
    private void sendMessage() {
    	String str = newPostText.getText().toString(); 
    	unpluggedMesh.addHydraPost(UnpluggedMessageHandler.MESSAGE_WRITE, str);
    	//    	unpluggedMesh.sendMessage(str);
		newPostText.setText("");
    } 
    
    public void startBroadcast() {
    	Log.d(TAG, "startBroadcast");
    	unpluggedMesh.stopDiscovery();
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_PERIOD);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
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
    		if(resultCode == DISCOVERABLE_PERIOD || resultCode == 1) {

    		} else if (resultCode == RESULT_CANCELED){

    		}
    	}
    }
    
    public BroadcastReceiver newDiscoveryBroadcastReceiver() {
    	return new BroadcastReceiver() {
    		 
    		@Override
    		 public synchronized void onReceive(Context context, Intent intent) {
	    		 String action = intent.getAction();
	    		 // When discovery finds a device
	    		 if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		    		 // Get the BluetoothDevice object from the Intent
		    		 BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    		 Log.d(TAG, "FOUND BROADCAST " + bluetoothDevice.getName());
		    		 // If it's already paired, skip it, because it's been listed already
		    		 if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
		    			 Log.d(TAG, "bluetooth device not previously bonded");
				    	if(bluetoothDevice.getName() != null && bluetoothDevice.getName().equals("motop")){
				    		Log.d(TAG, "Matches device name (motop). Establishing a connection.");
				    		unpluggedMesh.connectClient(bluetoothDevice);
	    			    }
		    		 } else {
		    			 Log.d(TAG, "bluetooth device already bonded");
		    			 BluetoothDevice bondedBluetoothDevice = unpluggedMesh.isBonded(bluetoothDevice);
				    	if( bondedBluetoothDevice != null) bluetoothDevice = unpluggedMesh.actualFromBonded(bondedBluetoothDevice);
				    	unpluggedMesh.connectClient(bluetoothDevice);
		    		 }
		    		 // When discovery is finished, change the Activity title
		    		 } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		    			 // could restart discovery
		    		 }
	    		 }
	    	};
    }
    
    
    /*
     * 
     */
    public void loadGui() {
    	if(!guiLoaded) {
	    	Log.d(TAG, "loadGui");

	    	// Submit Button
	    	submitButton = (ImageButton) findViewById(R.id.submit_button);
	        submitButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	sendMessage();
	            }
	        });

	        // Enter pressed submission
	    	newPostText = (EditText) findViewById(R.id.new_post_text);
	        newPostText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				    // If the action is a key-up event on the return key, send the list_item_message_outgoing
				    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
					    sendMessage();
				    }
				    return true;
			    }
	    	});
	        
	        // Chat log
	        mChatArrayAdapter = new MessageAdapter(this);
	        mChatView = (ListView) findViewById(R.id.chats);
	        mChatView.setAdapter(mChatArrayAdapter);
	        unpluggedMesh.setHandler(new UnpluggedMessageHandler(mChatArrayAdapter, mItemConnectionStatus));
	        

	        
	        guiLoaded = true;
    	}
    }



}