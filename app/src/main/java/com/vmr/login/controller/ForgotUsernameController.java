package com.vmr.login.controller;

/*
 * Created by abhijit on 11/29/16.
 */

import com.android.volley.VolleyError;

import org.json.JSONObject;

public class ForgotUsernameController {
    public interface OnForgotUsernameClick {
        void onOnForgotUsernameSuccess(JSONObject jsonObject);
        void onOnForgotUsernameFailure(VolleyError error);
    }
}
