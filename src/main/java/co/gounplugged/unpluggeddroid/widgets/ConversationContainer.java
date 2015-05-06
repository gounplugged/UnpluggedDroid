package co.gounplugged.unpluggeddroid.widgets;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.adapters.ConversationAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Conversation;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationContainer extends LinearLayout {

    private ListView mConversationsListView;
    private List<Conversation> mConversations;
    private List<ConversationListener> mListeners;
    private ConversationAdapter mAdapter;

    //transfers events from conversation-adapter to chat-activity
    private ConversationListener adapterListener = new ConversationListener() {
        @Override
        public void onConversationClicked(Conversation conversation) {
            for (ConversationListener listener : mListeners) {
                listener.onConversationClicked(conversation);
            }
        }
    };

    public ConversationContainer(Context context) {
        super(context);
        init(context);
    }

    public ConversationContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ConversationContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void setConversationListener(ConversationListener listener) {
        mListeners.add(listener);
    }
    public void removeConversationListener(ConversationListener listener) {
        mListeners.remove(listener);
    }

    private void init(final Context context) {
        mListeners = new ArrayList<>(10);

        LayoutInflater.from(context).inflate(R.layout.conversation_container, this);

        mConversationsListView = (ListView) findViewById(R.id.lv_conversations);

        //get conversations from cache
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        mConversations = conversationAccess.getAll();

        mAdapter = new ConversationAdapter(context, mConversations);

        mConversationsListView.setAdapter(mAdapter);

        mConversationsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //Send selected conversation to listeners
                ConversationAdapter.ViewHolder holder = ((ConversationAdapter.ViewHolder)view.getTag());
                Conversation conversation = holder.getConversation();
                for (ConversationListener listener : mListeners) {
                    listener.onConversationClicked(conversation);
                }

                CircleImageView mImageView = (CircleImageView) view.findViewById(R.id.conversation_icon);

                //prepare for drag
                ClipData.Item item = new ClipData.Item((CharSequence)mImageView.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData(mImageView.getTag().toString(), mimeTypes, item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(mImageView);
                view.startDrag(dragData, myShadow, null, 0);

                return true;
            }
        });

    }

    public void addConversation(Conversation conversation) {
        mAdapter.addConversation(conversation);
    }


    public interface ConversationListener {
        public void onConversationClicked(Conversation conversation);
    }




}
