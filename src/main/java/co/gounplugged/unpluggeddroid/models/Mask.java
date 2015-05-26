package co.gounplugged.unpluggeddroid.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;

/**
 * Created by pili on 20/03/15.
 */
@DatabaseTable(tableName = "masks")
public class Mask {
    private static final String TAG = "Mask";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private String countryCode;
    public String getCountryCode() {
        return countryCode;
    }

    public Mask() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Mask(String fullNumber) throws InvalidPhoneNumberException {
        this.phoneNumber = PhoneNumberParser.parsePhoneNumber(fullNumber);
        this.countryCode = PhoneNumberParser.parseCountryCode(fullNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFullNumber() {
        return countryCode + phoneNumber;
    }
}