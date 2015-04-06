package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.broadcastReceivers.SmsBroadcastReceiver;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.Throw;
import de.greenrobot.event.EventBus;


public class ChatActivity extends Activity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants
    public static final String EXTRA_MESSAGE = "message";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	// GUI
	private boolean guiLoaded = false;
	private ImageButton submitButton;
	private EditText newPostText;
	private MessageAdapter mChatArrayAdapter;
	private ListView mChatView;
    private ImageView mDropZoneImage;
    private MessageHandler mMessageHandler;

    private Profile profile;
    private Krewe knownMasks;
    private APICaller apiCaller;
    SmsBroadcastReceiver smsBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        profile = new Profile(getApplicationContext());
        apiCaller = new APICaller(getApplicationContext(), this);
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setActivity(this);
        IntentFilter fltr_smsreceived = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsBroadcastReceiver, fltr_smsreceived);

        seedKnownMasks();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
        EventBus.getDefault().register(this);
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

            mMessageHandler = new MessageHandler(mChatArrayAdapter, getApplicationContext());

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

    public void onEvent(ConversationEvent event){
        Log.d(TAG, "Eventbus onEvent event: " + event.toString());
        switch(event.getType()) {
            case SELECTED:
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

    private void sendMessage() {
        try {
            Conversation conversation = new Conversation();

            DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getApplicationContext(), Conversation.class);
            conversationAccess.create(conversation);
            conversation.setMessageHandler(mMessageHandler);
            selectedConversation = conversation;
            conversation.sendMessage(newPostText.getText().toString(), knownMasks);
            newPostText.setText("");

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failure to send", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void seedKnownMasks() {
        if(knownMasks == null){
            knownMasks = new Krewe();
        }
        DatabaseAccess<Mask> maskAccess = new DatabaseAccess<>(getApplicationContext(), Mask.class);
        // TODO: Prefill from db

        if(knownMasks.isEmpty()) {
            apiCaller.getMasks(Contact.DEFAULT_COUNTRY_CODE);
        }
    }

    public void processThrow(String s) {

        Throw receivedThrow = new Throw(s);
        String nextMessage = receivedThrow.getEncryptedContent();
        Log.d(TAG, "Next message: " + nextMessage);

        if(!receivedThrow.hasArrived()) {
            mMessageHandler.sendSms(receivedThrow.getThrowTo().getPhoneNumber(), nextMessage);
        } else {
            Conversation conversation = new Conversation();
            conversation.setMessageHandler(mMessageHandler);

            DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getApplicationContext(), Conversation.class);
            conversationAccess.create(conversation);

            selectedConversation = conversation;
            conversation.receiveThrow(receivedThrow);
        }


    }

    public void setKnownMasks(Krewe masks) {
        this.knownMasks = masks;
    }

}