package co.gounplugged.unpluggeddroid.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.preferences.DefaultAppPreference;

public class ProfilePreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_profile);


        DefaultAppPreference defaultAppPreference = (DefaultAppPreference) getPreferenceScreen().findPreference(
                getString(R.string.pref_key_profile_default_app));

        if (BaseApplication.getInstance(getActivity()).isDefaultSMSApp())
            getPreferenceScreen().removePreference(defaultAppPreference);
    }


}
