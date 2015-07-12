package co.gounplugged.unpluggeddroid.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.db.MaskDatabaseAccess;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.MaskUtil;
import co.gounplugged.unpluggeddroid.utils.PhoneNumberParser;

/**
 *
 */
public class SecondLine {
    private List<Mask> mKnownMasks;
    private Map<Contact, Krewe> mEstablishedKrewes;
    private Map<Mask, Mask> mKreweResponsibilities;
    private final Context mContext;
    private final APICaller mApiCaller;

    public SecondLine(Context context) {
        this.mContext = context;
        this.mApiCaller = new APICaller(context);
        this.mKnownMasks = new ArrayList<>();
        this.mEstablishedKrewes = new HashMap<>();
        this.mKreweResponsibilities = new HashMap<>();
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

    public boolean hasKreweResponsibility(String sentFromMaskAddress) {
        try {
            String countryCode = PhoneNumberParser.parseCountryCode(sentFromMaskAddress);

            Mask sentFromMask = (new MaskDatabaseAccess(mContext)).getMask();

            return mKreweResponsibilities.containsKey(sentFrom);
        } catch (InvalidPhoneNumberException e) {
            e.printStackTrace();
        }

    }



    public class SecondLineException extends Exception {
        public SecondLineException(String message) {
            super(message);
        }
    }
}
