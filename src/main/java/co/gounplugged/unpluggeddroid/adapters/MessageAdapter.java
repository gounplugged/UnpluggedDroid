package co.gounplugged.unpluggeddroid.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Message;

public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Message> mMessages;

    public MessageAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mMessages = new ArrayList<Message>();
    }

    public void setMessages(List<Message> messages) {
        mMessages = messages;
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

    public void addMessage(Message message) {
        mMessages.add(message);
        notifyDataSetChanged();
    }
}
