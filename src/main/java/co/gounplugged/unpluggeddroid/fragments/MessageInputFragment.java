package co.gounplugged.unpluggeddroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.exceptions.InvalidRecipientException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.utils.ImageUtil;

public class MessageInputFragment extends Fragment {
    private static final String TAG = "MessageInputFragment";
    private ImageButton submitButton;
    private EditText newPostText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        Log.d(TAG, "Menu visibility set to : " + visible);
        if (visible && submitButton != null) {
            setSubmitButtonImage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_input, container, false);

        // Submit Button
        submitButton = (ImageButton) view.findViewById(R.id.submit_button);
        setSubmitButtonImage();
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendMessage(MessageInputFragment.this.newPostText.getText().toString());
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
                        sendMessage(newPostText.getText().toString());
                    } catch (InvalidRecipientException e) {
                        //TODO define behavior for when message composed without selecting a conversation
                    }
                }
                return true;
            }
        });

        return view;
    }

    private void setSubmitButtonImage() {
        Conversation lastConvo = ((ChatActivity) getActivity()).getLastSelectedConversation();
        Log.d(TAG, "Found convo");
        if(lastConvo != null) {
            Log.d(TAG, "Convo not null");
            Contact lastContact = lastConvo.getParticipant();
            ImageUtil.loadContactImage(getActivity().getApplicationContext(), lastContact, submitButton);
        }
    }

    private void sendMessage(String message) throws InvalidRecipientException {
        if (TextUtils.isEmpty(message))
            return;

        Conversation conversation = ((ChatActivity) getActivity()).getLastSelectedConversation();
        if(conversation != null) {
            Context context = getActivity().getApplicationContext();
            String text = newPostText.getText().toString();
            conversation.sendMessage(context, text);
            newPostText.setText("");
        }
    }
}
