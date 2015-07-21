package co.gounplugged.unpluggeddroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "ConversationAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Conversation> mConversations;

    public ConversationRecyclerViewAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (conversationList == null)
            this.mConversations = new ArrayList<>();
        else
            this.mConversations = conversationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Conversation conversation = mConversations.get(position);

        holder.mConversationName.setText(conversation.getName());
        holder.mImageView.setTag(String.valueOf(conversation.id));

    }

    @Override
    public int getItemCount() {
        return mConversations.size();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final CircleImageView mImageView;
        public final TextView  mConversationName;

        public ViewHolder(View v) {
            super(v);
            mImageView = (CircleImageView) v.findViewById(R.id.conversation_icon);
            mConversationName = (TextView) v.findViewById(R.id.conversation_name);
        }

    }


}

