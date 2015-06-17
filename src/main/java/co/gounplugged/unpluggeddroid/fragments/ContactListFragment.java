package co.gounplugged.unpluggeddroid.fragments;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Build;
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

    private ContactAdapter mContactAdapter;

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

        if(Build.VERSION.SDK_INT >= 11)
            new LoadCachedContacts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new LoadCachedContacts().execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact c = mContactAdapter.getItem(position);
    }

    private class LoadCachedContacts extends AsyncTask<Void, Void, List<Contact>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setListShown(false);
        }

        @Override
        protected List<Contact> doInBackground(Void... params) {
            return ContactUtil.getCachedContacts(getActivity().getApplicationContext());
        }


        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);

            mContactAdapter = new ContactAdapter(getActivity().getApplicationContext(), contacts);
            setListAdapter(mContactAdapter);

            //setup listview
            getListView().setFastScrollEnabled(true);
            getListView().setOnItemClickListener(ContactListFragment.this);

            setListShown(true);
        }
    }
}

