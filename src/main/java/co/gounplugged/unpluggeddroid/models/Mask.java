package co.gounplugged.unpluggeddroid.models;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;

/**
 * Created by pili on 20/03/15.
 */
@DatabaseTable(tableName = "masks")
public class Mask {
    private static final String TAG = "Mask";
    public static String PHONE_NUMBER_REGEX = "(\\+(1|32))(\\d+)";

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String phoneNumber;

    public String getCountryCode() {
        return countryCode;
    }

    @DatabaseField
    private String countryCode;
    

    public Mask() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFullNumber() {
        return countryCode + phoneNumber;
    }

    public Mask(String fullNumber) throws InvalidPhoneNumberException{
        this.phoneNumber = parsePhoneNumber(fullNumber);
        this.countryCode = parseCountryCode(fullNumber);
    }

    public static String parsePhoneNumber(String fullNumber) throws InvalidPhoneNumberException {
        return splitOnCountryCode(sanitizePhoneNumber(fullNumber), 3);
    }

    public static String parseCountryCode(String fullNumber) throws InvalidPhoneNumberException{
        return splitOnCountryCode(sanitizePhoneNumber(fullNumber), 1);
    }

    public static String splitOnCountryCode(String sanitizedFullNumber, int group) {
        Matcher m = Pattern.compile(PHONE_NUMBER_REGEX).matcher(sanitizedFullNumber);
        m.matches();
        return m.group(group);
    }


    public static String sanitizePhoneNumber(String number) throws InvalidPhoneNumberException {
        number = number.replaceAll("\\(", "");
        number = number.replaceAll("\\)", "");
        number = number.replaceAll("-", "");
        number = number.replaceAll(" ", "");

        if(!isValidFullPhoneNumber(number)) throw new InvalidPhoneNumberException("Malformed phone number");

        return number;
    }

    public static boolean isValidFullPhoneNumber(String number) {
        return number.matches(PHONE_NUMBER_REGEX);
    }

}