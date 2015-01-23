package co.gounplugged.unpluggeddroid.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.models.Conversation;
import de.greenrobot.event.EventBus;

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

    public void setConversationListener() {

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

        final CircularImageView imageView =
                (CircularImageView) convertView.findViewById(R.id.conversation_icon);

        imageView.setTag(String.valueOf(conversation.id));

        //set listeners
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new ConversationEvent(
                        ConversationEvent.ConversationEventType.SELECTED, conversation));

                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imageView);
                v.startDrag(dragData, myShadow, null, 0);

                return true;
            }
        });

        return convertView;
    }

    public void addConversation(Conversation conversation) {
        mConversations.add(conversation);
        notifyDataSetChanged();
    }
}

