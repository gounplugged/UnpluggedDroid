package co.gounplugged.unpluggeddroid.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

@DatabaseTable(tableName = "conversations")
public class Conversation {

    @DatabaseField(generatedId = true)
    public long id;

    @ForeignCollectionField
    private Collection<Message> messages;

    public Conversation() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }


    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }
}
