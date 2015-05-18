package co.gounplugged.unpluggeddroid.application;

import android.app.Application;
import android.util.Log;
import java.util.List;
import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;

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
        loadContacts();
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

    private void loadContacts() {
        if(Profile.areContactsSynced()) return;
        ContactUtil.loadContacts(getApplicationContext());
        Profile.setContactsSynced(true);
    }

}
