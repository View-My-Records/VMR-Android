package com.vmr.login.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.model.UserInfo;
import com.vmr.network.JSONNetworkRequest;
import com.vmr.network.NetworkRequest;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;
import com.vmr.utils.WebApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/19/16.
 */

public class LoginRequest extends NetworkRequest<UserInfo> {

    private Map<String, String> formData;

    public LoginRequest(
            Map<String, String> formData,
            Response.Listener<UserInfo> successListener,
            Response.ErrorListener errorListener) {
        super( Method.POST,Constants.Url.LOGIN, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<UserInfo> parseNetworkResponse(NetworkResponse response) {
        VmrDebug.printLogD(VMR.getVMRContext(), response.headers.toString());
        String jsonString = new String(response.data);
        VmrDebug.printLogD(VMR.getVMRContext(), jsonString);
        JSONObject jsonObject;
        UserInfo userInfo;
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
