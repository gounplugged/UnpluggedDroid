package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Contact;

public class ImageUtil {

    // When using network requests we will probably have to use the no-fade option
    // https://github.com/hdodenhof/CircleImageView
    public static void loadContactImage(Context context, Contact contact, ImageView imageView) {
        Picasso.with(context).load(contact.getImageUri()).placeholder(R.drawable.avatar).into(imageView);
    }


    public static Drawable getDrawableFromUri(Context context, Uri uri) {
        Drawable drawable;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            drawable = Drawable.createFromStream(inputStream, uri.toString() );
        } catch (FileNotFoundException e) {
            drawable = context.getResources().getDrawable(R.drawable.avatar);
        }
        return drawable;
    }

    public static RoundedBitmapDrawable getCircularDrawable(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable circularBitmapDrawable = //
                RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);

        circularBitmapDrawable.setCornerRadius(bitmap.getWidth());

        return circularBitmapDrawable;
    }
}
