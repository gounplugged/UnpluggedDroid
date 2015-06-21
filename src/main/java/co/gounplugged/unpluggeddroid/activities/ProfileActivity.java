package co.gounplugged.unpluggeddroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Profile;

public class ProfileActivity extends BaseActivity {
    private TextView phoneNumberInput;
    private Button submitButton;
    private Spinner smsPlanSpinner;
    private TextView defaultAppStatusText;
    private Preference defaultAppPreference;
    private TextView passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        displayHomeAsUp();

        setupPhoneNumber();
        setupSmsPlan();
        submitButton = (Button) findViewById(R.id.submit_phone_number_activity_profile);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoneNumber();
                setSmsPlan();
                setPassword();
                ((BaseApplication) getApplicationContext()).refreshKnownMasks();
                ((BaseApplication) getApplicationContext()).generatePGPKey();
                Intent mainIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                ProfileActivity.this.startActivity(mainIntent);
                ProfileActivity.this.finish();
            }
        });

        defaultAppStatusText = (TextView) findViewById(R.id.default_app_status_title_activity_profile);
        if(BaseApplication.getInstance(getApplicationContext()).isDefaultSMSApp()) {
            defaultAppStatusText.setText("This is the default app");
        } else {
            defaultAppStatusText.setText("Not the default app");
            defaultAppStatusText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //http://android-developers.blogspot.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getApplicationContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            });
        }

        passwordInput = (TextView) findViewById(R.id.password_activity_profile);
    }


    private void setSmsPlan() {
        int selectedId = (int) smsPlanSpinner.getSelectedItemId();
        Profile.setSmsPlan(selectedId);
        ((BaseApplication) getApplicationContext()).seedKnownMasks();
    }

    private void setPhoneNumber() {
        String phoneNumber = phoneNumberInput.getText().toString();
        Profile.setPhoneNumber(phoneNumber);
        ((BaseApplication) getApplicationContext()).seedKnownMasks();
    }

    private void setPassword() {
        String password = passwordInput.getText().toString();
        Profile.setPassword(password);
    }


    private void setupSmsPlan() {
        smsPlanSpinner = (Spinner) findViewById(R.id.sms_plan_selection_activity_profile);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sms_plans_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        smsPlanSpinner.setAdapter(adapter);

        int savedSmsPlan = Profile.getSmsPlan();
        smsPlanSpinner.setSelection(savedSmsPlan);
    }

    private void setupPhoneNumber() {
        phoneNumberInput = (TextView) findViewById(R.id.phone_number_activity_profile);
        String savedPhoneNumber = Profile.getPhoneNumber();
        if(savedPhoneNumber != null) {
            phoneNumberInput.setText(savedPhoneNumber);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
