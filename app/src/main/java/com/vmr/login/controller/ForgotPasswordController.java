package com.vmr.login.controller;

/*
 * Created by abhijit on 11/29/16.
 */

import com.android.volley.VolleyError;

import org.json.JSONObject;

public class ForgotPasswordController {

    public interface OnForgotPasswordClick {
        void onOnForgotPasswordSuccess(JSONObject jsonObject);
        void onOnForgotPasswordFailure(VolleyError error);
    }

}
