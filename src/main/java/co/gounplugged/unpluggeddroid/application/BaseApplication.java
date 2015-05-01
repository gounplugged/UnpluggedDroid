package co.gounplugged.unpluggeddroid.application;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Profile;

/**
 * Serves as global application instance
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    private APICaller apiCaller;
    private Krewe mKnownMasks;

    public Profile getProfile() {
        return profile;
    }

    private Profile profile;

    public List<Contact> getContacts() {
        return contacts;
    }

    private List<Contact> contacts;

    /**
     * Get new masks from api or cache on app start
     */
    @Override
    public void onCreate() {
        super.onCreate();
        profile = new Profile(getApplicationContext());
        apiCaller = new APICaller(getApplicationContext());
//        seedKnownMasks();
        loadContacts();
    }

    public void refreshKnownMasks() {
        mKnownMasks = null;
        seedKnownMasks();
    }

    public void seedKnownMasks() {
        if(profile.getPhoneNumber() == null) return;

        if(mKnownMasks == null){
            mKnownMasks = new Krewe();
        }
        DatabaseAccess<Mask> maskAccess = new DatabaseAccess<>(getApplicationContext(), Mask.class);
        // TODO: Prefill from db

        if(mKnownMasks.isEmpty()) {
            apiCaller.getMasks(profile.getCountryCodeFilter());
        }
    }

    public Krewe getKnownMasks() {
        return mKnownMasks;
    }

    public void setKnownMasks(Krewe knownMasks) {
        this.mKnownMasks = knownMasks;
    }

    private void loadContacts() {
        if(profile.areContactsSynced()) return;
        contacts = Contact.loadContacts(getApplicationContext());
        profile.setContactsSynced(true);
    }

    public Conversation getLastSelectedConversation() {
        return Conversation.findById(getApplicationContext(), profile.getLastSelectedConversationId());
    }
}
