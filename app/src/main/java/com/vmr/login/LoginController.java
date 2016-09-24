package com.vmr.login;

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.login.request.AlfrescoTicketRequest;
import com.vmr.login.request.CheckUrlRequest;
import com.vmr.login.request.LoginRequest;
import com.vmr.model.UserInfo;
import com.vmr.network.VmrRequestQueue;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class LoginController {

    private VmrResponseListener.OnLoginListener onLoginListener;
    private VmrResponseListener.OnFetchTicketListener onFetchTicketListener;
    private VmrResponseListener.OnCheckUrlResponse onCheckUrlResponse;

    public LoginController(VmrResponseListener.OnLoginListener onLoginListener, VmrResponseListener.OnFetchTicketListener onFetchTicketListener) {
        this.onLoginListener = onLoginListener;
        this.onFetchTicketListener = onFetchTicketListener;
    }

    public LoginController(VmrResponseListener.OnCheckUrlResponse onCheckUrlResponse){
        this.onCheckUrlResponse = onCheckUrlResponse;
    }

    void fetchIndividualDetail(String email, String password, String domain){
        final Map<String,String> formData = new HashMap<>(3) ;

        formData.put(Constants.Request.Login.Individual.EMAIL_ID, Uri.encode(email, "UTF-8") );
        formData.put(Constants.Request.Login.Individual.PASSWORD, Uri.encode(password, "UTF-8") );
        formData.put(Constants.Request.Login.DOMAIN, Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);

    }

    void fetchFamilyDetail(String email, String password, String accountId, String domain){

        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Constants.Request.Login.Family.EMAIL_ID, Uri.encode(email, "UTF-8") );
        formData.put(Constants.Request.Login.Family.PASSWORD, Uri.encode(password, "UTF-8") );
        formData.put(Constants.Request.Login.Family.NAME, Uri.encode(accountId, "UTF-8") );
        formData.put(Constants.Request.Login.DOMAIN, Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchProfessionalDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.Request.Login.Professional.EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Professional.PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Professional.NAME, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCorporateDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.Request.Login.Corporate.EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Corporate.PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Corporate.NAME, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCustomIndividualDetail(String email, String password, String domain){

        final Map<String,String> formData = new HashMap<>(3) ;

        formData.put(Uri.encode(Constants.Request.Login.Individual.EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Individual.PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCustomFamilyDetail(String email, String password, String accountId, String domain){

        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.Request.Login.Family.EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Family.PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Family.NAME, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );


        dispatchRequest(formData);
    }

    void fetchCustomProfessionalDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.Request.Login.Professional.EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Professional.PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Professional.NAME, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchCustomCorporateDetail(String email, String password, String accountId, String domain){
        final Map<String,String> formData = new HashMap<>(4) ;

        formData.put(Uri.encode(Constants.Request.Login.Corporate.EMAIL_ID, "UTF-8"), Uri.encode(email, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Corporate.PASSWORD, "UTF-8"), Uri.encode(password, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.Corporate.NAME, "UTF-8"), Uri.encode(accountId, "UTF-8") );
        formData.put(Uri.encode(Constants.Request.Login.DOMAIN, "UTF-8"), Uri.encode(domain, "UTF-8") );

        dispatchRequest(formData);
    }

    void fetchAlfrescoTicket(){
        AlfrescoTicketRequest ticketRequest =
                new AlfrescoTicketRequest(
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                onFetchTicketListener.onFetchTicketSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchTicketListener.onFetchTicketFailure(error);
                            }
                        } );
        VmrRequestQueue.getInstance().addToRequestQueue(ticketRequest, Constants.VMR_LOGIN_REQUEST_TAG);
    }

    private void dispatchRequest(final Map<String, String> formData){
        LoginRequest loginRequest =
                new LoginRequest(
                        formData,
                        new Response.Listener<UserInfo>() {
                            @Override
                            public void onResponse(UserInfo userInfo) {
                                onLoginListener.onLoginSuccess(userInfo);
                                Vmr.setUserMap(formData);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onLoginListener.onLoginFailure(error);
                            }
                        } );
        VmrRequestQueue.getInstance().addToRequestQueue(loginRequest, Constants.VMR_LOGIN_REQUEST_TAG);
    }

    public void checkUrl(String url){

        CheckUrlRequest checkUrlRequest =
                new CheckUrlRequest(
                        url,
                        new Response.Listener<Integer>() {
                            @Override
                            public void onResponse(Integer response) {
                                onCheckUrlResponse.onCheckUrlResponseSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onCheckUrlResponse.onCheckUrlResponseFailure(error);
                            }
                        });

        VmrRequestQueue.getInstance().addToRequestQueue(checkUrlRequest, Constants.VMR_LOGIN_REQUEST_TAG);
    }

}
