package co.gounplugged.unpluggeddroid.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;

/**
 * Created by Marvin Arnold on 27/04/15.
 */
public class PhoneNumberParser {
    public static String PHONE_NUMBER_REGEX = "(\\+(1|32))(\\d+)";

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

    public static String makeValid(String number, String expectedCountryCode) throws  InvalidPhoneNumberException {
        String newNumber =  sanitizePhoneNumber(expectedCountryCode + number);
        if(isValidFullPhoneNumber(newNumber)) return newNumber;
        throw new InvalidPhoneNumberException("Malformed phone number");
    }
}
