package co.gounplugged.unpluggeddroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.predicates.ContactSearchPredicate;
import co.gounplugged.unpluggeddroid.utils.ImageUtil;
import co.gounplugged.unpluggeddroid.utils.Predicate;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> implements SectionIndexer {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Contact> mContacts;
    private List<Contact> mContactsClone;

    private HashMap<String, Integer> mapIndex;
    private String[] sections;

    public ContactRecyclerViewAdapter(Context context, List<Contact> contacts) {
        sortContacts(contacts);
        setupSectionIndexer(contacts);

        this.mContext = context;
        this.mContacts = contacts;
        this.mContactsClone = new ArrayList<>(contacts);
    }

//    public ContactRecyclerViewAdapter(Context context, List<Contact> contacts) {
//        super(context, R.layout.list_item_contact, contacts);
//
//        sortContacts(contacts);
//        setupSectionIndexer(contacts);
//
//        this.mContext = context;
//        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.mContacts = contacts;
//        this.mContactsClone = new ArrayList<>(contacts);
//    }
//
//    @Override
//    public int getCount() {
//        return mContacts.size();
//    }
//
//    @Override
//    public Contact getItem(int position) {
//        return mContacts.get(position);
//    }
//

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Contact contact = mContacts.get(position);
//
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.list_item_contact, parent, false);
//        }
//
//        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
//        tvName.setText(contact.getName());
//
//        CircleImageView ivAvatar = (CircleImageView) convertView.findViewById(R.id.iv_avatar);
//
//        ImageUtil.loadContactImage(mContext, contact, ivAvatar);
//
//        return convertView;
//    }

    public void filter(String query) {
        if (query.length() >= 3) {
            mContacts = (List<Contact>) Predicate.filter(mContacts, new ContactSearchPredicate(query));
            notifyDataSetChanged();
        } else {
            mContacts = new ArrayList<>(mContactsClone);
            notifyDataSetChanged();
        }
    }


    private void setupSectionIndexer(List<Contact> contacts) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < contacts.size(); i++) {
            String name = contacts.get(i).getName();
            String ch = name.substring(0, 1);
            ch = ch.toUpperCase(Locale.US);

            // HashMap will prevent duplicates
            mapIndex.put(ch, i);
        }

        Set<String> sectionLetters = mapIndex.keySet();

        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);

        sections = new String[sectionList.size()];
        sectionList.toArray(sections);
    }

    private void sortContacts(List<Contact> contacts) {
        Collections.sort(contacts, new Comparator<Contact>() {
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);
//        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);

        holder.tvName.setText(contact.getName());
        ImageUtil.loadContactImage(mContext, contact, holder.ivAvatar);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mapIndex.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvName;
        public final CircleImageView ivAvatar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }

}
