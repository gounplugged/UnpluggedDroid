package co.gounplugged.unpluggeddroid.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pili on 20/03/15.
 */
public class Krewe {
    private List<Mask> masks;

    public Krewe() {
        masks = new ArrayList<Mask>();
    }

    public Krewe(List<Mask> masks) {
        this.masks = masks;
    }

    public void addMask(Mask mask) {
        masks.add(mask);
    }

    public List<Mask> getMasks() {
        return masks;
    }

    public boolean isEmpty() {
        return masks == null || masks.size() == 0;
    }
}
