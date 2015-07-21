package co.gounplugged.unpluggeddroid.preferences;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.widget.Toast;

import org.jraf.android.backport.switchwidget.SwitchPreference;

import co.gounplugged.unpluggeddroid.application.BaseApplication;

public class DefaultAppPreference extends Preference {

    private Context mContext;

    public DefaultAppPreference(Context context) {
        super(context);
        init(context);
    }

    public DefaultAppPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public DefaultAppPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onClick() {
        super.onClick();
        try {
            //http://android-developers.blogspot.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, mContext.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e ) {
            //This shouldn't even happen, this app should be installed on sms compatible devices only
            Toast.makeText(mContext, "No default sms selection available.", Toast.LENGTH_LONG).show();
        }
    }
}
