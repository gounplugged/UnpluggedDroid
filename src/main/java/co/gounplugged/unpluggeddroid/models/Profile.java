package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;

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

    public static final String PASSWORD_PREFERENCE_NAME = "PasswordPref";
    public static final String PASSWORD_UNSET_VALUE = "";
    public static String password;

    public static final String PHONE_NUMBER_PREFERENCE_NAME = "CountryPref";

    public static int getSmsPlan() {
        return smsPlan;
    }
    private static int smsPlan;

    public static final int LAST_SELECTED_CONVERSATION_UNSET_ID = -1;
    public static final String LAST_SELECTED_CONVERSATION_ID = "LastSelectedConvoPref";
    private static long lastSelectedConversationId;

    public static final int APPLICATION_STATE_UNINITALIZED = 0;
    public static final int APPLICATION_STATE_INITALIZED = 1;
    public static final String APPLICATION_STATE_PREFERENCE_NAME = "ApplicationStatePref";

    public static int getApplicationState() {
        return applicationState;
    }

    public static void setApplicationState(int state) {
        applicationState = state;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putInt(APPLICATION_STATE_PREFERENCE_NAME, applicationState);
        editor.commit();
    }

    private static int applicationState;

    public static final String ARE_CONTACTS_SYNCED_PREFERENCE_NAME = "ContactsSyncedPref";

    public static boolean areContactsSynced() {
        return contactsSynced;
    }

    public static void setContactsSynced(boolean synced) {
        contactsSynced = synced;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putBoolean(ARE_CONTACTS_SYNCED_PREFERENCE_NAME, contactsSynced);
        editor.commit();
    }

    private static boolean contactsSynced;

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String number) {
        phoneNumber = number;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putString(PHONE_NUMBER_PREFERENCE_NAME, phoneNumber);
        editor.commit();
        updateApplicationState();
    }

    public static void setSmsPlan(int planId) {
        smsPlan = planId;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putInt(SMS_PLAN_PREFERENCE_NAME, planId);
        editor.commit();
        updateApplicationState();
    }

    private static String phoneNumber;

    public static final String SHARED_PREFERENCES_STRING = "co.gounplugged.unpluggeddroid.PROFILE_SHARED_PREFERENCES";
    private static SharedPreferences profileSharedPreferences;

    public static void loadProfile(Context context) {
        profileSharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_STRING, Context.MODE_PRIVATE);

        smsPlan = profileSharedPreferences.getInt(SMS_PLAN_PREFERENCE_NAME, SMS_DEFAULT);
        phoneNumber = profileSharedPreferences.getString(PHONE_NUMBER_PREFERENCE_NAME, null);
        contactsSynced = profileSharedPreferences.getBoolean(ARE_CONTACTS_SYNCED_PREFERENCE_NAME, false);
        lastSelectedConversationId = profileSharedPreferences.getLong(LAST_SELECTED_CONVERSATION_ID, LAST_SELECTED_CONVERSATION_UNSET_ID);
        password = profileSharedPreferences.getString(PASSWORD_PREFERENCE_NAME, PASSWORD_UNSET_VALUE);
        applicationState = profileSharedPreferences.getInt(APPLICATION_STATE_PREFERENCE_NAME, APPLICATION_STATE_UNINITALIZED);
    }

    public static String getCountryCodeFilter() {
        if(smsPlan == SMS_UNLIMITED_INTERNATIONAL) {
            return null;
        } else {
            try {
                return (phoneNumber == null) ? "" : PhoneNumberParser.parseCountryCode(phoneNumber);
            } catch (InvalidPhoneNumberException e) {
                //TODO
                return null;
            }
        }
    }

    public static long getLastConversationId() {
        Log.d(TAG, "Getting conversation " + lastSelectedConversationId);
        return lastSelectedConversationId;
    }

    public static void setLastConversationId(long conversationId) {
        Log.d(TAG, "Setting conversation to " + conversationId);
        lastSelectedConversationId = conversationId;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putLong(LAST_SELECTED_CONVERSATION_ID, lastSelectedConversationId);
        editor.commit();
    }

    public static void setPassword(String pass) {
        Log.d(TAG, "Setting password to " + pass);
        password = pass;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putString(PASSWORD_PREFERENCE_NAME, password);
        editor.commit();
    }

    public static String getPassword() {
        return password;
    }

    // Update application stated based on current settings
    private static void updateApplicationState() {
        switch(applicationState) {
            case APPLICATION_STATE_UNINITALIZED:
                if(isValidPhoneNumber() && isValidSmsPlan()) setApplicationState(APPLICATION_STATE_INITALIZED);
                break;
            case APPLICATION_STATE_INITALIZED:

                break;
        }
    }

    private static boolean isValidPhoneNumber() {
        return phoneNumber != null;
    }

    private static boolean isValidSmsPlan() {
        return true;
    }


}
