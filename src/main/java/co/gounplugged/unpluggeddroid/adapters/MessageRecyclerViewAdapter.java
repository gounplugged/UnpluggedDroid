package co.gounplugged.unpluggeddroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.IncomingAndOutgoingViewHolder> {

    public static final int ITEM_VIEW_TYPE_INCOMING = 0;
    public static final int ITEM_VIEW_TYPE_OUTGOING = 1;

    private static final String TAG = "MessageAdapter";
    private Context mContext;
    private Conversation mCurrentConversation;

    public MessageRecyclerViewAdapter(Context context, Conversation conversation) {
        this.mContext = context;
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
     * If message belongs to current conversation, refresh the view.
     * @param message
     */
    public void addMessage(Message message) {
        if(message.getConversation().equals(mCurrentConversation)) {
            refreshMessages();
        }
    }

    /**
     * Refresh the messages from the current conversation.
     */
    public void refreshMessages() {
        notifyDataSetChanged();
    }

    private List<Message> getMessages() {
        if(mCurrentConversation == null) {
            return new ArrayList<Message>();
        } else {
            return new ArrayList(mCurrentConversation.getMessages());
        }
    }

    @Override
    public IncomingAndOutgoingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_INCOMING:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_incoming, parent, false);
                return new IncomingAndOutgoingViewHolder(view);
            case ITEM_VIEW_TYPE_OUTGOING:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_outgoing, parent, false);
                return new IncomingAndOutgoingViewHolder(view2);
            default:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_incoming, parent, false);
                return new IncomingAndOutgoingViewHolder(view3);
        }
    }

    @Override
    public void onBindViewHolder(IncomingAndOutgoingViewHolder holder, int position) {
        Message message = getMessages().get(position);
        holder.tvMessage.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return getMessages().size();
    }


    @Override
    public int getItemViewType(int position) {
        Message message = getMessages().get(position);
        switch (message.getType()) {
            case Message.TYPE_INCOMING:
                return ITEM_VIEW_TYPE_INCOMING;
            case Message.TYPE_OUTGOING:
                return ITEM_VIEW_TYPE_OUTGOING;
            default:
                return ITEM_VIEW_TYPE_INCOMING;
        }
    }


//    @Override
//    public int getCount() {
//        return getMessages().size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return getMessages().get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Message message = getMessages().get(position);
//        switch (message.getType()) {
//            case Message.TYPE_INCOMING:
//                return 0;
//            case Message.TYPE_OUTGOING:
//                return 1;
//            default:
//                return 0;
//        }
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Message message = getMessages().get(position);
//
//        if (convertView == null) {
//            if (message.isOutgoing()) {
//                convertView = mInflater.inflate(R.layout.list_item_message_outgoing, parent, false);
//            } else {
//                convertView = mInflater.inflate(R.layout.list_item_message_incoming, parent, false);
//            }
//        }
//
//        TextView tvName = (TextView) convertView.findViewById(R.id.tv_message);
//        tvName.setText(message.getText());
//
//        return convertView;

//    }


    public static class IncomingAndOutgoingViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvMessage;

        public IncomingAndOutgoingViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
