package com.vmr.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.vmr.app.VMR;
import com.vmr.utils.Constants;

/*
 * Created by abhijit on 8/16/16.
 */

public class VmrRequestQueue {

    private static VmrRequestQueue vmrRequestQueue;
    private RequestQueue requestQueue;

    private VmrRequestQueue() {
        requestQueue = getRequestQueue();
    }

    public static synchronized VmrRequestQueue getInstance() {
        if (vmrRequestQueue == null) {
            vmrRequestQueue = new VmrRequestQueue();
        }
        return vmrRequestQueue;
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
        getRequestQueue().cancelAll(Constants.VMR_LOGIN_REQUEST_TAG);
    }

    public void cancelPendingRequest(String tag) {
        getRequestQueue().cancelAll(tag);
    }

}
