package com.vmr.network.controller.request;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.network.PreLoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/17/16.
 */

public class ForgotPasswordRequest extends PreLoginRequest<JSONObject> {

    private Map<String, String> formData;

    public ForgotPasswordRequest(
            Map<String, String> formData,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getForgotPasswordUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
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
