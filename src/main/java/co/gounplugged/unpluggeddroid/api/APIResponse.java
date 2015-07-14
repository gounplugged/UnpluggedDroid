package co.gounplugged.unpluggeddroid.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by pili on 29/03/15.
 */
public class APIResponse {
    private final static String TAG = "APIResponse";

    /**
     * Returns all the mask from server.
     * @param serverResponseString
     * @param filterByCountryCode
     * @return
     */
    public static List<Mask> getMasks(String serverResponseString, String filterByCountryCode) {
        if (filterByCountryCode == null) return null;
        List<Mask> masks = new ArrayList();
        try {
            JSONArray jArray = new JSONArray(serverResponseString);
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String countryCode = oneObject.getString("country_code");
                    Log.d(TAG, countryCode);

                    if(filterByCountryCode.equals("") || countryCode.equals(filterByCountryCode)) {
                        String phoneNumber = oneObject.getString("number");
                        Log.d(TAG, phoneNumber);
                        try {
                            masks.add(new Mask(countryCode + phoneNumber));
                        } catch (InvalidPhoneNumberException e) {
                            // Server should not accept invalid numbers to start with
                        }
                    }
                } catch (JSONException e) {
                    // Oops
                }
            }
        } catch (JSONException e) {

        }
        return masks;
    }
}
