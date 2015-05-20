package co.gounplugged.unpluggeddroid.handlers;

import android.content.Context;
import android.os.Handler;
import android.telephony.SmsManager;

import co.gounplugged.unpluggeddroid.adapters.MessageAdapter;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Message;
import de.greenrobot.event.EventBus;

public class MessageHandler extends Handler {
    private static final String TAG = "UnpluggedMessageHandler";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;

    private DatabaseAccess<Message> messageDatabaseAccess;

    public MessageHandler(Context context) {
        this.messageDatabaseAccess = new DatabaseAccess<Message>(context, Message.class);
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        Message message = (Message) msg.obj;
        // Post sticky event (Message.class) for message-adapter-controller (ChatActivity) to handle
        // when it is registered to event-bus
        EventBus.getDefault().postSticky(message);
    }
}