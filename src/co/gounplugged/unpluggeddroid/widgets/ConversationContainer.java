package co.gounplugged.unpluggeddroid.widgets;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.pkmmte.view.CircularImageView;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Conversation;

public class ConversationContainer extends LinearLayout {

    private List<Conversation> mConversations;
    private ConversationContainerListener mListener;

    public ConversationContainer(Context context) {
        super(context);
        init(context);
    }

    public ConversationContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ConversationContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void setConversationListener(ConversationContainerListener listener) {
        mListener = listener;
    }

    private void init(Context context) {
        //get conversations from cache
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        mConversations = conversationAccess.getAll();

        for (Conversation conversation : mConversations) {
            //add conversation to container
            addConversationToContainer(context, conversation);
        }
    }

    public interface ConversationContainerListener {
        public void onConversationSwitch(Conversation conversation);
        public void onConversationSelected(Conversation conversation);
    }


    private void addConversationToContainer(final Context context, final Conversation conversation) {
        final CircularImageView imageView;

        imageView = new CircularImageView(context);
        imageView.setImageDrawable(context.getDrawable(R.drawable.avatar));

        int widthHeightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        LayoutParams lp = new LayoutParams(widthHeightDp, widthHeightDp);

        int horizontalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int verticalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        lp.setMargins(horizontalDp, verticalDp, horizontalDp, verticalDp);

        imageView.setTag("" + conversation.id);

        //set listeners
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mListener != null)
                    mListener.onConversationSelected(conversation);

                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

                View.DragShadowBuilder myShadow = new DragShadowBuilder(imageView);
                v.startDrag(dragData, myShadow, null, 0);

                return true;
            }
        });

        imageView.setOnDragListener( new OnDragListener(){
            @Override
            public boolean onDrag(View v,  DragEvent event){
                switch(event.getAction())
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        if (mListener != null)
                            mListener.onConversationSwitch(conversation);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    default: break;
                }
                return true;
            }
        });

        addView(imageView, lp);

    }


}
