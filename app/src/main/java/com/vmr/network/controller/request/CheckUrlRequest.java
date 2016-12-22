package com.vmr.network.controller.request;


import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.network.PreLoginRequest;

/*
 * Created by abhijit on 8/17/16.
 */

public class CheckUrlRequest extends PreLoginRequest<Integer> {

    public CheckUrlRequest(
            String url,
            Response.Listener<Integer> successListener,
            Response.ErrorListener errorListener) {
        super(Method.GET, url, successListener, errorListener);
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
        Integer statusCode = response.statusCode;
        return Response.success(statusCode, getCacheEntry());
    }

}
