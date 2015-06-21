package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ConversationRecyclerViewAdapter;
import co.gounplugged.unpluggeddroid.adapters.MessageRecyclerViewAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Conversation;

public class MessagesContainerFragment extends Fragment {
    private final static String TAG = "MessageListFragment";

    private Conversation mConversation;

    private RecyclerView mMessagesRecyclerView;
    private MessageRecyclerViewAdapter mMessageRecyclerViewAdapter;

    private RecyclerView mConversationsRecyclerView;
    private ConversationRecyclerViewAdapter mConversationRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(
                R.layout.fragment_messages_container, container, false);

        mMessagesRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupMessagesRecyclerView(mMessagesRecyclerView);

        mConversationsRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewConversations);
        setupConversationsRecyclerView(mConversationsRecyclerView);

        return view;
    }

    private void setupMessagesRecyclerView(RecyclerView recyclerView) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        //get selected conversation
        mConversation = ((ChatActivity)getActivity()).getLastSelectedConversation();

        mMessageRecyclerViewAdapter = new MessageRecyclerViewAdapter(getActivity(), mConversation);
        recyclerView.setAdapter(mMessageRecyclerViewAdapter);
    }
    private void setupConversationsRecyclerView(RecyclerView recyclerView) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        //get conversations from cache
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getActivity(), Conversation.class);

        mConversationRecyclerViewAdapter = new ConversationRecyclerViewAdapter(getActivity(), conversationAccess.getAll());
        recyclerView.setAdapter(mConversationRecyclerViewAdapter);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setLastConversation(Conversation conversation) {
        mMessageRecyclerViewAdapter = new MessageRecyclerViewAdapter(getActivity(), conversation);
        mMessagesRecyclerView.setAdapter(mMessageRecyclerViewAdapter);
    }



}

