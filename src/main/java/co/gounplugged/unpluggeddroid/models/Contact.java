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
import co.gounplugged.unpluggeddroid.utils.ContactUtil;

/*

 */
@DatabaseTable(tableName = "contacts")
public class Contact {
    private static final String TAG = "Contact";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String mPhoneNumber;

    @DatabaseField
    private String mCountryCode;

    @DatabaseField
    private String mName;

    @DatabaseField
    private String mLookupKey;

    @DatabaseField
    private boolean mUsesSecondLine;

    public Contact() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    /**
     * Assume Contacts do not have SL
     * @param name
     * @param fullPhoneNumber
     */
    public Contact(String name, String fullPhoneNumber) throws InvalidPhoneNumberException {
        this(name, fullPhoneNumber, false);
    }

    public Contact(String name, String fullPhoneNumber, boolean usesSecondLine) throws InvalidPhoneNumberException {
        this.mPhoneNumber = PhoneNumberParser.parsePhoneNumber(fullPhoneNumber);
        this.mCountryCode = PhoneNumberParser.parseCountryCode(fullPhoneNumber);
        this.mName = name;
        this.mUsesSecondLine = usesSecondLine;
    }

    public Contact(String name, String fullPhoneNumber, String lookupKey) throws InvalidPhoneNumberException {
        this.mPhoneNumber = PhoneNumberParser.parsePhoneNumber(fullPhoneNumber);
        this.mCountryCode = PhoneNumberParser.parseCountryCode(fullPhoneNumber);
        this.mLookupKey = lookupKey;
        this.mName = name;
    }

    public String getFullNumber() {
        return this.mCountryCode + this.mPhoneNumber;
    }

    public String getName() {
        return mName;
    }

    public Uri getImageUri() {
        return Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_LOOKUP_URI, mLookupKey);
    }

    public boolean usesSecondLine() {
        return mUsesSecondLine;
    }
    public void setUsesSecondLine(Context context, boolean usesSecondLine) {
        this.mUsesSecondLine = usesSecondLine;
        ContactUtil.update(context, this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact))
            return false;
        if (obj == this)
            return true;
        Contact rhs = (Contact) obj;

        return id == rhs.id;
    }


}
