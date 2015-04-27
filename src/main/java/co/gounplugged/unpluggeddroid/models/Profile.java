package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.j256.ormlite.table.DatabaseTable;

import java.util.StringTokenizer;

import co.gounplugged.unpluggeddroid.R;

/**
 * Created by pili on 5/04/15.
 */

public class Profile {

    public static final int SMS_UNLIMITED_DOMESTIC = 1;
    public static final int SMS_UNLIMITED_INTERNATIONAL = 2;
    public static final int SMS_LIMITED = 3;
    public static final int SMS_DEFAULT = SMS_LIMITED;
    public static final String SMS_PLAN_PREFERENCE_NAME = "SMSPref";

    public static final String PHONE_NUMBER_PREFERENCE_NAME = "CountryPref";

    public int getSmsPlan() {
        return smsPlan;
    }

    private int smsPlan;

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
    }
}
