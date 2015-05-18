package co.gounplugged.unpluggeddroid.models;

import java.util.List;

/**
 *
 */
public class SecondLine {
    private Krewe selectedKrewe;

    public SecondLine(Contact ultimateRecipient, List<Mask> knownMasks) {
        this.selectedKrewe = new Krewe(ultimateRecipient, knownMasks);
    }

    public Throw getThrow(String message, String fromPhone) {
        return new Throw(message, fromPhone, selectedKrewe);
    }

}
