package com.vmr.network;

import com.android.volley.Request;
import com.android.volley.Response;

/*
 * Created by abhijit on 8/16/16.
 */

public abstract class PreLoginRequest<T> extends Request<T> {

    private final Response.Listener<T> successListener;

    public PreLoginRequest(int method , String url, Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.successListener = successListener;
    }

    @Override
    protected void deliverResponse(T response) {
        successListener.onResponse(response);
    }

}
