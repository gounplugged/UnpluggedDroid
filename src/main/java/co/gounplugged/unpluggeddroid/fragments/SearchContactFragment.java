package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;

public class SearchContactFragment extends Fragment {
    private final static String TAG = "SearchContactFragment";
    private AutoCompleteTextView contactAutoComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_search, container, false);

        contactAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_contacts);
        contactAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                RelativeLayout rl = (RelativeLayout) view;
                TextView tv = (TextView) rl.findViewById(R.id.tv_name);
                String name = tv.getText().toString();
                String phoneNumbers[] = ContactUtil.getPhoneNumbersForContactName(getActivity().getApplicationContext(),
                        name);

                if (phoneNumbers == null)
                    return;

                String phoneNumber = null;

                if (phoneNumbers.length > 1) {
                    //TODO: let user decicde which number to use..
                    phoneNumber = phoneNumbers[0];
                } else {
                    phoneNumber = phoneNumbers[0];
                }


                addConversation(phoneNumber);
            }

        });

        List<Contact> cachedContacts = ContactUtil.getCachedContacts(getActivity().getApplicationContext());
        ContactAdapter adapter = new ContactAdapter(getActivity().getApplicationContext(), cachedContacts);
        contactAutoComplete.setAdapter(adapter);

        return view;
    }



    private void addConversation(String phoneNr) {
        ((ChatActivity)getActivity()).addConversation(phoneNr);
        contactAutoComplete.setText("");
    }
}
