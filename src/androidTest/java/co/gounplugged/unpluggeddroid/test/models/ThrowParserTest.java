package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Throw;
import co.gounplugged.unpluggeddroid.models.ThrowParser;

/**
 * Created by pili on 5/04/15.
 */
public class ThrowParserTest extends AndroidTestCase {

    Krewe maskRoute;
    Mask m;
    int maskRouteLength;

    String phone = "+32123";
    String originatorNumber = "+1444";
    String message = "sstestaa";
    String throwContent;

   @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        maskRouteLength = 3;
        m = new Mask(phone);
        maskRoute = new Krewe();

        for(int i = 0; i< maskRouteLength; i++) {
            maskRoute.addMask(m);
        }
       ;

    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testBuildContent() {
        throwContent = ThrowParser.contentFor(message, originatorNumber, maskRoute);
        String content =  phone + ThrowParser.MASK_SEPARATOR +
                          phone + ThrowParser.MASK_SEPARATOR +
                          phone + ThrowParser.MASK_SEPARATOR +
                          message + ThrowParser.MESSAGE_SEPARATOR +
                          originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR;
        assertEquals(content, throwContent);
    }

    public void testRemoveNextMask() {
        throwContent = ThrowParser.contentFor(message, originatorNumber, maskRoute);
        assertEquals(
             phone + ThrowParser.MASK_SEPARATOR +
             phone + ThrowParser.MASK_SEPARATOR +
             message + ThrowParser.MESSAGE_SEPARATOR +
             originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR,

             ThrowParser.removeNextMask(throwContent)
        );
    }

    public void testGetOriginatorNumber() {
        throwContent = message + ThrowParser.MESSAGE_SEPARATOR +
                originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR;
        assertEquals(originatorNumber, ThrowParser.getOriginatorNumber(throwContent));
    }

    public void testGetMessage() {
        throwContent = message + ThrowParser.MESSAGE_SEPARATOR +
                originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR;
        assertEquals(message, ThrowParser.getMessage(throwContent));
    }

    public void testIsValidRelay() {
        throwContent = ThrowParser.contentFor(message, originatorNumber, maskRoute);
        assertTrue(ThrowParser.isValidRelayThrow(throwContent));
        throwContent = message + ThrowParser.MESSAGE_SEPARATOR +
                originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR;
        assertFalse(ThrowParser.isValidRelayThrow(throwContent));


    }

    public void testIsValidThrow() {
        assertTrue(ThrowParser.isValidThrow("ignoreWIxff+13016864576YzLqQ"));
    }
}