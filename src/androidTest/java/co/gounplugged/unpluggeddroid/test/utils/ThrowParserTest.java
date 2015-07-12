package co.gounplugged.unpluggeddroid.test.utils;

import android.test.AndroidTestCase;

import java.io.IOException;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.Base64;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;
import co.gounplugged.unpluggeddroid.utils.ThrowParser;

/**
 * Created by pili on 5/04/15.
 */
public class ThrowParserTest extends AndroidTestCase {

//    Krewe krewe;
//    List<Mask> maskRoute;
//    Mask m;
//    Contact destination;
//    int maskRouteLength;
//
//    String phoneBase = "+1301335113";
//    String originatorNumber = "+13016864576";
//    String message = "sstestaa";
//    String throwContent;
//
//    TestOpenPGPService mTestOpenPGPService;

   @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

//       mTestOpenPGPService = new TestOpenPGPService();
//
//        maskRouteLength = 3;
//        maskRoute = new ArrayList<Mask>();
//        destination = new Contact("Recipient", phoneBase + 9);
//
//        for(int i = 0; i< maskRouteLength; i++) {
//            m = new Mask(phoneBase + i);
//            maskRoute.add(m);
//        }
//
//        krewe = new Krewe(destination, maskRoute);
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testIsEncryptedThrow() {
        assertTrue(ThrowParser.isEncryptedThrow(ThrowParser.THROW_IDENTIFIER + "arostinrasoitenras "));
    }


    class TestOpenPGPService extends OpenPGPBridgeService {
        public static final String mockEncryptionPrefix = "encrypted";

        @Override
        public String encrypt(String plaintext, String recipientAddress) throws EncryptionUnavailableException {
            return mockEncryptionPrefix + recipientAddress + plaintext;
        }

        @Override
        public String decrypt(String cipherText) throws EncryptionUnavailableException {
            cipherText = cipherText.replaceFirst(ThrowParser.THROW_IDENTIFIER, "");
            try {
                cipherText = new String(Base64.decode(cipherText));
                return cipherText.replaceFirst(mockEncryptionPrefix + PhoneNumberParser.PHONE_NUMBER_REGEX, "");
            } catch (IOException e) {
                throw new EncryptionUnavailableException("problem decoding");
            }
        }
    }

//    public String encryptionPrefix(int maskIndex) {
//        return TestOpenPGPService.mockEncryptionPrefix + krewe.getMasks().get(maskIndex).getFullNumber();
//    }
//
//    // i: 0-9
//    private String phoneNo(int i) {
//        return phoneBase + Integer.toString(i);
//    }
}