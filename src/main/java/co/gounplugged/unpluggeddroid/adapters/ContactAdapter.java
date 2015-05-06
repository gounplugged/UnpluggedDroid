package co.gounplugged.unpluggeddroid.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Contact> mContacts;
    private List<Contact> mContactsClone;
    private List<Contact> mSuggestions;

    private Filter mFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Contact contact = (Contact) resultValue;
            return contact.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if(constraint != null) {
                mSuggestions.clear();
                for (Contact contact : mContactsClone) {
                    if(contact.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        mSuggestions.add(contact);
                    }
                }
                filterResults.values = mSuggestions;
                filterResults.count = mSuggestions.size();
                return filterResults;
            } else {
                return filterResults;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Contact> filteredList = (ArrayList<Contact>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Contact c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    public ContactAdapter(Context context,  List<Contact> contacts) {
        super(context, R.layout.list_item_contact, contacts);
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContacts = contacts;
        this.mContactsClone = new ArrayList<>(contacts);
        this.mSuggestions = new ArrayList<>();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = mContacts.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_contact, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(contact.getName());

        CircleImageView ivAvatar = (CircleImageView) convertView.findViewById(R.id.iv_avatar);

        // When using network requests we will probably have to use the no-fade option
        // https://github.com/hdodenhof/CircleImageView
        Picasso.with(mContext).load(contact.getImageUri()).into(ivAvatar);

        return convertView;
    }



}
