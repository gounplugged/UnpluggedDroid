package co.gounplugged.unpluggeddroid.test.utils;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;

/**
 * Created by Marvin Arnold on 22/05/15.
 */
public class ContactUtilTest extends AndroidTestCase {

    public void testFirstOrCreate() {
        String phoneNumber = "+13016864576";
        ContactUtil.deleteAll(getContext());
        try {
            Contact c1 = ContactUtil.firstOrCreate(getContext(), "", phoneNumber);
            Contact c2  = ContactUtil.firstOrCreate(getContext(), "", phoneNumber);

            assertEquals(1, ContactUtil.getAll(getContext()).size());
            assertEquals(c1, c2);
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }

    public void testDeleteAll() {
        try {
            ContactUtil.firstOrCreate(getContext(), "", "+13016864576");
            ContactUtil.deleteAll(getContext());
            assertEquals(0, ContactUtil.getAll(getContext()).size());
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }

    public void testRefreshContact() {
        ContactUtil.deleteAll(getContext());

        String name = "Marvin A";
        String lookupKey = "1";
        String phoneNo = "+13036864576";

        ContactUtil.refreshContact(getContext(), lookupKey, name, phoneNo);

        // Should not create a new contact if existing lookup key
        assertEquals(1, ContactUtil.getAll(getContext()).size());

        name = "Marvin B";
        phoneNo = "+13016864576";
        ContactUtil.refreshContact(getContext(), lookupKey, name, phoneNo);

        try {
            Contact c = ContactUtil.lookupContact(getContext(), lookupKey);

            // should refresh info of existing contact
            assertEquals(1, ContactUtil.getAll(getContext()).size());
            assertEquals(phoneNo, c.getFullNumber());
            assertEquals(name, c.getName());
        } catch (NotFoundInDatabaseException e) {
            assertTrue(false);
        }
    }
}
