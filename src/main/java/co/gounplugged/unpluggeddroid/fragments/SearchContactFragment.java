package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;

public class SearchContactFragment extends Fragment {
    private final static String TAG = "SearchContactFragment";
    private AutoCompleteTextView contactAutoComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_search, container, false);

        contactAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_contacts);

        List<Contact> cachedContacts = ContactUtil.getCachedContacts(getActivity().getApplicationContext());
        final ContactAdapter adapter = new ContactAdapter(getActivity().getApplicationContext(), cachedContacts);

        contactAutoComplete.setAdapter(adapter);

        contactAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                Contact contact = adapter.getItem(pos);
                addConversation(contact);
            }

        });

        return view;
    }



    private void addConversation(Contact contact) {
        ((ChatActivity)getActivity()).addConversation(contact);
        contactAutoComplete.setText("");
    }
}
