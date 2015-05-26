package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Contact;

/**
 * Created by Marvin Arnold on 21/05/15.
 */
public class ContactTest extends AndroidTestCase {
    public void testFullPhoneNumber() {
        String countryCode = "+1";
        String number = "123123";

        try {
            Contact c = new Contact("", countryCode + number);
            assertEquals(countryCode + number, c.getFullNumber());
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }

    }
}
