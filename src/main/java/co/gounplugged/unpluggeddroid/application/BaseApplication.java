package co.gounplugged.unpluggeddroid.application;

import android.app.Application;
import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.List;
import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidThrowException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.exceptions.PrematureReadException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;
import co.gounplugged.unpluggeddroid.utils.SMSUtil;

/**
 * Serves as global application instance
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    private APICaller mApiCaller;
    private List<Mask> mKnownMasks;

    /**
     * Get new masks from api or cache on app start
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Profile.loadProfile(getApplicationContext());
        mApiCaller = new APICaller(getApplicationContext());

        switch(Profile.getApplicationState()) {
            case(Profile.APPLICATION_STATE_UNINITALIZED):

                break;
            case(Profile.APPLICATION_STATE_INITALIZED):
                seedKnownMasks();
                break;
        }
    }

    public void refreshKnownMasks() {
        mKnownMasks = null;
        seedKnownMasks();
    }

    public void seedKnownMasks() {
        if(mKnownMasks == null) mKnownMasks = MaskUtil.getCachedMasks(getApplicationContext());

        if(mKnownMasks.isEmpty()) mApiCaller.getMasks(Profile.getCountryCodeFilter());

        Log.d(TAG, "Seeded masks " + mKnownMasks.size());
    }

    public List<Mask> getKnownMasks() {
        seedKnownMasks();
        Log.d(TAG, "There are this many known masks " + mKnownMasks.size());
        return mKnownMasks;
    }

    public void setKnownMasks(List<Mask> knownMasks) {
        Log.d(TAG, "Setting masks " + knownMasks.size());
        this.mKnownMasks = knownMasks;
    }

    public void loadContacts() {
        if(Profile.areContactsSynced()) return;
        ContactUtil.loadContacts(getApplicationContext());
        Profile.setContactsSynced(true);
    }

}
