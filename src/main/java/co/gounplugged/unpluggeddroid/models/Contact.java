package co.gounplugged.unpluggeddroid.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.exceptions.NotFoundInDatabaseException;

/*

 */
@DatabaseTable(tableName = "contacts")
public class Contact {
    private static final String TAG = "Contact";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private String countryCode;

    @DatabaseField
    private String name;

    @DatabaseField
    private String mLookupKey;

    public Contact() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Contact(String name, String fullPhoneNumber) throws InvalidPhoneNumberException {
        this.phoneNumber = PhoneNumberParser.parsePhoneNumber(fullPhoneNumber);
        this.countryCode = PhoneNumberParser.parseCountryCode(fullPhoneNumber);
        this.name = name;
    }

    public Contact(String name, String fullPhoneNumber, String lookupKey) throws InvalidPhoneNumberException {
        this.phoneNumber = PhoneNumberParser.parsePhoneNumber(fullPhoneNumber);
        this.countryCode = PhoneNumberParser.parseCountryCode(fullPhoneNumber);
        this.mLookupKey = lookupKey;
        this.name = name;
    }

    public String getFullNumber() {
        return countryCode + phoneNumber;
    }

    public String getName() {
        return name;
    }

    public Uri getImageUri() {
        return Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_LOOKUP_URI, mLookupKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact))
            return false;
        if (obj == this)
            return true;
        Contact rhs = (Contact) obj;
        Log.d(TAG, "Comparing Contact " + id + " against " + rhs.id);

        return id == rhs.id;
    }
}
