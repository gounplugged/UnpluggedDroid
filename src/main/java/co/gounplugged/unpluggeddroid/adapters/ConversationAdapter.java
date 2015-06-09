package co.gounplugged.unpluggeddroid.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.utils.ImageUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends BaseAdapter {
    private static final String TAG = "ConversationAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Conversation> mConversations;

    public ConversationAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (conversationList == null)
            this.mConversations = new ArrayList<>();
        else
            this.mConversations = conversationList;
    }

    @Override
    public int getCount() {
        return mConversations.size();
    }

    @Override
    public Object getItem(int position) {
        return mConversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Conversation conversation = mConversations.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_conversation, parent, false);
        }

        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder(convertView, conversation);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Load avatar
        Contact contact = conversation.getParticipant();
        ImageUtil.loadContactImage(mContext, contact, viewHolder.mImageView);

        return convertView;
    }

    public void addConversation(Conversation conversation) {
        Log.d(TAG, "conversation added");
        mConversations.add(conversation);
        notifyDataSetChanged();
    }

    public void removeConversation(Conversation conversation) {
        mConversations.remove(conversation);
        notifyDataSetChanged();
    }

    public void setConversations(List<Conversation> conversations) {
        Log.d(TAG, "conversation count: " + conversations.size());
        mConversations = conversations;
        notifyDataSetChanged();
    }

    public static class ViewHolder {

        private final Conversation mConversation;
        private final CircleImageView mImageView;
        private final TextView  mConversationName;

        public ViewHolder(View v, Conversation conversation) {
            mConversation = conversation;
            mImageView = (CircleImageView) v.findViewById(R.id.conversation_icon);
            mImageView.setTag(String.valueOf(conversation.id));
            mConversationName = (TextView) v.findViewById(R.id.conversation_name);
            mConversationName.setText(conversation.getName());
        }

        public Conversation getConversation() {
            return mConversation;
        }
    }


}

