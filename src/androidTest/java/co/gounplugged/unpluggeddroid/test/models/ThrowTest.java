package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

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
    String code = "+00";

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        maskRouteLength = 3;
        m = new Mask(phone, code);
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
        Throw t = new Throw(message, maskRoute);
        assertEquals(m, t.getThrowTo());
        String encrypted =  code + Throw.COUNTRY_CODE_SEPERATOR + phone + Throw.MASK_SEPERATOR +
                            code + Throw.COUNTRY_CODE_SEPERATOR + phone + Throw.MASK_SEPERATOR +
                            code + Throw.COUNTRY_CODE_SEPERATOR + phone + Throw.MASK_SEPERATOR +
                            message + Throw.MESSAGE_SEPERATOR;
        assertEquals(encrypted, t.getEncryptedContent());
    }

    public void testDecrypt() {
        String message = "test";
        Throw t = new Throw(message, maskRoute);
        t = new Throw(t.getEncryptedContent());

        String encrypted =  code + Throw.COUNTRY_CODE_SEPERATOR + phone + Throw.MASK_SEPERATOR +
                code + Throw.COUNTRY_CODE_SEPERATOR + phone + Throw.MASK_SEPERATOR +
                message + Throw.MESSAGE_SEPERATOR;
        assertEquals(encrypted, t.getEncryptedContent());
    }
}

