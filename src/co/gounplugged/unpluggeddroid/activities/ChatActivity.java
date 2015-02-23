package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.pkmmte.view.CircularImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.UnpluggedMessageHandler;
import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import de.greenrobot.event.EventBus;


public class ChatActivity extends Activity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    String SENDER_ID = "unpluggeddroid1";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regId;


    //////////////////////////////    Activity Lifecycles    ////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_chat);
        context = getApplicationContext();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

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
        checkPlayServices();
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
	        mChatView.setAdapter(mChatArrayAdapter);

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
//    private void sendMessage() {
//
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                Conversation conversation = new Conversation();
//
//                //save conversation
//                DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getApplicationContext(), Conversation.class);
//                conversationAccess.create(conversation);
//
//                String text = "Lorem ipsum ... " + sendCount++;
//
//                //Collection<Message> messages = new ArrayList<Message>();
//                for (int i=0; i<15; i++) {
//
//                    int messageType = UnpluggedMessageHandler.MESSAGE_WRITE;
//                    if (i%2 == 1)
//                        messageType = UnpluggedMessageHandler.MESSAGE_READ;
//
//                    Message message = new Message(text, messageType, System.currentTimeMillis());
//                    message.setConversation(conversation);
//
//                    //save message
//                    DatabaseAccess<Message> messageDatabaseAccess = new DatabaseAccess<Message>(getApplicationContext(), Message.class);
//                    messageDatabaseAccess.create(message);
//                }
//                return null;
//            }
//
//        }.execute();
//    }

    private void sendMessage() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("my_message", "Hello World");
                    data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                    mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);

    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(ChatActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }


    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}