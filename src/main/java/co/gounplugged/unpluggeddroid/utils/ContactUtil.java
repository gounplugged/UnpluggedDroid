package co.gounplugged.unpluggeddroid.utils;

import android.content.Context;

import java.util.List;

import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;

public class ContactUtil {

    public static String[] getContactNames(Context context) {
        DatabaseAccess<Contact> databaseAccess  = new DatabaseAccess<>(context, Contact.class);
        List<Contact> contactList = databaseAccess.getAll();
        String names[] = new String[contactList.size()];
        int i=0;
        for (Contact c : contactList) {
            names[i++] = c.getName();
        }
        return names;
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
