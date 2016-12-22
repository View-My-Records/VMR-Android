package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.model.Account;
import com.vmr.network.PostLoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 11/29/16.
 */

public class AccountDetailsRequest extends PostLoginRequest<Account> {

    private Map<String, String> formData;

    public AccountDetailsRequest(Map<String, String> formData,
                                 Response.Listener<Account> successListener,
                                 Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getAccountSetupUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<Account> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        JSONObject jsonObject;
        Account account;
        try {
            jsonObject = new JSONObject(jsonString);
            account = Account.parseJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new VolleyError("Failed to process request"));
        }
        return Response.success(account, getCacheEntry());
    }
}
