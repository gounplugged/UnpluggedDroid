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

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;

public class SearchContactFragment extends Fragment {
    private final static String TAG = "SearchContactFragment";
    private Button addConversationButton;
    private AutoCompleteTextView contactAutoComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_search, container, false);

        // Submit Button
        addConversationButton  = (Button) view.findViewById(R.id.add_conversation_button);
        addConversationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addConversation();
            }
        });


        //TODO: run in bg
//        DatabaseAccess<Contact> contactDatabaseAccess = new DatabaseAccess<>(getActivity(), Contact.class);
//        List<Contact> contacts = contactDatabaseAccess.getAll();
//        ContactAdapter adapter = new ContactAdapter(getActivity().getApplicationContext(), contacts);

        contactAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_contacts);
//        contactAutoComplete.setAdapter(adapter);
        contactAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                RelativeLayout rl = (RelativeLayout) arg1;
                TextView tv = (TextView) rl.getChildAt(0);
                contactAutoComplete.setText(tv.getText().toString());

            }

        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        contactAutoComplete.setAdapter(adapter);



        return view;
    }


    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

    private void addConversation() {
        ((ChatActivity)getActivity()).addConversation(contactAutoComplete.getText().toString());
        contactAutoComplete.setText("");
    }
}
