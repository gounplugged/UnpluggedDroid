package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.models.Contact;

public class ImageUtil {

    // When using network requests we will probably have to use the no-fade option
    // https://github.com/hdodenhof/CircleImageView
    public static void loadContactImage(Context context, Contact contact, ImageView imageView) {
        Picasso.with(context).load(contact.getImageUri()).placeholder(R.drawable.avatar).into(imageView);
    }
}
