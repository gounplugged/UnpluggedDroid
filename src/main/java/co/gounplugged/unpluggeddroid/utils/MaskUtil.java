package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.db.MaskDatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by Marvin Arnold on 18/05/15.
 */
public class MaskUtil extends DbUtil {
    public static Mask getMask(Context context, String countryCode, String phoneNumber) {
        return (new MaskDatabaseAccess(context)).getMask(countryCode, phoneNumber);
    }

    public static Mask getMask(Context context, String fullPhoneNumber) throws InvalidPhoneNumberException {
        String countryCode = PhoneNumberParser.parseCountryCode(fullPhoneNumber);
        String phoneNumber = PhoneNumberParser.parsePhoneNumber(fullPhoneNumber);

        return getMask(context, countryCode, phoneNumber);
    }

    public static Mask create(Context context, String fullPhoneNumber) throws InvalidPhoneNumberException {
        DatabaseAccess<Mask> maskAccess = new DatabaseAccess<>(context, Mask.class);
        Mask m = new Mask(fullPhoneNumber);
        maskAccess.create(m);
        return m;
    }

    public static List<Mask> getKnownMasks(Context context) {
        return getAll(context, Mask.class);
    }

}
