package co.gounplugged.unpluggeddroid.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.broadcastReceivers.SmsBroadcastReceiver;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.fragments.MessageInputFragment;
import co.gounplugged.unpluggeddroid.fragments.SearchContactFragment;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.widgets.ConversationContainer;
import co.gounplugged.unpluggeddroid.widgets.infiniteviewpager.InfinitePagerAdapter;
import co.gounplugged.unpluggeddroid.widgets.infiniteviewpager.InfiniteViewPager;


public class ChatActivity extends FragmentActivity {
	// Debug
	private final String TAG = "ChatActivity";
	
	// Constants
    public static final String EXTRA_MESSAGE = "message";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	// GUI
	private MessageAdapter mChatArrayAdapter;
	private ListView mChatListView;
    private InfiniteViewPager mViewPager;
    private ConversationContainer mConversationContainer;
    private ImageView mImageViewDropZoneChats, mImageViewDropZoneDelete;

    private MessageHandler mMessageHandler;

    private Profile profile;
    SmsBroadcastReceiver smsBroadcastReceiver;
    private Conversation mSelectedConversation;


    private ConversationContainer.ConversationListener conversationListener = new ConversationContainer.ConversationListener() {

        @Override
        public void onConversationSelected(Conversation conversation) {
            Log.i(TAG, "onConversationSelected: " + conversation.toString());
            mSelectedConversation = conversation;
            showDropZones();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    	loadGui();

        profile = new Profile(getApplicationContext());
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setActivity(this);
        mMessageHandler = new MessageHandler(mChatArrayAdapter, getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter fltr_smsreceived = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsBroadcastReceiver, fltr_smsreceived);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    protected synchronized void onResume() {
    	super.onResume();
        mConversationContainer.setConversationListener(conversationListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mConversationContainer.removeConversationListener(conversationListener);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    

    private void loadGui() {

        // Input/Search infinite-viewpager
        // It is only possible to achieve wrapping when you have at least 4 pages.
        // This is because of the way the ViewPager creates, destroys, and displays the pages.
        // No fix for the general case has been found.
        mViewPager = (InfiniteViewPager) findViewById(R.id.viewpager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(Fragment.instantiate(getApplicationContext(), MessageInputFragment.class.getName(), getIntent().getExtras()));
        fragments.add(Fragment.instantiate(getApplicationContext(), SearchContactFragment.class.getName(), getIntent().getExtras()));
        fragments.add(Fragment.instantiate(getApplicationContext(), MessageInputFragment.class.getName(), getIntent().getExtras()));
        fragments.add(Fragment.instantiate(getApplicationContext(), SearchContactFragment.class.getName(), getIntent().getExtras()));

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragments);
        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);

        mViewPager.setAdapter(wrappedAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, (position % 2 == 0 ? "input" : "search") + "-fragment in viewpager selected");
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        //drop zone views & animations
        mImageViewDropZoneDelete = (ImageView) findViewById(R.id.iv_drop_zone_delete);
        mImageViewDropZoneChats = (ImageView) findViewById(R.id.iv_drop_zone_chats);

        //Check for drop-events on drop-zones
        mImageViewDropZoneDelete.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    //should always be called
                    case DragEvent.ACTION_DRAG_ENDED:
                        hideDropZones();
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.i(TAG, "Conversation dropped on mImageViewDropZoneDelete.");
                        Collection<Message> messages = new ArrayList<>();
                        mChatArrayAdapter.setMessages(new ArrayList<>(messages));
                        break;
                }
                return true;
            }
        });
        mImageViewDropZoneChats.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        Log.i(TAG, "Conversation dropped on mChatListView.");
                        Collection<Message> messages = new ArrayList<>(mSelectedConversation.getMessages());
                        mChatArrayAdapter.setMessages(new ArrayList<>(messages));
                        break;
                }
                return true;
            }
        });


        // Chat log //todo extract
        mChatArrayAdapter = new MessageAdapter(this);
        mChatListView = (ListView) findViewById(R.id.lv_chats);
        mChatListView.setAdapter(mChatArrayAdapter);

        //Conversations
        mConversationContainer = (ConversationContainer) findViewById(R.id.conversation_container);

        //playground
        DatabaseAccess<Message> messageDatabaseAccess = new DatabaseAccess<>(getApplicationContext(), Message.class);
        List<Message> messages = messageDatabaseAccess.getAll();
        mChatArrayAdapter.setMessages(messages);

    }

    private void showDropZones() {
        mImageViewDropZoneChats.setVisibility(View.VISIBLE);
        mImageViewDropZoneDelete.setVisibility(View.VISIBLE);
    }

    private void hideDropZones() {
        mImageViewDropZoneDelete.setVisibility(View.GONE);
        mImageViewDropZoneChats.setVisibility(View.GONE);

    }

    //todo refactor
    public MessageAdapter getChatArrayAdapter() {
        return mChatArrayAdapter;
    }


    public void processMessage(String receivedMessage) {
        Throw receivedThrow = null;
        try {
            receivedThrow = new Throw(receivedMessage);
            String nextMessage = receivedThrow.getEncryptedContent();
            Log.d(TAG, "Next message: " + nextMessage);

            if(!receivedThrow.hasArrived()) {
                mMessageHandler.sendSms(receivedThrow.getThrowTo().getFullNumber(), nextMessage);
            } else {

                Conversation conversation = new Conversation();
//            conversation.setContext(getApplicationContext());
                conversation.setMessageHandler(mMessageHandler);

                DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getApplicationContext(), Conversation.class);
                conversationAccess.create(conversation);

                mSelectedConversation = conversation;
                conversation.receiveThrow(receivedThrow);
            }
        } catch (InvalidPhoneNumberException e) {
            //TODO recover from problem to ensure message delivery
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