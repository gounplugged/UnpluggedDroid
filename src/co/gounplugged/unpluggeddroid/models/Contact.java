package co.gounplugged.unpluggeddroid.models;

/**
 * Created by pili on 20/03/15.
 */
public class Contact {
    public static final String DEFAULT_CONTACT = "3016864576";

    private String phoneNumber;

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    private String name;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
