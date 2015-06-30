package co.gounplugged.unpluggeddroid.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.gounplugged.unpluggeddroid.R;

public class IntroActivity extends BaseActivity {

    @Bind(R.id.et_phone_number)
    EditText editTextPhoneNumber;

    @Bind(R.id.et_password)
    EditText editTextPassword;

    @Bind(R.id.rg_sms_plan)
    RadioGroup radioGroupSmsPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        setupToolbar();
        getSupportActionBar().setTitle(getString(R.string.app_name));

        setupRadioGroup();
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
        radioGroupSmsPlan.check(0);
    }


}
