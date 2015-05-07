package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Profile;

public class ProfileActivity extends Activity {
    private TextView phoneNumberInput;
    private Button submitButton;
    private Spinner smsPlanSpinner;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile = ((BaseApplication) getApplicationContext()).getProfile();

        setupPhoneNumber();
        setupSmsPlan();
        submitButton = (Button) findViewById(R.id.submit_phone_number_activity_profile);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoneNumber();
                setSmsPlan();
                ((BaseApplication) getApplicationContext()).refreshKnownMasks();
                Intent mainIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                ProfileActivity.this.startActivity(mainIntent);
                ProfileActivity.this.finish();
            }
        });
    }

    private void setSmsPlan() {
        int selectedId = (int) smsPlanSpinner.getSelectedItemId();
        profile.setSmsPlan(selectedId);
        ((BaseApplication) getApplicationContext()).seedKnownMasks();
    }

    private void setPhoneNumber() {
        String phoneNumber = phoneNumberInput.getText().toString();
        profile.setPhoneNumber(phoneNumber);
        ((BaseApplication) getApplicationContext()).seedKnownMasks();
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

        int savedSmsPlan = profile.getSmsPlan();
        smsPlanSpinner.setSelection(savedSmsPlan);
    }

    private void setupPhoneNumber() {
        phoneNumberInput = (TextView) findViewById(R.id.phone_number_activity_profile);
        String savedPhoneNumber = profile.getPhoneNumber();
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
