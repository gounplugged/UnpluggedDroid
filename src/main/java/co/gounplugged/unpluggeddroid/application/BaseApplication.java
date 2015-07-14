package co.gounplugged.unpluggeddroid.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.db.ConversationDatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.managers.ThrowManager;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.SecondLine;
import co.gounplugged.unpluggeddroid.services.EdgenetClientService;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;

/**
 * Serves as global application instance
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";

    private SecondLine mSecondLine;
    private List<Conversation> mRecentConversations;
    private APICaller mApiCaller ;
    private OpenPGPBridgeService mOpenPGPBridgeService;
    private boolean mIsBoundToOpenPGP = false;
    private ServiceConnection mOpenPGPBridgeConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OpenPGPBridgeService.LocalBinder binder = (OpenPGPBridgeService.LocalBinder) service;
            mOpenPGPBridgeService = binder.getService();
            mIsBoundToOpenPGP = true;
            Log.d(TAG, "bound to pgp bridge");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBoundToOpenPGP = false;
            Log.d(TAG, "unbound from pgp bridge");
        }
    };

    public static final BaseApplication getInstance(Context c) {
        return (BaseApplication) c.getApplicationContext();
    }

    public static class App {

        public static ThrowManager ThrowManager;
    }
    /**
     * Get new masks from api or cache on app start
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "APPLICATION STARTED");
        initManagers();

        Profile.loadProfile(getApplicationContext());
        this.mApiCaller = new APICaller(getApplicationContext());
        refreshKnownMasks();
        mSecondLine = new SecondLine(getApplicationContext());

        switch (Profile.getApplicationState()) {
            case (Profile.APPLICATION_STATE_UNINITALIZED):
                break;
            case (Profile.APPLICATION_STATE_INITALIZED):
                refreshKnownMasks();
                break;
        }

        //todo threading
        mRecentConversations = new ConversationDatabaseAccess(getApplicationContext()).getRecentConversations();

        Log.d(TAG, "APPLICATION PROGRESSED");

        startService(new Intent(this, EdgenetClientService.class));
        startService(new Intent(this, OpenPGPBridgeService.class));

        bindService(
                new Intent(this, OpenPGPBridgeService.class),
                mOpenPGPBridgeConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void unbindFromPGPService() {
        if (mIsBoundToOpenPGP) {
            unbindService(mOpenPGPBridgeConnection);
            mIsBoundToOpenPGP = false;
        }
    }

    public OpenPGPBridgeService getOpenPGPBridgeService() {
        return mOpenPGPBridgeService;
    }

    public void generatePGPKey() {
        if(mIsBoundToOpenPGP) {
            try {
                mOpenPGPBridgeService.generatePGPKey();
            } catch (EncryptionUnavailableException e) {
                // Todo
            }
        }
    }

    private void initManagers() {
        App.ThrowManager = new ThrowManager(getApplicationContext());
    }

    public List<Conversation> getRecentConversations() {
        return mRecentConversations;
    }

    public void addRecentConversation(Conversation conversation) {
        mRecentConversations.add(0, conversation);
    }
    public void removeRecentConversation(Conversation conversation) {
        mRecentConversations.remove(conversation);
    }

    public SecondLine getSecondLine() {
        return mSecondLine;
    }

    public void receiveMasks(List<Mask> masks) {
        Log.d(TAG, "receiveMasks: received " + masks.size());
        for(Mask m : masks) {
            MaskUtil.addToDb(getApplicationContext(), Mask.class, m);
            Log.d(TAG, "receiveMasks: mask created");
        }
    }

    public void refreshKnownMasks() {
        Log.d(TAG, "refreshKnownMasks");
        String filter = Profile.getCountryCodeFilter();
        if(filter != null) mApiCaller.getMasks(filter);
    }

    public void loadContacts() {
        if(Profile.areContactsSynced()) return;
        ContactUtil.loadContactsInThread(getApplicationContext());
        Profile.setContactsSynced(true);
    }

    public boolean isDefaultSMSApp() {
        String defaultApplication = Settings.Secure.getString(getContentResolver(),  SMS_DEFAULT_APPLICATION);
        String thisApplication = getApplicationContext().getPackageName();

        return defaultApplication.equals(thisApplication);
    }
}
