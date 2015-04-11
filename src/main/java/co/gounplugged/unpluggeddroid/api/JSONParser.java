package co.gounplugged.unpluggeddroid.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.gounplugged.unpluggeddroid.exceptions.InvalidPhoneNumberException;
import co.gounplugged.unpluggeddroid.models.Krewe;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by pili on 29/03/15.
 */
public class JSONParser {
    private final static String TAG = "JSONParser";

    public static Krewe getKrewe(String serverResponse, String filterByCountryCode) {
        Krewe krewe = new Krewe();
        try {
            JSONArray jArray = new JSONArray(serverResponse);
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String countryCode = oneObject.getString("country_code");
                    Log.d(TAG, countryCode);

                    if(countryCode.equals(filterByCountryCode)) {
                        String phoneNumber = oneObject.getString("number");
                        Log.d(TAG, phoneNumber);
                        try {
                            krewe.addMask(new Mask(countryCode + phoneNumber));
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
        return krewe;
    }
}
