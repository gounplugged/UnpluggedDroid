package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.models.Profile;

/**
 * Created by Marvin Arnold on 11/06/15.
 */
public class ProfileTest extends AndroidTestCase {
    /**
     * Should return empty string if no number set, or country code otherwise.
     */
    public void testGetCountryCodeFilter() {
        Profile.setPhoneNumber(null);
        assertEquals("", Profile.getCountryCodeFilter());

        Profile.setPhoneNumber("+13016864576");
        assertEquals("+1", Profile.getCountryCodeFilter());
    }
}
