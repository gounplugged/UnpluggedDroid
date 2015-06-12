package co.gounplugged.unpluggeddroid.test.utils;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

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
        } catch (OpenPGPBridgeService.EncryptionUnavailableException e) {
            assertTrue(false); return;
        }
        assertEquals(3, krewe.getMasks().size()); // ThrowParser shouldn't mutate krewe
        String content =
                encryptionPrefix(0) +
                phoneBase + "1" + ThrowParser.MASK_SEPARATOR +
                encryptionPrefix(1) +
                (phoneBase + "2" + ThrowParser.MASK_SEPARATOR) +
                encryptionPrefix(2) +
                (krewe.getRecipient().getFullNumber() + ThrowParser.MASK_SEPARATOR) +
                TestOpenPGPService.mockEncryptionPrefix + krewe.getRecipientNumber() +
                (message + ThrowParser.MESSAGE_SEPARATOR +
                originatorNumber + ThrowParser.ORIGINATOR_SEPARATOR);
        assertEquals(content, throwContent);
    }

    public String encryptionPrefix(int maskIndex) {
        return TestOpenPGPService.mockEncryptionPrefix + krewe.getMasks().get(maskIndex).getFullNumber();
    }
/*
    public void testRemoveNextMask() {
        throwContent = ThrowParser.contentFor(message, originatorNumber, krewe, mTestOpenPGPService);
        assertEquals(
            TestOpenPGPService.mockEncryptionPrefix + phoneBase + "2" + ThrowParser.MASK_SEPARATOR +
            TestOpenPGPService.mockEncryptionPrefix + krewe.getRecipientNumber() + ThrowParser.MASK_SEPARATOR +
            TestOpenPGPService.mockEncryptionPrefix + message + ThrowParser.MESSAGE_SEPARATOR +
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
        assertTrue(ThrowParser.isValidRelayThrow(
                PhoneNumberParser.PHONE_NUMBER_REGEX +
                ThrowParser.MASK_SEPARATOR +
                "arstiharistnoirastn"));
    }

    public void testIsValidThrow() {
        assertTrue(
            ThrowParser.isValidThrow(
                TestOpenPGPService.mockEncryptionPrefix +
                "ignoreWIxff+13016864576YzLqQ"));
    }

    public void testMinimumKreweAmount() {
        assertTrue(false);
    } */

    class TestOpenPGPService extends OpenPGPBridgeService {
        public static final String mockEncryptionPrefix = "encrypted";

        @Override
        public String encrypt(String plaintext, String recipientAddress) throws EncryptionUnavailableException {
            return mockEncryptionPrefix + recipientAddress + plaintext;
        }
    }
}