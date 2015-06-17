package co.gounplugged.unpluggeddroid.models.predicates;

import co.gounplugged.unpluggeddroid.models.Contact;

public class ContactNamePredicate implements IPredicate<Contact> {

    private String mQuery;

    public ContactNamePredicate(String query) {
        this.mQuery = query;
    }

    @Override
    public boolean apply(Contact contact) {
        String name = contact.getName().toLowerCase();
        return name.startsWith(mQuery.toLowerCase().toString());
    }
}
