package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;

public class SearchContactFragment extends Fragment {
    private final static String TAG = "SearchContactFragment";
    private Button addConversationButton;
    private EditText contactSearch;

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

        contactSearch = (EditText) view.findViewById(R.id.search_contact_text);
        return view;
    }

    private void addConversation() {
        ((ChatActivity)getActivity()).addConversation(contactSearch.getText().toString());
//        DatabaseAccess<Contact> contactAccess = new DatabaseAccess<>(getActivity().getApplicationContext(), Contact.class);
//        Contact c = contactAccess.getFirstString("phoneNumber", contactName);
//        if(c==null) {
//            Log.d(TAG, "NOTHING FOUND");
//        } else {
//            Log.d(TAG, "Found contact " + c.getName());
//        }
        contactSearch.setText("");
    }
}
