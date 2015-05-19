package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.PhoneNumberParser;

/**
 * Created by Marvin Arnold on 19/05/15.
 */
public class PhoneNumberParserTest extends AndroidTestCase {
    String marvin = "+13016864576";
    String tim = "+32475932921";

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testValidPhoneNumbers() {
        assertTrue(PhoneNumberParser.isValidFullPhoneNumber(marvin));
        assertTrue(PhoneNumberParser.isValidFullPhoneNumber(tim));
    }

    public void testParsePhoneNumbers() {
        try {
            assertEquals(PhoneNumberParser.parseCountryCode(marvin), "+1");
            assertEquals(PhoneNumberParser.parseCountryCode(tim), "+32");

            assertEquals(PhoneNumberParser.parsePhoneNumber(marvin), "3016864576");
            assertEquals(PhoneNumberParser.parsePhoneNumber(tim), "475932921");
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }

    public void testMakeValid() {
        boolean shouldPass = false;
        try {
            assertEquals(PhoneNumberParser.makeValid("3016864576", "+1"), marvin);
            shouldPass = true;
            assertEquals(PhoneNumberParser.makeValid("3016864@#$%^&576", "+1"), marvin);
        } catch (InvalidPhoneNumberException e) {
            assertTrue(shouldPass);
        }
    }
}
