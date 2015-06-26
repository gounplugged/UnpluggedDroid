package co.gounplugged.unpluggeddroid.activities;

import android.os.Bundle;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.gounplugged.unpluggeddroid.R;

public class IntroActivity extends BaseActivity {

    @InjectView(R.id.et_phone_number)
    EditText editTextPhoneNumber;

    @InjectView(R.id.et_password)
    EditText editTextPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        ButterKnife.inject(this);

        


    }
}
