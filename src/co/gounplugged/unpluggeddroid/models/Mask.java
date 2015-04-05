package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by pili on 20/03/15.
 */
@DatabaseTable(tableName = "masks")
public class Mask {
    public static final String COUNTRY_ID_FIELD_NAME = "country_id";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COUNTRY_ID_FIELD_NAME)
    private Country country;
    

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