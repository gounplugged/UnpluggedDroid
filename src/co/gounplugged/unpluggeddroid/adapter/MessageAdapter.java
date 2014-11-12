package co.gounplugged.unpluggeddroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.model.UnpluggedMessage;

import java.util.ArrayList;


public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<UnpluggedMessage> mMessages;

    public MessageAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mMessages = new ArrayList<UnpluggedMessage>();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        UnpluggedMessage message = mMessages.get(position);

        if (convertView == null) {
            if (message.isOutgoing()) {
                convertView = mInflater.inflate(R.layout.list_item_message_outgoing, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.list_item_message_incoming, parent, false);
            }
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_message);
        tvName.setText(message.getMessage());

        return convertView;
    }

    public void addMessage(UnpluggedMessage message) {
        mMessages.add(message);
        notifyDataSetChanged();
    }
}
