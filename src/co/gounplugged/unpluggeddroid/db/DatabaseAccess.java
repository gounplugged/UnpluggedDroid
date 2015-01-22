package co.gounplugged.unpluggeddroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Message;

public class DatabaseAccess<T> {

    private static final String DATABASE_NAME = "unplugged.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<T, Long> mDao;
    private static DatabaseHelper mHelper;

    public DatabaseAccess(Context context, Class<T> type) {
        try {
            DatabaseHelper helper = getDatabaseHelperInstance(context);
            mDao = helper.getDao(type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper getDatabaseHelperInstance(Context context) {
        if (mHelper == null) {
            mHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return mHelper;
    }

    public int create(T model) {
        try {
            return mDao.create(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int update(T model) {
        try {
            return mDao.update(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(T model) {
        try {
            return mDao.delete(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public T getById(long id) {
        try {
            QueryBuilder<T, Long> qb = mDao.queryBuilder();
            qb.where().eq("id", id);
            PreparedQuery<T> pq = qb.prepare();
            return mDao.queryForFirst(pq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> getAll() {
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int deleteAll() {
        try {
            DeleteBuilder<T, Long> db = mDao.deleteBuilder();
            return mDao.delete(db.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class DatabaseHelper extends OrmLiteSqliteOpenHelper {

        Context mContext;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;

        }

        @Override
        public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
            createDatabases(db, connectionSource, mContext);
        }

        private void createDatabases(SQLiteDatabase db, ConnectionSource connectionSource, Context context) {
            Class<?>[] columns = {Conversation.class, Message.class};
            try {
                for (Class<?> c : columns) {
                    TableUtils.createTable(connectionSource, c);
                }
            } catch (SQLException e) {
                Log.e(DatabaseHelper.class.getName()," - Can't create database", e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//            if (oldVersion < 2) {
//                db.execSQL("ALTER TABLE \'blabla\' ADD COLUMN \'blabla\' BIGINT DEFAULT 0");
//            }
        }
    }

}
