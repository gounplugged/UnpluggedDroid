package co.gounplugged.unpluggeddroid.test.managers;

import android.telephony.SmsMessage;
import android.test.AndroidTestCase;
import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.managers.ThrowManager;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import co.gounplugged.unpluggeddroid.utils.MessageUtil;

/**
 * Created by Marvin Arnold on 26/05/15.
 */
public class ThrowManagerTest extends AndroidTestCase {
    ThrowManager throwManager;
    String phoneNumInPDU;

    String marvinText;
    byte[] marvinPDU;
    SmsMessage marvinMessage;;

    // "marvin " (with space)
    String marvin_Text;
    byte[] marvin_PDU;
    SmsMessage marvin_Message;

    Contact marvin;

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        throwManager = new ThrowManager(getContext());
        phoneNumInPDU = "+13016864576";

        marvinText = "marvin";
        marvinPDU = hexStringToByteArray("07914140540510F1040B913110864675F600005150621120210A06EDB0DC9E7603");
        marvinMessage = SmsMessage.createFromPdu(marvinPDU);

        // "marvin " (with space)
        marvin_Text = "marvin ";
        marvin_PDU = hexStringToByteArray("07914140540510F1040B913110864675F600005150620125850A07EDB0DC9E768300");
        marvin_Message = SmsMessage.createFromPdu(marvin_PDU);

        ContactUtil.deleteAll(getContext());
        ConversationUtil.deleteAll(getContext());

        try {
            marvin = ContactUtil.firstOrCreate(getContext(), marvinText, phoneNumInPDU);
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
            return;
        }
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testUnderstandsSLMessages() {
        throwManager.processUnknownSMS(marvinMessage, marvinText);
        assertEquals(1, ContactUtil.getAll(getContext()).size()); // No additional contacts created
        assertFalse(marvin.usesSecondLine());

        throwManager.processUnknownSMS(marvin_Message, marvin_Text);
        ContactUtil.refresh(getContext(), marvin);
        assertEquals(1, ContactUtil.getAll(getContext()).size()); // No additional contacts created
        assertTrue(marvin.usesSecondLine());
    }

    /**
     * Ensure whitespace not added to stored messages
     */
    public void testSLIdentifierNotAdded() {
        MessageUtil.deleteAll(getContext());
        throwManager.processUnknownSMS(marvin_Message, marvin_Text);
        assertEquals(marvinText, MessageUtil.getAll(getContext()).get(0).getText());
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
