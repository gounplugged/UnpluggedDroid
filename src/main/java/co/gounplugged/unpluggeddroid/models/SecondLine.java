package co.gounplugged.unpluggeddroid.models;

import java.util.List;

import co.gounplugged.unpluggeddroid.activities.ChatActivity;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;

/**
 *
 */
public class SecondLine {
    private Krewe selectedKrewe;

    public SecondLine(Contact ultimateRecipient, List<Mask> knownMasks) {
        this.selectedKrewe = new Krewe(ultimateRecipient, knownMasks);
    }

    public Throw getThrow(
            String message,
            String fromPhone,
            OpenPGPBridgeService openPGPBridgeService)
            throws OpenPGPBridgeService.EncryptionUnavailableException {

        return new Throw(message, fromPhone, selectedKrewe, openPGPBridgeService);
    }

}
