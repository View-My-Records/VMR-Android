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

public class ForgotUsernameController {

    private OnForgotUsernameListener listener;

    public ForgotUsernameController(OnForgotUsernameListener listener) {
        this.listener = listener;
    }

    public void sendRequest(String domain, String accountType, String email){

        Map<String, String> formData = new HashMap<>();
        formData.put(Constants.Request.ForgotUsername.DOMAIN, domain);
        formData.put(Constants.Request.ForgotUsername.ACCOUNT_TYPE, accountType);
        formData.put(Constants.Request.ForgotUsername.URL_TYPE, PrefUtils.getSharedPreference(PrefConstants.URL_TYPE));
        formData.put(Constants.Request.ForgotUsername.EMAIL, email);

        ForgotUsernameRequest request
                = new ForgotUsernameRequest(
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onOnForgotUsernameSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onOnForgotUsernameFailure(error);
                    }
                }
        );

        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.ForgotUsername.TAG);

    }

    public interface OnForgotUsernameListener {
        void onOnForgotUsernameSuccess(JSONObject response);
        void onOnForgotUsernameFailure(VolleyError error);
    }
}
