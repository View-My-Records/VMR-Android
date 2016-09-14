package com.vmr.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.vmr.app.Vmr;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public abstract class PostLoginRequest<T> extends Request<T> {

    private final Response.Listener<T> successListener;

    public PostLoginRequest(int method , String url, Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.successListener = successListener;
    }

    @Override
    protected void deliverResponse(T response) {
        successListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01" );
        headers.put("Accept-Encoding", "gzip, deflate" );
        headers.put("Accept-Language", "en-US,en;q=0.8" );
        headers.put("Cookie", "JSESSIONID=" + Vmr.getLoggedInUserInfo().getHttpSessionId());
        headers.put("DNT", "1" );
        headers.put("Origin", "http://vmrdev.cloudapp.net:8080" );
        headers.put("Referer", "http://vmrdev.cloudapp.net:8080/vmr/main.do" );
        headers.put("X-Requested-With", "XMLHttpRequest" );
//        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + headers.toString());
        return headers;
    }
}
