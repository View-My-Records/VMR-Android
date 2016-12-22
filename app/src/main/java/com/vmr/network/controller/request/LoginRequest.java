package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.debug.VmrDebug;
import com.vmr.model.UserInfo;
import com.vmr.network.PreLoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/19/16.
 */

public class LoginRequest extends PreLoginRequest<UserInfo> {

    private Map<String, String> formData;

    public LoginRequest(
            Map<String, String> formData,
            Response.Listener<UserInfo> successListener,
            Response.ErrorListener errorListener) {
        super( Method.POST, VmrURL.getLoginUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<UserInfo> parseNetworkResponse(NetworkResponse response) {
//        VmrDebug.printLogD(this.getClass(), response.headers.toString());
        String jsonString = new String(response.data);
//        VmrDebug.printLogD(this.getClass(), jsonString);
        JSONObject jsonObject;
        UserInfo userInfo;
        VmrDebug.printLogI(this.getClass(), jsonString);
        try {
            jsonObject = new JSONObject(jsonString);
            userInfo = new UserInfo( jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new VolleyError("Invalid Username or Password"));
        }
        return Response.success(userInfo, getCacheEntry());
    }
}
