package co.gounplugged.unpluggeddroid.activities;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import co.gounplugged.unpluggeddroid.fragments.MessageInputFragment;
import co.gounplugged.unpluggeddroid.fragments.SearchContactFragment;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.widgets.infiniteviewpager.InfinitePagerAdapter;
import co.gounplugged.unpluggeddroid.widgets.infiniteviewpager.InfiniteViewPager;
import de.greenrobot.event.EventBus;


public class ChatActivity extends FragmentActivity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants
    public static final String EXTRA_MESSAGE = "message";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	// GUI
	private MessageAdapter mChatArrayAdapter;
	private ListView mChatView;
    private ImageView mDropZoneImage;
    private InfiniteViewPager mViewPager;

    private MessageHandler mMessageHandler;

    private Profile profile;
    SmsBroadcastReceiver smsBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    	loadGui();

        profile = new Profile(getApplicationContext());
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setActivity(this);
        IntentFilter fltr_smsreceived = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsBroadcastReceiver, fltr_smsreceived);
        mMessageHandler = new MessageHandler(mChatArrayAdapter, getApplicationContext());
    }

    @Override
    protected void onStart() {
    	super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected synchronized void onResume() {
    	super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    private Conversation selectedConversation = null;

    public void loadGui() {
        // Chat log
        mChatArrayAdapter = new MessageAdapter(this);
        mChatView = (ListView) findViewById(R.id.chats);
        mChatView.setAdapter(mChatArrayAdapter);

        // Input/Search viewpager
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(Fragment.instantiate(getApplicationContext(), MessageInputFragment.class.getName(), getIntent().getExtras()));
        fragments.add(Fragment.instantiate(getApplicationContext(), SearchContactFragment.class.getName(), getIntent().getExtras()));
        fragments.add(Fragment.instantiate(getApplicationContext(), MessageInputFragment.class.getName(), getIntent().getExtras()));
        fragments.add(Fragment.instantiate(getApplicationContext(), SearchContactFragment.class.getName(), getIntent().getExtras()));

        mViewPager = (InfiniteViewPager) findViewById(R.id.viewpager);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragments);
        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);

        mViewPager.setAdapter(wrappedAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Todo reset or store input values
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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
                        //TODO check if it ended in the listview!
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

        //playground
        DatabaseAccess<Message> messageDatabaseAccess = new DatabaseAccess<>(getApplicationContext(), Message.class);
        List<Message> messages = messageDatabaseAccess.getAll();
        mChatArrayAdapter.setMessages(messages);

        //add conversation
        CircularImageView imageView = new CircularImageView(getApplicationContext());

    }

    public void onEvent(ConversationEvent event){
        Log.d(TAG, "Eventbus onEvent event: " + event.toString());
        switch(event.getType()) {
            case SELECTED:
                selectedConversation = event.getConversation();
                mDropZoneImage.setVisibility(View.VISIBLE);
                break;
            case SWITCHED:
                mDropZoneImage.setVisibility(View.GONE);
                break;
        }
    }

    //todo refactor
    public MessageAdapter getChatArrayAdapter() {
        return mChatArrayAdapter;
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

    private class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        private final List<Fragment> mViewPagerFragments;

        public FragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mViewPagerFragments = fragments;
        }

        @Override
        public Fragment getItem(int index) {
            return mViewPagerFragments.get(index);
        }

        @Override
        public int getCount() {
            return mViewPagerFragments.size();
        }
    }

}