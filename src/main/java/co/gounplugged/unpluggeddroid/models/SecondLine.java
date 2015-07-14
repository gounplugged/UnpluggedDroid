package co.gounplugged.unpluggeddroid.models;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;

/**
 *
 */
public class SecondLine {
    private final static String TAG = "SecondLine";

    private Map<String, Krewe> mEstablishedKrewes;
    private Map<String, Mask> mKreweResponsibilities;
    private Map<String, Contact> mKreweUnderstandings;
    private final Context mContext;

    public SecondLine(Context context) {
        this.mContext = context;
        this.mEstablishedKrewes = new HashMap<>();
        this.mKreweResponsibilities = new HashMap<>();
        this.mKreweUnderstandings = new HashMap<>();
    }

    /**
     *
     * @param recipient
     * @return the Throws that need to be sent over the wire inform the entire Krewe.
     * @throws Krewe.KreweException
     */
    public List<Throw> establishNewKrewe(Contact recipient, OpenPGPBridgeService openPGPBridgeService)
            throws Krewe.KreweException,
            EncryptionUnavailableException {

        Log.d(TAG, "establishNewKrewe: knownMaks " + getKnownMasks().size());
        Krewe establishedKrewe = new Krewe(recipient, getKnownMasks());
        mEstablishedKrewes.put(recipient.getFullNumber(), establishedKrewe);

        return establishedKrewe.getBuilderThrows(openPGPBridgeService);
    }

    private List<Mask> getKnownMasks() {
        // Todo caching
        return MaskUtil.getKnownMasks(mContext);
    }

    public Throw getMessageThrow(String content, Contact recipient, OpenPGPBridgeService openPGPBridgeService)
            throws SecondLineException, EncryptionUnavailableException{

        if (!mEstablishedKrewes.containsKey(recipient)) throw new SecondLineException("Krewe to this recipient not established");
        Krewe establishedKrewe = mEstablishedKrewes.get(recipient);

        return new MessageThrow(
                        content,
                        recipient.getFullNumber(),
                        establishedKrewe.getAdjacentMask(),
                        openPGPBridgeService);
    }


    public void addResponsibility(Mask sentFrom, Mask sendTo) {
        Log.d(TAG, "Added responsibility from: " + sentFrom.getFullNumber() + " to: " + sendTo.getFullNumber());
        mKreweResponsibilities.put(sentFrom.getFullNumber(), sendTo);
    }

    public void addUnderstanding(Mask sentFrom, Contact trueOriginator) {
        Log.d(TAG, "Added understanding from: " + sentFrom.getFullNumber() + " to: " + trueOriginator.getFullNumber());
        mKreweUnderstandings.put(sentFrom.getFullNumber(), trueOriginator);
    }

    public Krewe getEstablishedKrewe(Contact contact) throws SecondLineException {
        Krewe establishedKrewe = mEstablishedKrewes.get(contact);
        if(contact == null || establishedKrewe == null) throw new SecondLineException("No existing Krewe found");
        return establishedKrewe;
    }

    public Contact getKreweUnderstanding(Mask sentFromMask) throws SecondLineException {
        Contact trueOriginator = mKreweUnderstandings.get(sentFromMask.getFullNumber());
        if(trueOriginator == null) throw new SecondLineException("No existing Understanding found");
        return trueOriginator;
    }

    public Mask getKreweResponsibility(String sentFromMaskAddress) throws SecondLineException {
//        try {
//            Mask sentFromMask = MaskUtil.getMask(mContext, sentFromMaskAddress);

            Mask sendToMask = mKreweResponsibilities.get(sentFromMaskAddress);
            if(sendToMask == null) throw new SecondLineException("No existing Responsibility found");
            return sendToMask;
//        } catch (InvalidPhoneNumberException e) {
//            throw new SecondLineException("Invalid number, cannot have a Responsibility");
//        }
    }

    public class SecondLineException extends Exception {
        public SecondLineException(String message) {
            super(message);
        }
    }
}
