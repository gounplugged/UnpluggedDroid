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

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            SmsMessage smsMessage = null;
            for (int i = 0; i < sms.length; ++i) {
                Log.d(TAG, "Raw PDU: " + bytesToHex((byte[]) sms[i]));
                smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                smsMessageStr = smsMessageStr + smsMessage.getDisplayMessageBody();
            }

            BaseApplication.App.ThrowManager.processUnknownSMS(smsMessage, smsMessageStr);
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}