package co.gounplugged.unpluggeddroid.db;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import co.gounplugged.unpluggeddroid.models.Conversation;

public class ConversationDatabaseAccess extends DatabaseAccess<Conversation>{

    public ConversationDatabaseAccess(Context context) {
        super(context, Conversation.class);
    }



    public List<Conversation> getRecentConversations() {
        try {
            return mDao.queryBuilder().orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
