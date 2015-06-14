package co.gounplugged.unpluggeddroid.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pili on 20/03/15.
 */
public class Krewe {
    /*
        Sequential list of all masks
     */
    private final List<Mask> masks;
    private final Contact recipient;

    public Krewe(Contact recipient, List<Mask> masks) {
        this.masks = masks;
        this.recipient = recipient;
    }

    public List<Mask> getMasks() {
        return masks;
    }

    public boolean isEmpty() {
        return masks == null || masks.size() == 0;
    }

    public Mask getNextMask() { return masks.get(0); }

    public Contact getRecipient() {
        return recipient;
    }

    public String getRecipientNumber() {
        return recipient.getFullNumber();
    }
}
