package co.gounplugged.unpluggeddroid.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;

public class ContactUtil {


    public static List<Contact> getCachedContacts(Context context) {
        DatabaseAccess<Contact> databaseAccess  = new DatabaseAccess<>(context, Contact.class);
        return databaseAccess.getAll();
    }

    public static String[] getPhoneNumbersForContactName(Context context, String name) {
        DatabaseAccess<Contact> databaseAccess  = new DatabaseAccess<>(context, Contact.class);
        List<Contact> contactList = databaseAccess.getAllByColumnValue("name", name);
        String numbers[] = new String[contactList.size()];
        int i=0;
        for (Contact c : contactList) {
            numbers[i++] = c.getFullNumber();
        }
        return numbers;
    }

    public InputStream openPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }


}
