package co.gounplugged.unpluggeddroid.test.adapters;

import android.test.AndroidTestCase;
import android.util.Log;

import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;
import co.gounplugged.unpluggeddroid.utils.MessageUtil;

/**
 * Created by Marvin Arnold on 25/05/15.
 */
public class MessageAdapterTest extends AndroidTestCase {
    public void testConversationWMessages() {
        try {
            Contact participant = ContactUtil.firstOrCreate(getContext(), "", "+13016864576");
            Conversation conversation = ConversationUtil.createConversation(participant, getContext());

            Message m1 = MessageUtil.create(getContext(), conversation, "hi", Message.TYPE_OUTGOING, System.currentTimeMillis());
            Message m2 = MessageUtil.create(getContext(), conversation, "hi", Message.TYPE_OUTGOING, System.currentTimeMillis());

            MessageAdapter messageAdapter = new MessageAdapter(getContext(), conversation);

            assertEquals(messageAdapter.getCount(), 2);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testConversationWOMessages() {
        try {
            Contact participant = ContactUtil.firstOrCreate(getContext(), "", "+13016864576");
            Conversation conversation = ConversationUtil.createConversation(participant, getContext());
            MessageAdapter messageAdapter = new MessageAdapter(getContext(), conversation);

            assertEquals(messageAdapter.getCount(), 0);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testNullConversation() {
        MessageAdapter messageAdapter = new MessageAdapter(getContext(), null);
        assertEquals(messageAdapter.getCount(), 0);
    }
}
