package com.vmr.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by abhijit on 8/16/16.
 */

public abstract class JSONNetworkRequest  extends NetworkRequest<JSONObject>{

    public JSONNetworkRequest( String url, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        super(Method.POST, url, successListener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        byte[] responseBytes = response.data;
        String responseString = new String(responseBytes);
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(responseString);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }

        return Response.success(jsonObject, getCacheEntry());
    }
}
