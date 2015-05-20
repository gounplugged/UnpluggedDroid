package co.gounplugged.unpluggeddroid.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import co.gounplugged.unpluggeddroid.application.BaseApplication;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {

        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                BaseApplication.App.ThrowManager.processUnknownSMS(smsMessage);
                Log.d(TAG, "Received message: " + smsMessageStr);
            }
        }
    }
}