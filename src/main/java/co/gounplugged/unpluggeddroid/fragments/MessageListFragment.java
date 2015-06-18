package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.adapters.ContactRecyclerViewAdapter;
import co.gounplugged.unpluggeddroid.adapters.MessageRecyclerViewAdapter;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.exceptions.InvalidConversationException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import de.greenrobot.event.EventBus;

public class MessageListFragment extends Fragment {
    private final static String TAG = "MessageListFragment";

    private MessageRecyclerViewAdapter mMessageRecyclerViewAdapter;
    private Conversation mConversation;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(
                R.layout.fragment_contact_list, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupRecyclerView(rv);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        Conversation conversation = ((ChatActivity)getActivity()).getLastSelectedConversation();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        mMessageRecyclerViewAdapter = new MessageRecyclerViewAdapter(getActivity(), conversation);
        recyclerView.setAdapter(mMessageRecyclerViewAdapter);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setLastConversation(Conversation conversation) {
//        Profile.setLastConversationId(conversation.id);
//        mChatArrayAdapter.setConversation(conversation);
//        getRecycleView().setAdapter
    }



}

