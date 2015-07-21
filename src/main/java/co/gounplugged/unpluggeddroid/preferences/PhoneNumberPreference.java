package co.gounplugged.unpluggeddroid.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import co.gounplugged.unpluggeddroid.models.Profile;

public class PhoneNumberPreference extends EditTextPreference {


    public PhoneNumberPreference(Context context) {
        super(context);
        init(context);
    }

    public PhoneNumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setSummary(Profile.getPhoneNumber());
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            //todo: validate number
            String number = getEditText().getText().toString();
            Profile.setPhoneNumber(number);
            setSummary(number);
        }
    }



}
