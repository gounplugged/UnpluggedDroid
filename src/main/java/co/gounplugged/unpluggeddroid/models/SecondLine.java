package co.gounplugged.unpluggeddroid.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;

/**
 *
 */
public class SecondLine {
    private List<Mask> mKnownMasks;
    private Map<Contact, Krewe> mEstablishedKrewes;
    private Map<Mask, Mask> mKreweResponsibilities;
    private Map<Mask, Contact> mKreweUnderstandings;
    private final Context mContext;
    private final APICaller mApiCaller;

    public SecondLine(Context context) {
        this.mContext = context;
        this.mApiCaller = new APICaller(context);
        this.mKnownMasks = new ArrayList<>();
        this.mEstablishedKrewes = new HashMap<>();
        this.mKreweResponsibilities = new HashMap<>();
        this.mKreweUnderstandings = new HashMap<>();

        seedKnownMasks();
    }

    /**
     *
     * @param recipient
     * @return the Throws that need to be sent over the wire inform the entire Krewe.
     * @throws Krewe.KreweException
     */
    public List<Throw> establishKrewe(Contact recipient, OpenPGPBridgeService openPGPBridgeService)
            throws Krewe.KreweException, EncryptionUnavailableException {

        Krewe establishedKrewe = new Krewe(recipient, mKnownMasks);
        mEstablishedKrewes.put(recipient, establishedKrewe);

        return establishedKrewe.getBuilderThrows(openPGPBridgeService);
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

    public void refreshKnownMasks() {
        mKnownMasks = null;
        seedKnownMasks();
    }

    public void seedKnownMasks() {
        if(mKnownMasks == null) mKnownMasks = MaskUtil.getCachedMasks(mContext);
        if(mKnownMasks.isEmpty()) mApiCaller.getMasks(Profile.getCountryCodeFilter());
    }

    public void setKnownMasks(List<Mask> knownMasks) {
        this.mKnownMasks = knownMasks;
    }

    public void addResponsibility(Mask sentFrom, Mask sendTo) {
        mKreweResponsibilities.put(sentFrom, sendTo);
    }

    public void addUnderstanding(Mask sentFrom, Contact trueOriginator) {
        mKreweUnderstandings.put(sentFrom, trueOriginator);
    }

    public Contact getKreweUnderstanding(Mask sentFromMask) throws SecondLineException {
        Contact trueOriginator = mKreweUnderstandings.get(sentFromMask);
        if(sentFromMask == null || trueOriginator == null) throw new SecondLineException("No existing Understanding found");
        return trueOriginator;
    }

    public Mask getKreweResponsibility(String sentFromMaskAddress) throws SecondLineException {
        try {
            Mask sentFromMask = MaskUtil.getMask(mContext, sentFromMaskAddress);

            Mask sendToMask = mKreweResponsibilities.get(sentFromMask);
            if(sentFromMask == null || sendToMask == null) throw new SecondLineException("No existing Responsibility found");
            return sendToMask;
        } catch (InvalidPhoneNumberException e) {
            throw new SecondLineException("Invalid number, cannot have a Responsibility");
        }
    }

    public class SecondLineException extends Exception {
        public SecondLineException(String message) {
            super(message);
        }
    }
}
