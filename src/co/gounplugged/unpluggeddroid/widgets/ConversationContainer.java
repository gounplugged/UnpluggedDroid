package co.gounplugged.unpluggeddroid.widgets;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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

    CircularImageView imageView;
    List<Conversation> mConversations;

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

    private void init(Context context) {

        //get conversations from cache
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(context, Conversation.class);
        mConversations = conversationAccess.getAll();

        for (Conversation conversation : mConversations) {
            //add image to container
            addImageToContainer(context, ""+conversation.id);
        }




    }

    public interface ConversationContainerListener {
        public void onConversationSwitch();
    }


    private void addImageToContainer(Context context, String tag) {
        imageView = new CircularImageView(context);
        imageView.setImageDrawable(context.getDrawable(R.drawable.avatar));

        int widthHeightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        LayoutParams lp = new LayoutParams(widthHeightDp, widthHeightDp);

        int horizontalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int verticalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        lp.setMargins(horizontalDp, verticalDp, horizontalDp, verticalDp);

        addView(imageView, lp);

        imageView.setTag(tag);

        //set listeners
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // Create a new ClipData.Item from the ImageView object's tag
                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());

                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

                // Instantiates the drag shadow builder.
                View.DragShadowBuilder myShadow = new DragShadowBuilder(imageView);

                // Starts the drag

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
//                        layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
//                        layoutParams.leftMargin = x_cord;
//                        layoutParams.topMargin = y_cord;
//                        v.setLayoutParams(layoutParams);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        //Switch conversation


                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    default: break;
                }
                return true;
            }
        });
    }


}
