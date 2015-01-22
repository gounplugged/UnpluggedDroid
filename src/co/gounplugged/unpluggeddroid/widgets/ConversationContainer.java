package co.gounplugged.unpluggeddroid.widgets;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pkmmte.view.CircularImageView;

import co.gounplugged.unpluggeddroid.R;

public class ConversationContainer extends LinearLayout {

    CircularImageView imageView;
    LayoutParams layoutParams;

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

        //add image to container
        imageView = new CircularImageView(context);
        imageView.setImageDrawable(context.getDrawable(R.drawable.avatar));

        int widthHeightDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lp = new LayoutParams(widthHeightDp, widthHeightDp);

        int horizontalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int verticalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        lp.setMargins(horizontalDp, verticalDp, horizontalDp, verticalDp);



        addView(imageView, lp);


        
        final String IV_TAG = "icon bitmap";
        imageView.setTag(IV_TAG);


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

        final String msg="DRAGBABY";
        // Create and set the drag event listener for the View
        imageView.setOnDragListener( new OnDragListener(){
            @Override
            public boolean onDrag(View v,  DragEvent event){
                switch(event.getAction())
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (LinearLayout.LayoutParams)
                                v.getLayoutParams();
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
                        // Do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED :
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION  :
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED   :
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");
                        // Do nothing
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.d(msg, "ACTION_DROP event");
                        // Do nothing
                        break;
                    default: break;
                }
                return true;
            }
        });


    }




}