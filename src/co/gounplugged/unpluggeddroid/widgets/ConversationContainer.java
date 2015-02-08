package co.gounplugged.unpluggeddroid.widgets;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pkmmte.view.CircularImageView;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.adapters.ConversationAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Conversation;

public class ConversationContainer extends LinearLayout {

    private ListView mConversationsListView;
    private List<Conversation> mConversations;
    private ConversationContainerListener mListener;

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

    public void setConversationListener(ConversationContainerListener listener) {
        mListener = listener;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.conversation_container, this);

        mConversationsListView = (ListView) findViewById(R.id.lv_conversations);

        //get conversations from cache
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        mConversations = conversationAccess.getAll();

        mConversationsListView.setAdapter(new ConversationAdapter(context, mConversations));
    }


    public interface ConversationContainerListener {
        public void onConversationSwitch(Conversation conversation);
        public void onConversationSelected(Conversation conversation);
    }




}
