package co.gounplugged.unpluggeddroid.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
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
    public static final String DEFAULT_CONTACT_NUMBER = "+13016864576";

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
        this.name = name;;
    }

    public Contact(String name, String fullPhoneNumber, String lookupKey) throws InvalidPhoneNumberException {
        this.phoneNumber = PhoneNumberParser.parsePhoneNumber(fullPhoneNumber);
        this.countryCode = PhoneNumberParser.parseCountryCode(fullPhoneNumber);
        this.mLookupKey = lookupKey;
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFullNumber() {
        return countryCode + phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getName() {
        return name;
    }

    public String getLookupKey() {
        return mLookupKey;
    }
    public void setLookupKey(String lookupKey) {
        this.mLookupKey = lookupKey;
    }

    public static List<Contact> loadContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        DatabaseAccess<Contact> contactAccess = new DatabaseAccess<>(context, Contact.class);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact c = null;
                        try {
                            c = new Contact(name, phoneNo, lookupKey);
                            Log.d(TAG, "Adding Name: " + name + ", Phone No: " + phoneNo);
                            contacts.add(c);
                            contactAccess.create(c);
                        } catch (InvalidPhoneNumberException e) {
                            Log.d(TAG, "Skipping Name: " + name + ", Phone No: " + phoneNo);
                        }
                    }
                    pCur.close();
                }
            }
        }

        return contacts;
    }

    public static Contact getContact(Context context, String phoneNumber) throws NotFoundInDatabaseException {
        try {
            phoneNumber = PhoneNumberParser.parsePhoneNumber(phoneNumber);
        } catch (InvalidPhoneNumberException e) {
            e.printStackTrace();
        }
        DatabaseAccess<Contact> contactAccess = new DatabaseAccess<>(context, Contact.class);
        Contact contact = contactAccess.getFirstString("phoneNumber", phoneNumber);
        if (contact == null) throw new NotFoundInDatabaseException("Could not find a contact with that phone number");
        return contact;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Throw))
            return false;
        if (obj == this)
            return true;
        Contact rhs = (Contact) obj;
        Log.d(TAG, "Comparing Contact " + id + " against " + rhs.id);

        return id == rhs.id;
    }
}
