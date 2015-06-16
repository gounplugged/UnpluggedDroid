package co.gounplugged.unpluggeddroid.fragments;

import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ProgressBar;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.BaseActivity;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.adapters.ContactAdapter;
import co.gounplugged.unpluggeddroid.events.ConversationEvent;
import co.gounplugged.unpluggeddroid.exceptions.InvalidConversationException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import de.greenrobot.event.EventBus;

public class SearchContactFragment extends Fragment {

    private final static String TAG = "SearchContactFragment";
    private AutoCompleteTextView mContactAutoComplete;
    private ImageButton mRefreshContactsButton;
    private ProgressBar mContactSyncProgressBar;
    private LoadContactsTask mLoadContactsTask;

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
                mLoadContactsTask = new LoadContactsTask();
                if(Build.VERSION.SDK_INT >= 11)
                    mLoadContactsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    mLoadContactsTask.execute();
            }
        });

        mContactSyncProgressBar = (ProgressBar) view.findViewById(R.id.progress_contacts_sync);

        return view;
    }

    @Override
    public void onDestroy() {
        if (mLoadContactsTask.getStatus() == AsyncTask.Status.RUNNING)
            mLoadContactsTask.cancel(true);
        super.onDestroy();
    }

    private void addConversation(Contact contact) {
        ((ChatActivity)getActivity()).addConversation(contact);
        mContactAutoComplete.setText("");

        Conversation newConversation;

        try {
            newConversation = ConversationUtil.findByParticipant(contact, getActivity());
        } catch(NotFoundInDatabaseException e) {
            try {
                newConversation = ConversationUtil.createConversation(contact, getActivity());
            } catch (InvalidConversationException e1) {
                //TODO let user know something went wrong
                return;
            }
        }


        ConversationEvent event = new ConversationEvent(
                ConversationEvent.ConversationEventType.SWITCHED, newConversation);
        EventBus.getDefault().postSticky(event);
    }

    private class LoadContactsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            toggleProgressBar();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ContactUtil.loadContacts(getActivity().getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled())
                toggleProgressBar();
        }

        private void toggleProgressBar() {
            if (mContactSyncProgressBar.getVisibility() == View.GONE) {
                mContactSyncProgressBar.setVisibility(View.VISIBLE);
                mRefreshContactsButton.setVisibility(View.GONE);
            } else {
                mContactSyncProgressBar.setVisibility(View.GONE);
                mRefreshContactsButton.setVisibility(View.VISIBLE);
            }
        }
    };


}
