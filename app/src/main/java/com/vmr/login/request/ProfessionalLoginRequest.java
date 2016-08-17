package com.vmr.login.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.vmr.network.JSONNetworkRequest;
import com.vmr.utils.Constants;
import com.vmr.utils.WebApiConstants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class ProfessionalLoginRequest extends JSONNetworkRequest {

    public String email ;
    public String password ;
    public String domain ;
    public String accType ;

    public ProfessionalLoginRequest(
            String email,
            String password,
            String accType,
            String domain,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super( Constants.Url.LOGIN_URL, successListener, errorListener);
        this.email  = email   ;
        this.password = password  ;
        this.accType = accType  ;
        this.domain = domain  ;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        Map<String,String> formData = new HashMap<>(4) ;
        try {
            formData.put(URLEncoder.encode(WebApiConstants.JSON_LOGIN_PROFESSIONAL_EMAIL_ID, "UTF-8"), URLEncoder.encode(email, "UTF-8") );
            formData.put(URLEncoder.encode(WebApiConstants.JSON_LOGIN_PROFESSIONAL_PASSWORD, "UTF-8"), URLEncoder.encode(password, "UTF-8") );
            formData.put(URLEncoder.encode(WebApiConstants.JSON_LOGIN_PROFESSIONAL_NAME, "UTF-8"), URLEncoder.encode(accType, "UTF-8") );
            formData.put(URLEncoder.encode(WebApiConstants.JSON_LOGIN_DOMAIN, "UTF-8"), URLEncoder.encode(domain, "UTF-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return formData;
    }
}
