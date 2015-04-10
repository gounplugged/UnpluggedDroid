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
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testBuildContent() {
        String content =  phone + ThrowParser.MASK_SEPARATOR +
                            phone + ThrowParser.MASK_SEPARATOR +
                            phone + ThrowParser.MASK_SEPARATOR +
                            message + ThrowParser.MESSAGE_SEPARATOR +
                            originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR;
        assertEquals(content, ThrowParser.contentFor(message, originatorNumber, maskRoute));
    }

    public void testRemoveNextMask() {

    }

    public void testGetOriginatorNumber() {

    }

    public void testGetMessage() {

    }
}