package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import co.gounplugged.unpluggeddroid.R;

public class ProfilePreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_profile);
    }
}
