package co.gounplugged.unpluggeddroid.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pili on 10/04/15.
 */
public class ThrowParser {
    public final static String MASK_SEPARATOR = "zQpQQ";
    public final static String MESSAGE_SEPARATOR = "WIxff";
    public final static String ORIGINATOR_SEPARATOR = "YzLqQ";

    private final static String THROW_REGEX = "(.*" + MESSAGE_SEPARATOR + ")(" +
    PhoneNumberParser.PHONE_NUMBER_REGEX + ORIGINATOR_SEPARATOR + ")";

    public static String getNextMask(String content) {
        return content.split(MASK_SEPARATOR)[0];
    }

    public static boolean isValidRelayThrow(String content){
        return content.matches(
                "(" + PhoneNumberParser.PHONE_NUMBER_REGEX + MASK_SEPARATOR +
                 ")+" + THROW_REGEX);
    }

    public static boolean isValidThrow(String content){
        return content.matches(
                THROW_REGEX
        );
    }

    public static String contentFor(String message, String originatorNumber, Krewe krewe) {
        StringBuilder stringBuilder = new StringBuilder();

        for(Mask m : krewe.getMasks()) {
            stringBuilder.append(m.getFullNumber());
            stringBuilder.append(ThrowParser.MASK_SEPARATOR);
        }

        stringBuilder.append(krewe.getRecipient().getFullNumber());
        stringBuilder.append(ThrowParser.MASK_SEPARATOR);

        stringBuilder.append(message);
        stringBuilder.append(ThrowParser.MESSAGE_SEPARATOR);

        stringBuilder.append(originatorNumber);
        stringBuilder.append(ThrowParser.ORIGINATOR_SEPARATOR);

        return  stringBuilder.toString();
    }

    public static String removeNextMask(String content) {
        return content.replaceFirst(PhoneNumberParser.PHONE_NUMBER_REGEX + MASK_SEPARATOR, "");
    }

    public static String getOriginatorNumber(String content) {
        Matcher m = Pattern.compile("(.*)(" + MESSAGE_SEPARATOR +")(" + PhoneNumberParser.PHONE_NUMBER_REGEX +")(.*)").matcher(content);
        m.matches();
        return m.group(3);
    }

    public static String getMessage(String content) {
        Matcher m = Pattern.compile("(.*)(" + MESSAGE_SEPARATOR +")(.*)").matcher(content);
        m.matches();
        return m.group(1);
    }
}
