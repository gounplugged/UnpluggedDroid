package co.gounplugged.unpluggeddroid.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import co.gounplugged.unpluggeddroid.application.BaseApplication;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";
    private static final String SMS_RECEIVED_ACTION  = Telephony.Sms.Intents.SMS_RECEIVED_ACTION;
    private static final String SMS_DELIVERED_ACTION = Telephony.Sms.Intents.SMS_DELIVER_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( isValidSMSReceivedAction(context, intent) || isValidSMSDeliveredAction(context, intent)) {
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
    }

    /**
     * To distinguish the two SMS broadcasts, imagine that the SMS_RECEIVED_ACTION simply says
     * "the system received an SMS," whereas the SMS_DELIVER_ACTION says
     * "the system is delivering your app an SMS, because you're the default SMS app."
     * @param context
     * @param intent
     * @return
     */
    public boolean isValidSMSDeliveredAction(Context context, Intent intent) {
        return intent.getAction().equals(SMS_DELIVERED_ACTION);
    }

    /**
     * Valid if intent contains the right action and not currently the default app.
     *
     * To distinguish the two SMS broadcasts, imagine that the SMS_RECEIVED_ACTION simply says
     * "the system received an SMS," whereas the SMS_DELIVER_ACTION says
     * "the system is delivering your app an SMS, because you're the default SMS app."
     * @param context
     * @param intent
     * @return
     */
    public boolean isValidSMSReceivedAction(Context context, Intent intent) {
        return intent.getAction().equals(SMS_RECEIVED_ACTION) &&
                !((BaseApplication) context.getApplicationContext()).isDefaultSMSApp();
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