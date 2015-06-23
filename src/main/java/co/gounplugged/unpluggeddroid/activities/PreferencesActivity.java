package co.gounplugged.unpluggeddroid.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.fragments.ProfilePreferenceFragment;

public class PreferencesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preferences);

        setupToolbar(NAVIGATION_MAIN_SETTINGS);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new ProfilePreferenceFragment());
        fragmentTransaction.commit();
    }
}
