package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by Marvin Arnold on 18/05/15.
 */
public class MaskUtil {
    public static List<Mask> getCachedMasks(Context context) {
        DatabaseAccess<Mask> databaseAccess  = new DatabaseAccess<>(context, Mask.class);
        return databaseAccess.getAll();
    }
}
