package co.gounplugged.unpluggeddroid.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Profile;

public class ProfileIntroFragment extends Fragment {

    @Bind(R.id.rg_sms_plan) RadioGroup radioGroupSmsPlan;
    @Bind(R.id.tv_default_app_status) TextView textViewDefaultAppStatus;

    public static ProfileIntroFragment newInstance() {
        ProfileIntroFragment fragment = new ProfileIntroFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRadioGroup();
        radioGroupSmsPlan.check(0);

        setupDefaultAppTextView();
    }

    private void saveProfileInfo() {
        int smsPlanId = radioGroupSmsPlan.getCheckedRadioButtonId();

        Profile.setSmsPlan(smsPlanId);
    }

    private void setupRadioGroup() {
        final String[] options = getResources().getStringArray(R.array.sms_plans_array);

        int i=0;
        for (String option : options) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(i++);
            radioButton.setText(option);
            radioGroupSmsPlan.addView(radioButton);
        }
    }

    private void setupDefaultAppTextView() {
        if(BaseApplication.getInstance(getActivity()).isDefaultSMSApp()) {
            textViewDefaultAppStatus.setText("This is the default app");
        } else {
            textViewDefaultAppStatus.setText("Not the default app");
            textViewDefaultAppStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //http://android-developers.blogspot.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html
                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getActivity().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    } catch (ActivityNotFoundException e ) {
                        //This shouldn't even happen, this app should be installed on sms compatible devices only
                        Toast.makeText(getActivity(), "No default sms selection available.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }



}
