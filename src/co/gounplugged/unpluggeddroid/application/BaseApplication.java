package co.gounplugged.unpluggeddroid.application;

import android.app.Application;

import co.gounplugged.unpluggeddroid.api.APICaller;
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Contact;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Serves as global application instance
 */
public class BaseApplication extends Application {

    private APICaller apiCaller;
    private Krewe mKnownMasks;


    /**
     * Get new masks from api or cache on app start
     */
    @Override
    public void onCreate() {
        super.onCreate();

        apiCaller = new APICaller(getApplicationContext());
        seedKnownMasks();

    }

    private void seedKnownMasks() {
        if(mKnownMasks == null){
            mKnownMasks = new Krewe();
        }
        DatabaseAccess<Mask> maskAccess = new DatabaseAccess<>(getApplicationContext(), Mask.class);
        // TODO: Prefill from db

        if(mKnownMasks.isEmpty()) {
            apiCaller.getMasks(Contact.DEFAULT_COUNTRY_CODE);
        }
    }


    public Krewe getKnownMasks() {
        return mKnownMasks;
    }

    public void setKnownMasks(Krewe knownMasks) {
        this.mKnownMasks = knownMasks;
    }
}
