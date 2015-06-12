package co.gounplugged.unpluggeddroid.test.activities;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.view.ContextThemeWrapper;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.exceptions.InvalidConversationException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.utils.ContactUtil;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;

/**
 * Created by Marvin Arnold on 8/06/15.
 */
public class ChatActivityTest extends ActivityUnitTestCase<ChatActivity> {
    ChatActivity mChatActivity;
    Context mContext;
    public ChatActivityTest() {
        super(ChatActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
        setActivity(launchActivity("co.gounplugged.unpluggeddroid", ChatActivity.class, null));
        mChatActivity = getActivity();
        mContext = mChatActivity.getApplicationContext();
    }

    public void testGetCurrentConversation() {
        Profile.setLastConversationId(Profile.LAST_SELECTED_CONVERSATION_UNSET_ID);
        ConversationUtil.deleteAll(mContext);
        Log.d("ChatActivitTest", "num convos: " + ConversationUtil.getAll(mContext).size());

        assertNull(mChatActivity.getLastSelectedConversation());

        Conversation conversation;
        try {
            conversation = ConversationUtil.createConversation(ContactUtil.create(mContext, "", "+130168645876"), mContext);
        } catch (InvalidConversationException e) {
            assertTrue(false);
            return;
        } catch (InvalidPhoneNumberException e) {
            assertTrue(false);
            return;
        }

        assertEquals(conversation, mChatActivity.getLastSelectedConversation());
    }
}
