package com.vmr.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public abstract class NetworkRequest<T> extends Request<T> {

    private final Response.Listener<T> successListener;

    public NetworkRequest(int method ,String url, Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.successListener = successListener;
    }

    @Override
    protected void deliverResponse(T response) {
        successListener.onResponse(response);
    }

//    @Override
//    public String getBodyContentType() {
//        return "application/x-www-form-urlencoded;charset=UTF-8";
//    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }
}
