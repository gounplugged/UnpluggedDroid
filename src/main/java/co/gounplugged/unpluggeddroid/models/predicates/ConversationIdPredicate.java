package co.gounplugged.unpluggeddroid.models.predicates;

import co.gounplugged.unpluggeddroid.models.Conversation;

public class ConversationIdPredicate implements IPredicate<Conversation> {

    private long mId;

    public ConversationIdPredicate(long id) {
        this.mId = id;
    }

    @Override
    public boolean apply(Conversation conversation) {
        return conversation.id == mId;
    }
}
