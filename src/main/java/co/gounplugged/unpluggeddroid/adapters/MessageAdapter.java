package co.gounplugged.unpluggeddroid.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;

public class MessageAdapter extends BaseAdapter {
    private static final String TAG = "MessageAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private Conversation mCurrentConversation;
    private List<Message> mMessages;

    public MessageAdapter(Context context, Conversation conversation) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setConversation(conversation);
    }

    /**
     * Set the current conversation assigned to this adapter.
     * @param conversation
     */
    public void setConversation(Conversation conversation) {
        ConversationUtil.refresh(mContext, conversation);
        this.mCurrentConversation = conversation;
        refreshMessages();
    }

    /**
     * Refresh the messages from the current conversation.
     */
    public void refreshMessages() {
        if(mCurrentConversation == null) {
            this.mMessages = new ArrayList<Message>();
        } else {
            this.mMessages = new ArrayList(mCurrentConversation.getMessages());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        switch (message.getType()) {
            case Message.TYPE_INCOMING:
                return 0;
            case Message.TYPE_OUTGOING:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = mMessages.get(position);

        if (convertView == null) {
            if (message.isOutgoing()) {
                convertView = mInflater.inflate(R.layout.list_item_message_outgoing, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.list_item_message_incoming, parent, false);
            }
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_message);
        tvName.setText(message.getText());

        return convertView;
    }

    /**
     * If message belongs to current conversation, refresh the view.
     * @param message
     */
    public void addMessage(Message message) {
        if(message.getConversation().equals(mCurrentConversation)) {
            refreshMessages();
        }
    }
}
