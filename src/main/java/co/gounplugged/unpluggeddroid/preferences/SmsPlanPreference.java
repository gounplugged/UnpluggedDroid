package co.gounplugged.unpluggeddroid.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.util.HashMap;

import co.gounplugged.unpluggeddroid.models.Profile;

public class SmsPlanPreference extends ListPreference {

    HashMap<CharSequence, Integer> mSmsPlanIdLookupMap;

    public SmsPlanPreference(Context context) {
        super(context);
        init(context);
    }

    public SmsPlanPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        initLookupMap();

        final int id = Profile.getSmsPlan();
        setSummaryForEntryId(id);
    }

    private void initLookupMap() {
        mSmsPlanIdLookupMap = new HashMap<>(getEntries().length);
        CharSequence entries[] = getEntries();
        int i = 0;
        for (CharSequence entry : entries) {
            mSmsPlanIdLookupMap.put(entry, i++);
        }
    }

    private void setSummaryForEntryId(int entryId) {
        CharSequence entries[] = getEntries();
        setSummary(entries[entryId]);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            final int smsPlanId = mSmsPlanIdLookupMap.get(getEntry());
            Profile.setSmsPlan(smsPlanId);
            setSummaryForEntryId(smsPlanId);
        }
    }
}
