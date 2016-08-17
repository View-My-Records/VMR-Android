package com.vmr.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.vmr.app.VMR;
import com.vmr.utils.Constants;

/**
 * Created by abhijit on 8/16/16.
 */

public class VolleySingleton {

    private static VolleySingleton volleySingletonInstance;
    private RequestQueue requestQueue;

    private VolleySingleton() {
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance() {
        if (volleySingletonInstance == null) {
            volleySingletonInstance = new VolleySingleton();
        }
        return volleySingletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(VMR.getVMRContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelAllPending() {
        getRequestQueue().cancelAll(Constants.LOGIN_REQUEST);
    }

    public void cancelPendingRequest(String tag) {
        getRequestQueue().cancelAll(tag);
    }








}
