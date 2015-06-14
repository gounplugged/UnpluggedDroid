package co.gounplugged.unpluggeddroid.test.utils;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.exceptions.InvalidConversationException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;

/**
 * Created by Marvin Arnold on 11/06/15.
 */
public class ConversationUtilTest extends AndroidTestCase {
    public void testDeleteAll() {
        try {
            ConversationUtil.createConversation(ContactUtil.firstOrCreate(getContext(), "", "+130168645876"), getContext());

            ConversationUtil.deleteAll(getContext());
            assertEquals(0, ConversationUtil.getAll(getContext()).size());
        } catch (InvalidConversationException e) {
            assertTrue(false);
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
        }
    }
}
