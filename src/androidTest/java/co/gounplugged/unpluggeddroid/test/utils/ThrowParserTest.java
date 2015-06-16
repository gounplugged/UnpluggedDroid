package co.gounplugged.unpluggeddroid.test.utils;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;
import co.gounplugged.unpluggeddroid.utils.ThrowParser;

/**
 * Created by pili on 5/04/15.
 */
public class ThrowParserTest extends AndroidTestCase {

    Krewe krewe;
    List<Mask> maskRoute;
    Mask m;
    Contact destination;
    int maskRouteLength;

    String phoneBase = "+1301335113";
    String originatorNumber = "+13016864576";
    String message = "sstestaa";
    String throwContent;

    TestOpenPGPService mTestOpenPGPService;

   @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

       mTestOpenPGPService = new TestOpenPGPService();

        maskRouteLength = 3;
        maskRoute = new ArrayList<Mask>();
        destination = new Contact("Recipient", phoneBase + 9);

        for(int i = 0; i< maskRouteLength; i++) {
            m = new Mask(phoneBase + i);
            maskRoute.add(m);
        }

        krewe = new Krewe(destination, maskRoute);
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testBuildContent() {
        try {
            throwContent = ThrowParser.contentFor(message, originatorNumber, krewe, mTestOpenPGPService);
        } catch (Exception e) {
            assertTrue(false); return;
        }
        assertEquals(3, krewe.getMasks().size()); // ThrowParser shouldn't mutate krewe
        String content =
                ThrowParser.THROW_IDENTIFIER +
                encryptionPrefix(0) +
                (phoneNo(1) + ThrowParser.MASK_SEPARATOR) +
                encryptionPrefix(1) +
                (phoneNo(2) + ThrowParser.MASK_SEPARATOR) +
                encryptionPrefix(2) +
                (krewe.getRecipient().getFullNumber() + ThrowParser.MASK_SEPARATOR) +
                TestOpenPGPService.mockEncryptionPrefix + krewe.getRecipientNumber() +
                (message + ThrowParser.MESSAGE_SEPARATOR +
                originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR);
        assertEquals(content, throwContent);
    }

    public void testGetNextMaskAddress() {
        assertEquals(
                originatorNumber,
                ThrowParser.getNextMaskAddress(
                        originatorNumber +
                        ThrowParser.MASK_SEPARATOR +
                        "aorsitenaorisent"));
    }

    public void testIsValidThrow() {
        assertTrue(ThrowParser.isValidThrow(
                ThrowParser.THROW_IDENTIFIER +
                        "-----BEGIN PGP MESSAGE-----\n" +
                        "hQIMAwNJDWvmOi2RARAAyyEHJtvp+fUh0QnL45W41vT9TGZ35WItT2UTVrMmlge6" +
                        "gcNuEjusvDwoXKhB5AEDiTCDzI8Oynw0AYvBPfcuDQL9AU2OW1xpgD8Nh/yXDvAh"));
    }

    public void testIsFullyDecrypted() {
        assertTrue(ThrowParser.isFullyDecrypted(
                        "art" +
                        ThrowParser.MESSAGE_SEPARATOR +
                        originatorNumber +
                        ThrowParser.ORIGINATOR_SEPARATOR
        ));
    }

    public void testContentForNextThrow() {
        try {
            throwContent = ThrowParser.contentFor(message, originatorNumber, krewe, mTestOpenPGPService);
        } catch (EncryptionUnavailableException e) {
            assertTrue(false);
        } catch (ThrowParser.KreweException e) {
            assertTrue(false);
        }

        try {
            throwContent = mTestOpenPGPService.decrypt(throwContent);
        } catch (EncryptionUnavailableException e) {
            assertTrue(false);
        }

        String content =
                ThrowParser.THROW_IDENTIFIER +
                encryptionPrefix(1) +
                (phoneBase + "2" + ThrowParser.MASK_SEPARATOR) +
                encryptionPrefix(2) +
                (krewe.getRecipient().getFullNumber() + ThrowParser.MASK_SEPARATOR) +
                TestOpenPGPService.mockEncryptionPrefix + krewe.getRecipientNumber() +
                (message + ThrowParser.MESSAGE_SEPARATOR +
                originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR);

        assertEquals(content, ThrowParser.contentFor(throwContent));
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

    // i: 0-9
    private String phoneNo(int i) {
        return phoneBase + Integer.toString(i);
    }

    public void testMinimumKreweAmount() {
        List badRoute = new ArrayList<Mask>();
        krewe = new Krewe(destination, badRoute);

        try {
            ThrowParser.contentFor(message, originatorNumber, new Krewe(destination, badRoute), mTestOpenPGPService);
        } catch (EncryptionUnavailableException e) {
            assertTrue(false);
        } catch (ThrowParser.KreweException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    class TestOpenPGPService extends OpenPGPBridgeService {
        public static final String mockEncryptionPrefix = "encrypted";

        @Override
        public String encrypt(String plaintext, String recipientAddress) throws EncryptionUnavailableException {
            return mockEncryptionPrefix + recipientAddress + plaintext;
        }

        @Override
        public String decrypt(String cipherText) throws EncryptionUnavailableException {
            return cipherText.replaceFirst(mockEncryptionPrefix + PhoneNumberParser.PHONE_NUMBER_REGEX, "");
        }
    }

    public String encryptionPrefix(int maskIndex) {
        return TestOpenPGPService.mockEncryptionPrefix + krewe.getMasks().get(maskIndex).getFullNumber();
    }
}