package co.gounplugged.unpluggeddroid.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by pili on 20/03/15.
 */
public class Krewe {
    /*
        Sequential list of all masks
     */
    private final List<Mask> mMasks;
    private static final int NUMBER_MASKS_IN_KREWE = 3;
    private final Contact mRecipient;

    public Krewe(Contact recipient, List<Mask> knownMasks) throws KreweException {
        if(knownMasks.size() < NUMBER_MASKS_IN_KREWE) throw new KreweException("Need more masks to choose from");
        List<Mask> masksBuilder = new ArrayList<>();
        Collections.shuffle(knownMasks);

        for(int i = 0; i < NUMBER_MASKS_IN_KREWE; i++) {
            masksBuilder.add(knownMasks.get(i));
        }

        this.mMasks = masksBuilder;
        this.mRecipient = recipient;
    }

    public List<Throw> getBuilderThrows(OpenPGPBridgeService openPGPBridgeService) {
        List<Throw> builderThrows = new ArrayList<>();
        Mask adjacentMask = getAdjacentMask();

        for(int i = 1; i < mMasks.size(); i++) {
            Mask nextMask = mMasks.get(i);
            Mask secondToNextMask = mMasks.get(i - 1);
            builderThrows.add(
                    new BuilderThrow(
                            nextMask.getFullNumber(),
                            secondToNextMask.getFullNumber(),
                            adjacentMask,
                            openPGPBridgeService));
        }

        builderThrows.add(
                new BuilderThrow(
                        mRecipient.getFullNumber(),
                        getLastMask().getFullNumber(),
                        adjacentMask,
                        openPGPBridgeService));

        builderThrows.add(
                new TerminatingBuilderThrow(
                        Profile.getPhoneNumber(),
                        getRecipientNumber(),
                        adjacentMask,
                        openPGPBridgeService));

        return builderThrows;
    }

    public List<Mask> getMasks() {
        return mMasks;
    }

    public Mask getAdjacentMask() {
        return mMasks.get(0);
    }

    public Mask getLastMask() {
        return mMasks.get(mMasks.size()-1);
    }

    public Contact getRecipient() {
        return mRecipient;
    }

    public String getRecipientNumber() {
        return getRecipient().getFullNumber();
    }

    public class KreweException extends Exception {
        public KreweException(String message) {
            super(message);
        }
    }
}
