package com.vmr.login.request;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.vmr.network.JSONNetworkRequest;
import com.vmr.utils.Constants;
import com.vmr.utils.WebApiConstants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class IndividualLoginRequest extends JSONNetworkRequest  {

    public String email;
    public String password;
    public String domain;

    public IndividualLoginRequest(String email, String password, String domain, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        super(Constants.Url.LOGIN_URL, successListener, errorListener);
        this.email  = email;
        this.password = password;
        this.domain = domain;

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        Map<String,String> formData = new HashMap<>(3) ;

        formData.put(Uri.encode(WebApiConstants.JSON_LOGIN_INDIVIDUAL_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(WebApiConstants.JSON_LOGIN_INDIVIDUAL_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(WebApiConstants.JSON_LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        return formData;
    }

}
