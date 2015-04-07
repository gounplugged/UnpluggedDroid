package co.gounplugged.unpluggeddroid.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.widgets.ConversationContainer;
import de.greenrobot.event.EventBus;

public class ConversationAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Conversation> mConversations;

    public ConversationAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (conversationList == null)
            this.mConversations = new ArrayList<Conversation>();
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

        if (convertView.getTag() == null)
            convertView.setTag(new ViewHolder(convertView, conversation));

        return convertView;
    }

    public void addConversation(Conversation conversation) {
        mConversations.add(conversation);
        notifyDataSetChanged();
    }

    public static class ViewHolder {

        private final Conversation mConversation;
        private final CircularImageView mImageView;

        public ViewHolder(View v, Conversation conversation) {

            mConversation = conversation;

            mImageView = (CircularImageView) v.findViewById(R.id.conversation_icon);
            mImageView.setTag(String.valueOf(conversation.id));
        }

        public Conversation getConversation() {
            return mConversation;
        }

    }


}

