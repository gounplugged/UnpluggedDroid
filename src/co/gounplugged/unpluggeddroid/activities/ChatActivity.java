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

    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	loadGui();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    
    @Override
    protected synchronized void onResume() {
    	super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	guiLoaded = false;
    }


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
	        mChatView.setAdapter(mChatArrayAdapter);;

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

    private int sendCount = 0;
    //playground
    private void sendMessage() {

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
    
}