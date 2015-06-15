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
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.exceptions.InvalidRecipientException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ImageUtil;
import de.greenrobot.event.EventBus;

public class MessageInputFragment extends Fragment {
    private static final String TAG = "MessageInputFragment";
    private ImageButton submitButton;
    private EditText newPostText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ConversationEvent event) {
        switch (event.getType()) {
            case SELECTED:
                break;
            case SWITCHED:
                setSubmitButtonImage(event.getConversation());
                setHint(event.getConversation());
                break;
        }

    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && submitButton != null) {
            setSubmitButtonImage(getLastConversation());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_input, container, false);

        // Submit Button
        submitButton = (ImageButton) view.findViewById(R.id.submit_button);
        setSubmitButtonImage(getLastConversation());

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendMessage(MessageInputFragment.this.newPostText.getText().toString());
                } catch (InvalidRecipientException e) {
                    e.printStackTrace();
                }
            }
        });

        // Setup input field
        newPostText = (EditText) view.findViewById(R.id.new_post_text);
        setHint(getLastConversation());

        // Enter pressed submission
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

    private Conversation getLastConversation() {
        return ((ChatActivity) getActivity()).getLastSelectedConversation();
    }


    private void setHint(Conversation lastConversation) {
        String hint;
        // TODO should really just not show text input on first run
        if(lastConversation == null) {
            hint = getString(R.string.new_post_text_hint_first_run);
        } else {
            String conversationName = lastConversation.getName();
            hint = (getString(R.string.new_post_text_hint)) + " " + conversationName;
        }

        newPostText.setHint(hint);
    }

    private void setSubmitButtonImage(Conversation conversation) {
        if(conversation != null) {
            Contact lastContact = conversation.getParticipant();
            if(lastContact != null && lastContact.getImageUri() != null) ImageUtil.loadContactImage(getActivity().getApplicationContext(), lastContact, submitButton);
        }
    }

    private void sendMessage(String message) throws InvalidRecipientException {
        if (TextUtils.isEmpty(message)) return;

        ChatActivity chatActivity = ((ChatActivity) getActivity());
        OpenPGPBridgeService openPGPBridgeService = chatActivity.getOpenPGPBridgeService();
        if(openPGPBridgeService == null) return;

        Conversation conversation = chatActivity.getLastSelectedConversation();
        if(conversation != null) {
            Log.d(TAG, "ADDING MESSAGE TO CONVO: " + conversation.id);
            Context context = getActivity().getApplicationContext();
            String text = newPostText.getText().toString();
            BaseApplication.App.ThrowManager.sendMessage(conversation, text, openPGPBridgeService);
            newPostText.setText("");
        }
    }
}
