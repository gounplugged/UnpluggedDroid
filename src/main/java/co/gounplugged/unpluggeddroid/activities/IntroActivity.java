package co.gounplugged.unpluggeddroid.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Profile;

public class IntroActivity extends BaseActivity {

    @Bind(R.id.et_phone_number) EditText editTextPhoneNumber;
    @Bind(R.id.et_password) EditText editTextPassword;
    @Bind(R.id.rg_sms_plan) RadioGroup radioGroupSmsPlan;
    @Bind(R.id.tv_default_app_status) TextView textViewDefaultAppStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        setupToolbar();
        getSupportActionBar().setTitle(getString(R.string.app_name));

        setupRadioGroup();
        radioGroupSmsPlan.check(0);

        setupDefaultAppTextView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_intro_action_next) {
            saveProfileInfo();
            BaseApplication.getInstance(this).seedKnownMasks();
            startMain();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startMain() {
        Intent mainIntent = new Intent(this, ChatActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void saveProfileInfo() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        int smsPlanId = radioGroupSmsPlan.getCheckedRadioButtonId();

        Profile.setPhoneNumber(phoneNumber);
        Profile.setPassword(password);
        Profile.setSmsPlan(smsPlanId);
    }

    private void setupRadioGroup() {
        final String[] options = getResources().getStringArray(R.array.sms_plans_array);

        int i=0;
        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i++);
            radioButton.setText(option);
            radioGroupSmsPlan.addView(radioButton);
        }
    }

    private void setupDefaultAppTextView() {
        if(BaseApplication.getInstance(getApplicationContext()).isDefaultSMSApp()) {
            textViewDefaultAppStatus.setText("This is the default app");
        } else {
            textViewDefaultAppStatus.setText("Not the default app");
            textViewDefaultAppStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //http://android-developers.blogspot.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html
                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getApplicationContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    } catch (ActivityNotFoundException e ) {
                        //This shouldn't even happen, this app should be installed on sms compatible devices only
                        Toast.makeText(getApplicationContext(), "No default sms selection available.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }


}
