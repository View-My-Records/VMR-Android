package com.vmr.login.request;


import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.network.PreLoginRequest;
import com.vmr.utils.VmrURL;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by abhijit on 8/17/16.
 */

public class ForgotPasswordRequest extends PreLoginRequest<JSONObject> {

    public ForgotPasswordRequest(
            String url,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getAccountSetupUrl(), successListener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new VolleyError("Invalid Username or Email"));
        }
        return Response.success(jsonObject, getCacheEntry());
    }

}
