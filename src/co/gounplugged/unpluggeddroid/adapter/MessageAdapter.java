package co.gounplugged.unpluggeddroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import co.gounplugged.unpluggeddroid.R;

import java.util.ArrayList;


public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mMessages;

    public MessageAdapter(Context context, ArrayList<String> messages) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mMessages = messages;
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message, parent, false);
        }

        String message = mMessages.get(position);

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_message);
        tvName.setText(message);

        return convertView;
    }

    public void addMessage(String message) {
        mMessages.add(message);
        notifyDataSetChanged();
    }
}
