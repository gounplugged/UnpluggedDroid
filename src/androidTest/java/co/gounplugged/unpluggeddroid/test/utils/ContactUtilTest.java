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
}
