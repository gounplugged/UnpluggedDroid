package co.gounplugged.unpluggeddroid.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;

public class ContactListFragment  extends ListFragment implements AdapterView.OnItemClickListener{
    private final static String TAG = "ContactListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get super view and add custom layout to it to make sure setListShown and other helpers are accessible
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_contact_list, container, false);
        parent.addView(v, 0);
        return parent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListShown(false);

        List<Contact> cachedContacts = ContactUtil.getCachedContacts(getActivity().getApplicationContext());
        final ContactAdapter adapter = new ContactAdapter(getActivity().getApplicationContext(), cachedContacts);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);

        setListShown(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

