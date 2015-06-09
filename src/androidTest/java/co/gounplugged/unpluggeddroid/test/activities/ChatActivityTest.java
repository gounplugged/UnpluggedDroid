package co.gounplugged.unpluggeddroid.test.activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.utils.ConversationUtil;

/**
 * Created by Marvin Arnold on 8/06/15.
 */
public class ChatActivityTest extends ActivityUnitTestCase<ChatActivity> {
    ChatActivity mChatActivity;
    public ChatActivityTest() {
        super(ChatActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
        setActivity(launchActivity("co.gounplugged.unpluggeddroid", ChatActivity.class,null));
        mChatActivity = getActivity();
    }

    public void testGetCurrentConversation() {
        Profile.setLastConversationId(Profile.LAST_SELECTED_CONVERSATION_UNSET_ID);
        ConversationUtil.deleteAll(mChatActivity.getApplicationContext());

        assertNotNull(mChatActivity.getLastSelectedConversation());
    }
}
