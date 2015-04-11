package co.gounplugged.unpluggeddroid.models;

import java.util.List;

/**
 *
 */
public class SecondLine {
    private Contact recipient;
    private Krewe selectedKrewe;

    public SecondLine(Contact ultimateRecipient, Krewe seedKrewe) {
        this.recipient = recipient;
        this.selectedKrewe = seedKrewe;
    }

    public Throw getThrow(String message) {
        return new Throw(message, Contact.DEFAULT_CONTACT_NUMBER, selectedKrewe);
    }

}
