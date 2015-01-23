package co.gounplugged.unpluggeddroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Conversation;

public class ConversationAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Conversation> mConversations;

    public ConversationAdapter(Context mContext) {
        this(mContext, null);
    }

    public ConversationAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (conversationList == null)
            this.mConversations = new ArrayList<Conversation>();
        else
            this.mConversations = conversationList;
    }


    public void setConversations(List<Conversation> conversations) {
        mConversations = conversations;
        notifyDataSetChanged();
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
        Conversation conversation = mConversations.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_conversation, parent, false);
        }

        CircularImageView imageView = (CircularImageView) convertView.findViewById(R.id.conversation_icon);
        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));

        return convertView;
    }

    public void addConversation(Conversation conversation) {
        mConversations.add(conversation);
        notifyDataSetChanged();
    }
}

