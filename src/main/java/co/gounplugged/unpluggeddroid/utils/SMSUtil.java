package co.gounplugged.unpluggeddroid.utils;

import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.util.ArrayList;

/**
 * Created by Marvin Arnold on 19/05/15.
 */
public class SMSUtil {
    /*
        Break message up into pieces and send.
     */
    public static void sendSms(String fullPhoneNumber, String textToSend) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> messages = smsManager.divideMessage(textToSend);

        try {
            smsManager.sendMultipartTextMessage(fullPhoneNumber, null, messages, null, null);
        } catch (NullPointerException e) {
            // TODO https://github.com/SMSSecure/SMSSecure/blob/master/src/org/smssecure/smssecure/jobs/SmsSendJob.java
            // for way to handle known bug
        }
    }
}
