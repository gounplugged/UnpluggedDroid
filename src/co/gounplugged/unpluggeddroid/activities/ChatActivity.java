package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.pkmmte.view.CircularImageView;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.UnpluggedMesh;
import co.gounplugged.unpluggeddroid.UnpluggedMessageHandler;
import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.widgets.ConversationContainer;
import de.greenrobot.event.EventBus;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


public class ChatActivity extends Activity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants  
	private static int REQUEST_ENABLE_BT = 1;
	private static int REQUEST_ENABLE_DISCOVERABLE = 2;
	private static int DISCOVERABLE_PERIOD = 0; // 0 = always on
	
	public static final String SERVICE_NAME = "Unplugged";
	public static final UUID Uuid = UUID.nameUUIDFromBytes(SERVICE_NAME.getBytes(Charset.forName("UTF-8")));
	
	// GUI
	private boolean guiLoaded = false;
	private ImageButton submitButton;
	private EditText newPostText;
	private MessageAdapter mChatArrayAdapter;
	private ListView mChatView;
	private MenuItem mItemConnectionStatus;
    private ImageView mDropZoneImage;

	
	// Connectivity
	private UnpluggedMesh unpluggedMesh;
	private boolean syncing;
	private BroadcastReceiver mDiscoveryBroadcastReceiver;
	
    //////////////////////////////    Activity Lifecycles    ////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncing = false;
        
        setContentView(R.layout.activity_chat);
        BluetoothAdapter bluetoothAdapter;
        
        if (!isBleSupported()) {
			 Toast.makeText(this, "BLE is not available", Toast.LENGTH_LONG).show();
//			 finish();
//			 return;
        } 
        
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    	bluetoothAdapter = bluetoothManager.getAdapter();
        unpluggedMesh = new UnpluggedMesh(SERVICE_NAME, Uuid, this, bluetoothAdapter);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
        EventBus.getDefault().register(this);

        if (!unpluggedMesh.areConnectionsEnabled()) {
  
    	} else {
    		loadGui();
    	}
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    
    @Override
    protected synchronized void onResume() {
    	super.onResume();
    	if(!syncing) {
    		unpluggedMesh.resumeConnections();
    		unpluggedMesh.ping();
    		syncing = true;
    	}

        mDiscoveryBroadcastReceiver = newDiscoveryBroadcastReceiver();
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mDiscoveryBroadcastReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mDiscoveryBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mDiscoveryBroadcastReceiver);
    }

    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        if (unpluggedMesh != null)
    	    unpluggedMesh.stopAll();
    	guiLoaded = false;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                final MenuItem menuItem = item;
//                menuItem.setActionView(R.layout.actionbar_progress);
//                menuItem.expandActionView();
//
//                //AsyncTask used to 'fake' progress by showing spinner for x seconds
//                new AsyncTask<Void, Void, String>() {
//
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String result) {
//                        menuItem.collapseActionView();
//                        menuItem.setActionView(null);
//                    }
//                }.execute();
//                break;
//            case R.id.action_settings:
//                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
//                        .show();
//                break;
//            default:
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//
//        mItemConnectionStatus = menu.findItem(R.id.action_connection_status);
//        // Hack: load gui again to make sure mItemConnectionStatus is passed on to UnpluggedMessageHandler
//        // so it can receive connection-updates
//        guiLoaded = false;
//        loadGui();
//
//        return true;
//    }
                  
	/////////////////////////////////////////         GUI    ////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Conversation selectedConversation = null;
    
    public void loadGui() {
    	if(!guiLoaded) {
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

            if (unpluggedMesh != null)
	            unpluggedMesh.setHandler(new UnpluggedMessageHandler(mChatArrayAdapter, mItemConnectionStatus));

            //Chat-container //todo extract
            mChatView.setBackgroundColor(Color.GRAY);
            mChatView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v,  DragEvent event){
                    switch(event.getAction())
                    {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            int x_cord = (int) event.getX();
                            int y_cord = (int) event.getY();
                            Log.i(TAG, "ACTION_DRAG_ENTERED x:" + x_cord + " y:" + y_cord);
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            x_cord = (int) event.getX();
                            y_cord = (int) event.getY();
                            Log.i(TAG, "ACTION_DRAG_EXITED x:" + x_cord + " y:" + y_cord);
                            break;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            x_cord = (int) event.getX();
                            y_cord = (int) event.getY();
                            Log.i(TAG, "ACTION_DRAG_LOCATION x:" + x_cord + " y:" + y_cord);
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            //Drag ended in listview: change conversation  //TODO check if it really ended in the listview!
//                            mChatView.setBackgroundColor(Color.GRAY);
                            mDropZoneImage.setVisibility(View.GONE);

                            Collection<Message> messages = selectedConversation.getMessages();
                            mChatArrayAdapter.setMessages( new ArrayList<>(messages));


                            int[] dropZoneLocation = new int[2];
                            ((View)mDropZoneImage).getLocationOnScreen(dropZoneLocation);


                            Log.i(TAG, "ACTION_DRAG_ENDED");
                            break;
                        case DragEvent.ACTION_DROP:
                            Log.i(TAG, "ACTION_DROP");
                            break;
                        default: break;
                    }
                    return true;
                }
            });

            mDropZoneImage = (ImageView) findViewById(R.id.iv_drop_zone);


	        guiLoaded = true;


            //playground
            DatabaseAccess<Message> messageDatabaseAccess = new DatabaseAccess<>(getApplicationContext(), Message.class);
            List<Message> messages = messageDatabaseAccess.getAll();
            mChatArrayAdapter.setMessages(messages);

            //add conversation
            CircularImageView imageView = new CircularImageView(getApplicationContext());




    	}
    }
	/////////////////////////////////////////   Callbacks    ////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Log.d(TAG, "onActivityResult, requestCode: " + requestCode + ", resultCode: " + resultCode);
    	if (requestCode == REQUEST_ENABLE_BT)  { // Response to Enable Bluetooth
    		if (resultCode == RESULT_OK){
        		loadGui();
        		unpluggedMesh.resumeConnections();
    		} else {
    			 Log.d(TAG, "BT not enabled");
    			 finish();
    		}
    	} else if (requestCode == REQUEST_ENABLE_DISCOVERABLE) { //Response to Enable Bluetooth Discoverable
    		if(resultCode == DISCOVERABLE_PERIOD || resultCode == 1) {
    		} else if (resultCode == RESULT_CANCELED){
    			// no problem
    		}
    	}
    }
    
    public BroadcastReceiver newDiscoveryBroadcastReceiver() {
    	return new BroadcastReceiver() {
    		 
    		@Override
    		public synchronized void onReceive(Context context, Intent intent) {
    			onReceiveBluetooth(intent);
	    	}
    		
    		private void onReceiveBluetooth(Intent intent) {
    			String action = intent.getAction();
	    		 // When discovery finds a device
	    		 if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		    		 // Get the BluetoothDevice object from the Intent
		    		 BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    		 // If it's already paired, skip it, because it's been listed already
		    		 if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
				    	if(bluetoothDevice.getName() != null && bluetoothDevice.getName().equals("motop")){
				    		// Connect client
	    			    }
		    		 } else {
		    			 // Connect already bonded
		    		 }
		    		 // When discovery is finished, change the Activity title
	    		 } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		    			 // could restart discovery
	    		 }
    		}
	    };
    }


    public void onEvent(ConversationEvent event){
        switch(event.getType()) {

            case SELECTED:
                Log.d(TAG, "Eventbus selected ");
                //Blur listview
//                mChatView.setBackgroundColor(Color.GREEN);
                selectedConversation = event.getConversation();
                mDropZoneImage.setVisibility(View.VISIBLE);
                break;
            case SWITCHED:
                mDropZoneImage.setVisibility(View.GONE);
                break;
        }
    }
    
	/////////////////////////////////////////   Connectivity ////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static ParcelUuid getParcelUuid() {
		return new ParcelUuid(ChatActivity.Uuid);
	}
    
	public void enableBle() {
		enableBluetooth();
	}
	
	public void enableBluetooth() {
  	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}
	
	private boolean isBleSupported() {
		return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private int sendCount = 0;
    //playground
    private void sendMessage() {
//        int messageType = UnpluggedMessageHandler.MESSAGE_WRITE;
//        if (i++%2 == 1)
//            messageType = UnpluggedMessageHandler.MESSAGE_READ;
//
//    	String str = newPostText.getText().toString();
//    	unpluggedMesh.addHydraPost(messageType, str);
//		newPostText.setText("");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Conversation conversation = new Conversation();

                //save conversation
                DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getApplicationContext(), Conversation.class);
                conversationAccess.create(conversation);

                String text = "Lorem ipsum ... " + sendCount++;

                //Collection<Message> messages = new ArrayList<Message>();
                for (int i=0; i<15; i++) {

                    int messageType = UnpluggedMessageHandler.MESSAGE_WRITE;
                    if (i%2 == 1)
                        messageType = UnpluggedMessageHandler.MESSAGE_READ;

                    Message message = new Message(text, messageType, System.currentTimeMillis());
                    message.setConversation(conversation);

                    //save message
                    DatabaseAccess<Message> messageDatabaseAccess = new DatabaseAccess<Message>(getApplicationContext(), Message.class);
                    messageDatabaseAccess.create(message);
                }
                return null;
            }

        }.execute();










    } 
    
    public void startBroadcast() {
    	Log.d(TAG, "startBroadcast");
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_PERIOD);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
    }
    
    
}