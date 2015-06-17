package co.gounplugged.unpluggeddroid.models;

import java.util.List;
import co.gounplugged.unpluggeddroid.exceptions.EncryptionUnavailableException;
import co.gounplugged.unpluggeddroid.services.OpenPGPBridgeService;
import co.gounplugged.unpluggeddroid.utils.ThrowParser;

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
            throws EncryptionUnavailableException,
            ThrowParser.KreweException {

        return new Throw(message, fromPhone, selectedKrewe, openPGPBridgeService);
    }
}
