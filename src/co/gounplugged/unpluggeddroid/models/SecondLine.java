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
        this.selectedKrewe = formSecondLine(ultimateRecipient, seedKrewe);
    }

    public Throw getThrow(String message) {
        return new Throw(message, selectedKrewe);
    }

    private Krewe formSecondLine(Contact ultimateRecipient, Krewe seedKrewe) {
        seedKrewe.addMask((Mask) ultimateRecipient);
        return seedKrewe;
    }

}
