package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.content.SharedPreferences;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;

/**
 * Created by pili on 5/04/15.
 */

public class Profile {

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
    }

    public void setSmsPlan(int planId) {
        this.smsPlan = planId;
        SharedPreferences.Editor editor = profileSharedPreferences.edit();
        editor.putInt(SMS_PLAN_PREFERENCE_NAME, planId);
        editor.commit();
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
    }

    public String getCountryCodeFilter() {
        if(smsPlan == SMS_UNLIMITED_INTERNATIONAL) {
            return null;
        } else {
            try {
                return Mask.parseCountryCode(phoneNumber);
            } catch (InvalidPhoneNumberException e) {
                //TODO
                return null;
            }
        }

    }
}
