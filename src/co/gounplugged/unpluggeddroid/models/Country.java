package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by pili on 5/04/15.
 */
@DatabaseTable(tableName = "countries")
public class Country {

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String countryCode;

    @DatabaseField
    private String humanName;

    public Country() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }
}
