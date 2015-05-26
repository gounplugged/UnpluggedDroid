package co.gounplugged.unpluggeddroid.test.utils;

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

    public void testGetContact() throws InvalidPhoneNumberException {
        String number = "+123";
        Contact originalContact = ContactUtil.create(getContext(), "", number);

        try {
            Contact foundContact = ContactUtil.getContact(getContext(), number);
            // Can't test for equality of objects because getContact returns the first one it finds
            assertEquals(originalContact.getFullNumber(), foundContact.getFullNumber());
        } catch (NotFoundInDatabaseException e) {
            assertTrue(false);
        }
    }

    public void testFirstOrCreate() {
        String phoneNumber = "+11";
        ContactUtil.deleteAll(getContext());
        try {
            Contact c1 = ContactUtil.create(getContext(), "", phoneNumber);
            Contact c2  = ContactUtil.firstOrCreate(getContext(), "", phoneNumber);

            assertEquals(1, ContactUtil.getAll(getContext()).size());
            assertEquals(c1, c2);
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }

    public void testDeleteAll() {
        try {
            Contact newContact = ContactUtil.create(getContext(), "", "+11");
            ContactUtil.deleteAll(getContext());
            assertEquals(0, ContactUtil.getAll(getContext()).size());
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }
}
