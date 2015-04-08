package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by pili on 5/04/15.
 */

public class Profile {

    public static final int SMS_UNLIMITED_DOMESTIC = 1;
    public static final int SMS_UNLIMITED_INTERNATIONAL = 2;
    public static final int SMS_LIMITED = 3;

    public static final String SHARED_PREFERENCES_STRING = "co.gounplugged.unpluggeddroid.PROFILE_SHARED_PREFERENCES";
    private SharedPreferences profileSharedPreferences;

    public Profile(Context context) {
        profileSharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_STRING, Context.MODE_PRIVATE);
    }
}
