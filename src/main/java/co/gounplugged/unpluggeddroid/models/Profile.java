package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;

/**
 * Created by pili on 5/04/15.
 */

public class Profile {
    private static final String TAG = "Profile";
    public static final int SMS_UNLIMITED_DOMESTIC = 0;
    public static final int SMS_UNLIMITED_INTERNATIONAL = 1;
    public static final int SMS_LIMITED = 2;
    public static final int SMS_DEFAULT = SMS_LIMITED;
    public static final String SMS_PLAN_PREFERENCE_NAME = "SMSPref";

    public static final String PHONE_NUMBER_PREFERENCE_NAME = "CountryPref";

    public int getSmsPlan() {
        return smsPlan;
    }
    private int smsPlan;

    public static final int LAST_SELECTED_CONVERSATION_UNSET_ID = -1;
    public static final String LAST_SELECTED_CONVERSATION_ID = "LastSelectedConvoPref";
    private long lastSelectedConversationId;

    public static final int APPLICATION_STATE_UNINITALIZED = 0;
    public static final int APPLICATION_STATE_INITALIZED = 1;
    public static final String APPLICATION_STATE_PREFERENCE_NAME = "ApplicationStatePref";

    public int getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(int applicationState) {
        this.applicationState = applicationState;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putInt(APPLICATION_STATE_PREFERENCE_NAME, applicationState);
        editor.commit();
    }

    private int applicationState;


    public static final String ARE_CONTACTS_SYNCED_PREFERENCE_NAME = "ContactsSyncedPref";

    public boolean areContactsSynced() {
        return contactsSynced;
    }

    public void setContactsSynced(boolean contactsSynced) {
        this.contactsSynced = contactsSynced;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putBoolean(ARE_CONTACTS_SYNCED_PREFERENCE_NAME, contactsSynced);
        editor.commit();
    }

    private boolean contactsSynced;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putString(PHONE_NUMBER_PREFERENCE_NAME, phoneNumber);
        editor.commit();
        updateApplicationState();
    }

    public void setSmsPlan(int planId) {
        this.smsPlan = planId;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putInt(SMS_PLAN_PREFERENCE_NAME, planId);
        editor.commit();
        updateApplicationState();
    }

    private String phoneNumber;

    public static final String SHARED_PREFERENCES_STRING = "co.gounplugged.unpluggeddroid.PROFILE_SHARED_PREFERENCES";
    private SharedPreferences profileSharedPreferences;

    public Profile(Context context) {
        profileSharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_STRING, Context.MODE_PRIVATE);

        smsPlan = profileSharedPreferences.getInt(SMS_PLAN_PREFERENCE_NAME, SMS_DEFAULT);
        phoneNumber = profileSharedPreferences.getString(PHONE_NUMBER_PREFERENCE_NAME, null);
        contactsSynced = profileSharedPreferences.getBoolean(ARE_CONTACTS_SYNCED_PREFERENCE_NAME, false);
        lastSelectedConversationId = profileSharedPreferences.getLong(LAST_SELECTED_CONVERSATION_ID, LAST_SELECTED_CONVERSATION_UNSET_ID);
        applicationState = profileSharedPreferences.getInt(APPLICATION_STATE_PREFERENCE_NAME, APPLICATION_STATE_UNINITALIZED);
    }

    public String getCountryCodeFilter() {
        if(smsPlan == SMS_UNLIMITED_INTERNATIONAL) {
            return null;
        } else {
            try {
                return PhoneNumberParser.parseCountryCode(phoneNumber);
            } catch (InvalidPhoneNumberException e) {
                //TODO
                return null;
            }
        }
    }

    public long getLastConversationId() {
        Log.d(TAG, "Getting conversation " + lastSelectedConversationId);
        return lastSelectedConversationId;
    }

    public void setLastConversationId(long conversationId) {
        Log.d(TAG, "Setting conversation to " + conversationId);
        this.lastSelectedConversationId = conversationId;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putLong(LAST_SELECTED_CONVERSATION_ID, lastSelectedConversationId);
        editor.commit();
    }

    // Update application stated based on current settings
    private void updateApplicationState() {
        switch(applicationState) {
            case APPLICATION_STATE_UNINITALIZED:
                if(isValidPhoneNumber() && isValidSmsPlan()) setApplicationState(APPLICATION_STATE_INITALIZED);
                break;
            case APPLICATION_STATE_INITALIZED:

                break;
        }
    }

    private boolean isValidPhoneNumber() {
        return phoneNumber != null;
    }

    private boolean isValidSmsPlan() {
        return true;
    }
}
