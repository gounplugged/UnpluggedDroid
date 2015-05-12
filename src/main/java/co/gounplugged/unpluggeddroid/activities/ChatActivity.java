package co.gounplugged.unpluggeddroid.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
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
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.broadcastReceivers.SmsBroadcastReceiver;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidThrowException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.fragments.MessageInputFragment;
import co.gounplugged.unpluggeddroid.fragments.SearchContactFragment;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.widgets.ConversationContainer;
import co.gounplugged.unpluggeddroid.widgets.infiniteviewpager.InfinitePagerAdapter;
import co.gounplugged.unpluggeddroid.widgets.infiniteviewpager.InfiniteViewPager;


public class ChatActivity extends ActionBarActivity {
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

    SmsBroadcastReceiver smsBroadcastReceiver;

    private Conversation mSelectedConversation;
    private Conversation mClickedConversation;  //TODO refactor global var

    private ConversationContainer.ConversationListener conversationListener = new ConversationContainer.ConversationListener() {

        //TODO: clear distinction / proper naming for selecting a conversation and switching to conversation
        @Override
        public void onConversationClicked(Conversation conversation) {
            Log.i(TAG, "onConversationClicked: " + conversation.toString());
            mClickedConversation = conversation;
            showDropZones();
        }
    };

    /*
        Return the last selected conversation. Null if no last conversation.
     */
    public Conversation getLastSelectedConversation() {
        if(mSelectedConversation == null) {
            long cid = ((BaseApplication) getApplicationContext()).getProfile().getLastConversationId();
            if(cid != Profile.LAST_SELECTED_CONVERSATION_UNSET_ID) {
                try {
                    mSelectedConversation = Conversation.findById(getApplicationContext(), cid, mMessageHandler);
                } catch (NotFoundInDatabaseException e) {
                    e.printStackTrace();
                }
            }
        }
        return mSelectedConversation;
    }

    private void setLastConversation() {
        ((BaseApplication)getApplicationContext()).getProfile().setLastConversationId(mSelectedConversation.id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatArrayAdapter = new MessageAdapter(this);
        mMessageHandler = new MessageHandler(mChatArrayAdapter, getApplicationContext());
        getLastSelectedConversation();
    	loadGui();

        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setActivity(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter fltr_smsreceived = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsBroadcastReceiver, fltr_smsreceived);
        ((BaseApplication) getApplicationContext()).seedKnownMasks();
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
        mViewPager = (InfiniteViewPager) findViewById(R.id.viewpager);

        // It is only possible to achieve wrapping when you have at least 4 pages.
        // This is because of the way the ViewPager creates, destroys, and displays the pages.
        // No fix for the general case has been found.
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
                        replaceSelectedConversation(mClickedConversation);
                        break;
                }
                return true;
            }
        });


        // Chat log //todo extract
        mChatListView = (ListView) findViewById(R.id.lv_chats);
        mChatListView.setAdapter(mChatArrayAdapter);

        //Conversations
        mConversationContainer = (ConversationContainer) findViewById(R.id.conversation_container);
        mConversationContainer.setConversationsAllBut(mSelectedConversation);

        List<Message> messages;
        if(mSelectedConversation == null) {
            messages = new ArrayList<Message>();
        } else {
            //playground
            DatabaseAccess<Message> messageDatabaseAccess = new DatabaseAccess<>(getApplicationContext(), Message.class);
            messages = new ArrayList(mSelectedConversation.getMessages());
        }
        mChatArrayAdapter.setMessages(messages);

    }

    private void replaceSelectedConversation(Conversation newConversation) {
        if(!hasConversationChanged(newConversation)) return;
        if(mSelectedConversation != null) mConversationContainer.addConversation(mSelectedConversation);

        mConversationContainer.removeConversation(newConversation);
        mSelectedConversation = newConversation;
        setLastConversation();

        Collection<Message> messages = mSelectedConversation.getMessages();
        ArrayList messageList = new ArrayList();
        if(messages != null) messageList = new ArrayList<>(messages);
        mChatArrayAdapter.setMessages(messageList);

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
        Log.d(TAG, "Received message: " + receivedMessage);
        try {
            Throw receivedThrow = new Throw(receivedMessage);
            String nextMessage = receivedThrow.getEncryptedContent();
            Log.d(TAG, "Next message: " + nextMessage);

            if(!receivedThrow.hasArrived()) {
                Log.d(TAG, "Throw again");
                mMessageHandler.sendSms(receivedThrow.getThrowTo().getFullNumber(), nextMessage);
            } else {
                try {
                    Contact participant = receivedThrow.getThrownFrom(getApplicationContext());
                    Log.d(TAG, "Chat for contact " + participant.id);
                    Conversation conversation = Conversation.findOrNew(participant, getApplicationContext(), mMessageHandler);
                    Log.d(TAG, "Conversation for " + conversation.id);
                    conversation.receiveThrow(receivedThrow);
                } catch (PrematureReadException e) {
                    Log.e(TAG, "Premature");
                } catch (NotFoundInDatabaseException e) {
                    Log.e(TAG, "Contact not found");
                }
            }
        } catch (InvalidPhoneNumberException e) {
            //TODO recover from problem to ensure message delivery
            Log.d(TAG, "Invalid phone number");
        } catch (InvalidThrowException e) {
            Log.d(TAG, "Invalid throw");
            return;
        }
    }

    public void addConversation(Contact contact) {
        Conversation newConversation;
        boolean conversationChanged = false;

        try {
            newConversation = Conversation.findByParticipant(contact, getApplicationContext(), mMessageHandler);
        } catch(NotFoundInDatabaseException e) {
            newConversation = Conversation.createConversation(contact, getApplicationContext(), mMessageHandler);
        }

       replaceSelectedConversation(newConversation);
    }

    private boolean hasConversationChanged(Conversation newConversation) {
        if(newConversation == null) return false;
        if(mSelectedConversation == null) return true;
        if(mSelectedConversation.equals(newConversation)) return false;
        return true;
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