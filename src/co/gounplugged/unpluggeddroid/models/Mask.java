package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by pili on 20/03/15.
 */
@DatabaseTable(tableName = "masks")
public class Mask {

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String phoneNumber;

    public Mask() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Mask(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean hasArrived() { return phoneNumber == null; }
}