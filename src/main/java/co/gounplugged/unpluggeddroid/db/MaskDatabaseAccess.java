package co.gounplugged.unpluggeddroid.db;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by Marvin Arnold on 11/07/15.
 */
public class MaskDatabaseAccess extends DatabaseAccess<Mask>{

    public MaskDatabaseAccess(Context context) {
        super(context, Mask.class);
    }

    public Mask getMask(String countryCode, String phoneNumber) {
        QueryBuilder<Mask, Long> queryBuilder = mDao.queryBuilder();
        Where<Mask, Long> where = queryBuilder.where();
        try {
            where.eq("mCountryCode", countryCode);
            where.and();
            where.eq("mPhoneNumber", phoneNumber);
            PreparedQuery<Mask> pq = queryBuilder.prepare();
            return mDao.queryForFirst(pq);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

