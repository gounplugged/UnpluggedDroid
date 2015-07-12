package co.gounplugged.unpluggeddroid.models;

import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 * Created by Marvin Arnold on 11/07/15.
 */
public class TerminatingBuilderThrow extends Throw {
    public final static String TERMINATING_BUILDER_THROW_IDENTIFIER = "XZyQy";

    /**
     * The terminating throw contains the originator's phone number encrypted with the key
     * of the terminating recipient.
     * @param terminatingPhoneNumber
     * @param originatorPhoneNumber
     * @param adjacentMask
     * @param openPGPBridgeService
     */
    public TerminatingBuilderThrow(
            String originatorPhoneNumber,
            String terminatingPhoneNumber,
            Mask adjacentMask,
            OpenPGPBridgeService openPGPBridgeService) {

        super(TERMINATING_BUILDER_THROW_IDENTIFIER + originatorPhoneNumber, adjacentMask);
    }
}
