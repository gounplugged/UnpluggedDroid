package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;

/**
 * Created by pili on 9/04/15.
 */
public class MaskTest extends AndroidTestCase{

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


        public void testSanitize() {
            try {
                assertEquals("+123456789", PhoneNumberParser.sanitizePhoneNumber("(+)123  --456789  ))"));
            } catch (InvalidPhoneNumberException e) {
                assertTrue(false);
            }
        }

       public void testParseNumber() {
            String n = "+123123";
            try {
                assertEquals("+1", PhoneNumberParser.parseCountryCode(n));
                assertEquals("23123", PhoneNumberParser.parsePhoneNumber(n));
                n = "+3-21-321";
                assertEquals("+32", PhoneNumberParser.parseCountryCode(n));
                assertEquals("1321", PhoneNumberParser.parsePhoneNumber(n));
            } catch (InvalidPhoneNumberException e) {
                assertTrue(false);
            }
        }
 }
