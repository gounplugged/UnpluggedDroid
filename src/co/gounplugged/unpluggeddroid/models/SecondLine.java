package co.gounplugged.unpluggeddroid.models;

import java.util.List;

/**
 *
 */
public class SecondLine {
    private Contact recipient;
    private Krewe krewe;

    public SecondLine(Krewe seedKrewe, Contact recipient) {
        this.recipient = recipient;
        this.krewe = formLine(seedKrewe);
    }

    public Throw getThrow(String message) {
        return new Throw(message, krewe, recipient);
    }

    private Krewe formLine(Krewe seedKrewe) {
        return seedKrewe;
    }

}
