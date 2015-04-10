package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.models.Throw;

/**
 * Created by pili on 5/04/15.
 */
public class ThrowTest extends AndroidTestCase {

    Krewe maskRoute;
    Mask m;
    int maskRouteLength;

    String phone = "123";
    String code = "+32";
    String originatorNumber = "+44444";

   @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        maskRouteLength = 3;
        m = new Mask(code + phone);
        maskRoute = new Krewe();

        for(int i = 0; i<= maskRouteLength; i++) {
            maskRoute.addMask(m);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testEncrypt() {
        String message = "test";
        Throw t = new Throw(message, originatorNumber, maskRoute);
        assertEquals(m, t.getThrowTo());
        String encrypted =  code + Throw.COUNTRY_CODE_SEPARATOR + phone + Throw.MASK_SEPARATOR +
                            code + Throw.COUNTRY_CODE_SEPARATOR + phone + Throw.MASK_SEPARATOR +
                            code + Throw.COUNTRY_CODE_SEPARATOR + phone + Throw.MASK_SEPARATOR +
                            message + Throw.MESSAGE_SEPARATOR +
                            originatorNumber + Throw.ORIGINATOR_SEPARATOR;
        assertEquals(encrypted, t.getEncryptedContent());
    }

    public void testDecrypt() {
        String message = "test";
        Throw t = new Throw(message, originatorNumber, maskRoute);
        try {
            t = new Throw(t.getEncryptedContent());
            String encrypted =  code + Throw.COUNTRY_CODE_SEPARATOR + phone + Throw.MASK_SEPARATOR +
                                code + Throw.COUNTRY_CODE_SEPARATOR + phone + Throw.MASK_SEPARATOR +
                                message + Throw.MESSAGE_SEPARATOR +
                                originatorNumber + Throw.ORIGINATOR_SEPARATOR;
            assertEquals(encrypted, t.getEncryptedContent());
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }
}