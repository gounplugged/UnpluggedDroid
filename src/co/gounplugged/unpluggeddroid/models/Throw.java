package co.gounplugged.unpluggeddroid.models;

/**
 * Created by pili on 20/03/15.
 */
public class Throw {
    private final static String MASK_SEPERATOR = "@#&";
    private final static String RECIPIENT_SEPERATOR = "!)$";
    private final static String MESSAGE_SEPERATOR = "*+#";

    private String content;

    public Throw(String message, Krewe krewe, Contact recipient) {
        this.content = contentFor(message,krewe, recipient);
    }

    private String contentFor(String message, Krewe krewe, Contact recipient) {
        StringBuilder stringBuilder = new StringBuilder();

        for(Mask m : krewe.getMasks()) {
            stringBuilder.append(m.getPhoneNumber());
            stringBuilder.append(MASK_SEPERATOR);
        }

        stringBuilder.append(recipient.getPhoneNumber());
        stringBuilder.append(RECIPIENT_SEPERATOR);

        stringBuilder.append(message);
        stringBuilder.append(MESSAGE_SEPERATOR);

        return  stringBuilder.toString();
    }

    public String getContent() {
        return content;
    }
}
