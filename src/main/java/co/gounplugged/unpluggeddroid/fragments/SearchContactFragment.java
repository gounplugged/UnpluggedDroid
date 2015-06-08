package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;

public class SearchContactFragment extends Fragment {
    private final static String TAG = "SearchContactFragment";
    private AutoCompleteTextView mContactAutoComplete;
    private ImageButton mRefreshContactsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_contact_search, container, false);

        mContactAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_contacts);
        mContactAutoComplete.setHint(R.string.search_hint);

        List<Contact> cachedContacts = ContactUtil.getCachedContacts(getActivity().getApplicationContext());
        final ContactAdapter adapter = new ContactAdapter(getActivity().getApplicationContext(), cachedContacts);

        mContactAutoComplete.setAdapter(adapter);

        mContactAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                Contact contact = adapter.getItem(pos);
                addConversation(contact);
            }

        });

        mRefreshContactsButton = (ImageButton) view.findViewById(R.id.refresh_contacts_button);
        mRefreshContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.loadContactsInThread(v.getContext());
            }
        });

        return view;
    }

    private void addConversation(Contact contact) {
        ((ChatActivity)getActivity()).addConversation(contact);
        mContactAutoComplete.setText("");
    }
}
