package co.gounplugged.unpluggeddroid.test.api;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.api.APIResponse;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by Marvin Arnold on 13/07/15.
 */
public class APIResponseTest extends AndroidTestCase {
    Mask marvin;
    Mask tim;

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        marvin = new Mask("+13016864576");
        tim = new Mask("+32475932921");
    }

    public void testGetMasks() {
        String response = "[{\"number\":\"3016864576\",\"country_code\":\"+1\"},{\"number\":\"3016864576\",\"country_code\":\"+1\"},{\"number\":\"3016864576\",\"country_code\":\"+1\"},{\"number\":\"475932921\",\"country_code\":\"+32\"},{\"number\":\"475932921\",\"country_code\":\"+32\"},{\"number\":\"475932921\",\"country_code\":\"+32\"}]";
        List<Mask> expectedResponse = new ArrayList();
        expectedResponse.add(marvin);
        expectedResponse.add(marvin);
        expectedResponse.add(marvin);
        expectedResponse.add(tim);
        expectedResponse.add(tim);
        expectedResponse.add(tim);

        List<Mask> actualResponse = APIResponse.getMasks(response, "");

        for(int i = 0; i < expectedResponse.size(); i++) {
            String expectedMaskNumber = expectedResponse.get(i).getFullNumber();
            String actualMaskNumber = actualResponse.get(i).getFullNumber();
//            Log.d("APIResponseTest", expectedMaskNumber + " compared to " + actualMaskNumber);
//            assertEquals(expectedMaskNumber, actualMaskNumber);

            assertEquals(expectedResponse.get(i), actualResponse.get(i));
        }

        // Don't get anything if null filter
        assertNull(APIResponse.getMasks(response, null));

    }
}
