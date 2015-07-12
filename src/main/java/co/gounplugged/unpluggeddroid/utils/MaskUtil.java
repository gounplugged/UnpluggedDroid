package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by Marvin Arnold on 18/05/15.
 */
public class MaskUtil {
    public static List<Mask> getCachedMasks(Context context) {
        DatabaseAccess<Mask> databaseAccess  = new DatabaseAccess<>(context, Mask.class);
        return databaseAccess.getAll();
    }

    public static Mask getMask(Context context, String countryCode, String phoneNumber) throws NotFoundInDatabaseException {
        DatabaseAccess<Mask> maskAccess = new DatabaseAccess<>(context, Mask.class);
        QueryBuilder<Mask, String> query = maskAccess.m
//        try {
//            phoneNumber = PhoneNumberParser.parsePhoneNumber(phoneNumber);
//        } catch (InvalidPhoneNumberException e) {
//            e.printStackTrace();
//        }
//        DatabaseAccess<Contact> contactAccess = new DatabaseAccess<>(context, Contact.class);
//        Contact contact = contactAccess.getFirstString("mPhoneNumber", phoneNumber);
//        if (contact == null) throw new NotFoundInDatabaseException("Could not find a contact with that phone number");
//        return contact;
    }
}
