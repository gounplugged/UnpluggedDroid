package co.gounplugged.unpluggeddroid.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Mask;

/**
 * Created by pili on 5/04/15.
 */
public class APICaller {
    private final static String TAG = "APICaller";
    private RequestQueue queue;
    private String url ="https://stormy-hamlet-7282.herokuapp.com/masks";
    private Context mContext;

    public APICaller(Context context) {
        queue = Volley.newRequestQueue(context);
        mContext = context;
    }

    public void getMasks(final String filterByCountryCode) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        List<Mask> receivedMasks = APIResponse.getMasks(response, filterByCountryCode);
                        Log.d(TAG, "received " + receivedMasks.size());
                        ((BaseApplication)mContext).receiveMasks(receivedMasks);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Call didn't work");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
