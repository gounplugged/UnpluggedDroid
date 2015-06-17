package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;

/**
 * Created by Marvin Arnold on 26/05/15.
 */
public abstract class DbUtil {
    public static <T> void deleteAll(Context context, Class<T> clazz) {
        DatabaseAccess<T> databaseAccess = new DatabaseAccess<>(context, clazz);
        databaseAccess.deleteAll();
    }

    public static <T> List<T> getAll(Context context, Class<T> clazz) {
        DatabaseAccess<T> databaseAccess = new DatabaseAccess<>(context, clazz);
        return databaseAccess.getAll();
    }

    public static <T> int refresh(Context context, Class<T> clazz, T t) {
        DatabaseAccess<T> databaseAccess = new DatabaseAccess<>(context, clazz);
        return databaseAccess.refresh(t);
    }

    public static <T> int update(Context context, Class<T> clazz, T t) {
        DatabaseAccess<T> databaseAccess = new DatabaseAccess<>(context, clazz);
        return databaseAccess.update(t);
    }

    public static <T> int addToDb(Context context, Class<T> clazz, T t) {
        DatabaseAccess<T> databaseAccess = new DatabaseAccess<>(context, clazz);
        return databaseAccess.create(t);
    }
}
