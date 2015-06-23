package co.gounplugged.unpluggeddroid.events;

import co.gounplugged.unpluggeddroid.models.Conversation;

public class ConversationEvent {

    public enum ConversationEventType {
        SELECTED,
        SWITCHED
    }


    private ConversationEventType mType;
    private Conversation mConversation;

    public ConversationEvent(ConversationEventType mType, Conversation mConversation) {
        this.mType = mType;
        this.mConversation = mConversation;
    }

    public ConversationEventType getType() {
        return mType;
    }

    public Conversation getConversation() {
        return mConversation;
    }

}
