package co.gounplugged.unpluggeddroid.utils;

import android.telephony.SmsManager;

/**
 * Created by Marvin Arnold on 19/05/15.
 */
public class SMSUtil {
    public static void sendSms(String fullPhoneNumber, String s) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(fullPhoneNumber, null, s, null, null);
    }
}
