package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidRecipientException;
import co.gounplugged.unpluggeddroid.handlers.MessageHandler;
import co.gounplugged.unpluggeddroid.models.Conversation;

public class MessageInputFragment extends Fragment {

    private ImageButton submitButton;
    private EditText newPostText;
    private MessageHandler mMessageHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessageAdapter adapter = ((ChatActivity)getActivity()).getChatArrayAdapter();
        mMessageHandler = new MessageHandler(adapter, getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_input, container, false);

        // Submit Button
        submitButton = (ImageButton) view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendMessage();
                } catch (InvalidRecipientException e) {
                    e.printStackTrace();
                }
            }
        });

        // Enter pressed submission
        newPostText = (EditText) view.findViewById(R.id.new_post_text);
        newPostText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                // If the action is a key-up event on the return key, send the list_item_message_outgoing
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                    try {
                        sendMessage();
                    } catch (InvalidRecipientException e) {
                        //TODO define behavior for when message composed without selecting a conversation
                    }
                }
                return true;
            }
        });


        return view;
    }

    private void sendMessage() throws InvalidRecipientException {
        try {
            Conversation conversation = ((ChatActivity) getActivity()).getLastSelectedConversation();
            conversation.sendMessage(newPostText.getText().toString(), ((BaseApplication) getActivity().getApplicationContext()).getKnownMasks());
            newPostText.setText("");
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Failure to send", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
