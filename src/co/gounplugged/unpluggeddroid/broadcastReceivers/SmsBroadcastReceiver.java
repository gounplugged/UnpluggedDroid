package co.gounplugged.unpluggeddroid.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import co.gounplugged.unpluggeddroid.activities.ChatActivity;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    private ChatActivity chatActivity;

    public void setActivity(ChatActivity activity) {
        this.chatActivity = activity;
    }

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
//                String address = smsMessage.getOriginatingAddress();

//                smsMessageStr += "SMS From: " + address + "\n";
//                smsMessageStr += smsBody + "\n";
                smsMessageStr = smsBody;
            }

            //this will update the UI with message
            chatActivity.processThrow(smsMessageStr);
        }
    }
}