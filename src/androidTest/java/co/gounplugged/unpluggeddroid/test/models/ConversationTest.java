package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Conversation;

/**
 * Created by Marvin Arnold on 21/05/15.
 */
public class ConversationTest extends AndroidTestCase {
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

    public void sameCompatibilityAsParticipant() {
        String number = "+13016864576";
        try {
            Contact user = new Contact("",number, true);
            Contact hater = new Contact("", number, false);

            Conversation users = new Conversation(user);
            Conversation haters = new Conversation(hater);

            assertTrue(users.isSecondLineComptabile(getContext()));
            assertFalse(haters.isSecondLineComptabile(getContext()));
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testSameCompatibilityAsSwitchingParticipant() {
        String number = "+13016864576";
        try {
            Contact user = new Contact("",number, false);
            Conversation users = new Conversation(user);

            assertFalse(users.isSecondLineComptabile(getContext()));
            user.setUsesSecondLine(getContext(), true);
            assertTrue(users.isSecondLineComptabile(getContext()));
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
