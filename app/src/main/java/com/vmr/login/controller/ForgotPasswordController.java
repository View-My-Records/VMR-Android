package com.vmr.login.controller;

/*
 * Created by abhijit on 11/29/16.
 */

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.login.controller.request.ForgotUsernameRequest;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordController {

    private OnForgotPasswordListener listener;

    public ForgotPasswordController(OnForgotPasswordListener listener) {
        this.listener = listener;
    }

    public void sendRequest(String domain, String accountType, String username, String email, String accountId){

        Map<String, String> formData = new HashMap<>();
        formData.put(Constants.Request.ForgotPassword.URL_TYPE, PrefUtils.getSharedPreference(PrefConstants.URL_TYPE));
        formData.put(Constants.Request.ForgotPassword.URL_TYPE1, PrefUtils.getSharedPreference(PrefConstants.URL_TYPE));
        formData.put(Constants.Request.ForgotPassword.URL_TYPE2, PrefUtils.getSharedPreference(PrefConstants.URL_TYPE));
        formData.put(Constants.Request.ForgotPassword.ACCOUNT_SELECTED, accountType);
        if(!domain.equals(Constants.Request.Login.Domain.INDIVIDUAL))
            formData.put(Constants.Request.ForgotPassword.CORPORATE_ID, accountId );
        formData.put(Constants.Request.ForgotPassword.DOMAIN, domain);
        formData.put(Constants.Request.ForgotPassword.USERNAME, username);
        formData.put(Constants.Request.ForgotPassword.EMAIL, email);

        ForgotUsernameRequest request
                = new ForgotUsernameRequest(
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onOnForgotPasswordSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onOnForgotPasswordFailure(error);
                    }
                }
        );

        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.ForgotPassword.TAG);

    }

    public interface OnForgotPasswordListener {
        void onOnForgotPasswordSuccess(JSONObject response);
        void onOnForgotPasswordFailure(VolleyError error);
    }
}
