package com.vmr.login;

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.vmr.app.VMR;
import com.vmr.login.interfaces.LoginRequestInterface;
import com.vmr.login.request.AlfrescoTicketRequest;
import com.vmr.login.request.LoginRequest;
import com.vmr.model.UserInfo;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class LoginController {

    private LoginRequestInterface loginRequestInterface;

    LoginController(LoginRequestInterface LoginRequestInterface) {
        this.loginRequestInterface = LoginRequestInterface;
    }

    void fetchIndividualDetail(String email, String password, String domain){
        final Map<String,String> formData = new HashMap<>(3) ;

        formData.put(Constants.LoginRequest.INDIVIDUAL_EMAIL_ID, Uri.encode(email, "UTF-8") );
        formData.put(Constants.LoginRequest.INDIVIDUAL_PASSWORD, Uri.encode(password, "UTF-8") );
        formData.put(Constants.LoginRequest.LOGIN_DOMAIN, Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);

    }

    void fetchFamilyDetail(String email, String password, String accountId, String domain){

        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Constants.LoginRequest.FAMILY_EMAIL_ID, Uri.encode(email, "UTF-8") );
        formData.put(Constants.LoginRequest.FAMILY_PASSWORD, Uri.encode(password, "UTF-8") );
        formData.put(Constants.LoginRequest.FAMILY_ID, Uri.encode(accountId, "UTF-8") );
        formData.put(Constants.LoginRequest.LOGIN_DOMAIN, Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchProfessionalDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.LoginRequest.PROFESSIONAL_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.PROFESSIONAL_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.PROFESSIONAL_ID, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCorporateDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.LoginRequest.CORPORATE_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.CORPORATE_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.CORPORATE_ID, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCustomIndividualDetail(String email, String password, String domain){

        final Map<String,String> formData = new HashMap<>(3) ;

        formData.put(Uri.encode(Constants.LoginRequest.INDIVIDUAL_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.INDIVIDUAL_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCustomFamilyDetail(String email, String password, String accountId, String domain){

        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.LoginRequest.FAMILY_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.FAMILY_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.FAMILY_ID, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );


        dispatchRequest(formData);
    }

    void fetchCustomProfessionalDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.LoginRequest.PROFESSIONAL_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.PROFESSIONAL_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.PROFESSIONAL_ID, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCustomCorporateDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.LoginRequest.CORPORATE_EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.CORPORATE_PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.CORPORATE_ID, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.LoginRequest.LOGIN_DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchAlfrescoTicket(){
        AlfrescoTicketRequest ticketRequest =
                new AlfrescoTicketRequest(
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loginRequestInterface.onFetchTicketSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loginRequestInterface.onFetchTicketFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(ticketRequest, Constants.VMR_LOGIN_REQUEST_TAG);
    }

    private void dispatchRequest(final Map<String, String> formData){
        LoginRequest loginRequest =
                new LoginRequest(
                        formData,
                        new Response.Listener<UserInfo>() {
                            @Override
                            public void onResponse(UserInfo userInfo) {
                                loginRequestInterface.onLoginSuccess(userInfo);
                                VMR.setUserMap(formData);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loginRequestInterface.onLoginFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(loginRequest, Constants.VMR_LOGIN_REQUEST_TAG);
    }
}
