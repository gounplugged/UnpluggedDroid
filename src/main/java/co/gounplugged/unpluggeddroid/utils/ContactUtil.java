package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

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
}
