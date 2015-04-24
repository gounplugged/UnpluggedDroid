package co.gounplugged.unpluggeddroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Profile;

public class ProfileActivity extends Activity {
    private TextView phoneNumberInput;
    private Button submitPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        phoneNumberInput = (TextView) findViewById(R.id.phone_number_activity_profile);
        submitPhoneNumber = (Button) findViewById(R.id.submit_phone_number_activity_profile);

        submitPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberInput.getText().toString();
                Profile p = ((BaseApplication) getApplicationContext()).getProfile();
                p.setPhoneNumber(phoneNumber);
                ((BaseApplication) getApplicationContext()).refreshKnownMasks();
                Intent mainIntent = new Intent(ProfileActivity.this, ChatActivity.class);
                ProfileActivity.this.startActivity(mainIntent);
                ProfileActivity.this.finish();
            }
        });
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
