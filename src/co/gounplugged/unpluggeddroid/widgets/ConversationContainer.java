package co.gounplugged.unpluggeddroid.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pkmmte.view.CircularImageView;

import co.gounplugged.unpluggeddroid.R;

public class ConversationContainer extends LinearLayout {

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
        CircularImageView imageView = new CircularImageView(context);
        imageView.setImageDrawable(context.getDrawable(R.drawable.avatar));

        int widthHeightDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lp = new LayoutParams(widthHeightDp, widthHeightDp);

        int horizontalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int verticalDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        lp.setMargins(horizontalDp, verticalDp, horizontalDp, verticalDp);

        addView(imageView, lp);
    }




}
