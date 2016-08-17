package com.vmr.login;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.network.VolleySingleton;
import com.vmr.login.interfaces.LoginRequestInterface;
import com.vmr.login.request.CorporateLoginRequest;
import com.vmr.login.request.FamilyLoginRequest;
import com.vmr.login.request.IndividualLoginRequest;
import com.vmr.login.request.ProfessionalLoginRequest;
import com.vmr.utils.Constants;

import org.json.JSONObject;

/*
 * Created by abhijit on 8/16/16.
 */

public class LoginController {
    private LoginRequestInterface LoginRequestInterface;

    public LoginController(LoginRequestInterface LoginRequestInterface) {
        this.LoginRequestInterface = LoginRequestInterface;
    }

    public void fetchIndividualDetail(String email, String password, String domain){
        IndividualLoginRequest loginRequest =
            new IndividualLoginRequest(
                email,
                password,
                domain,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoginRequestInterface.fetchIndividualDetailSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoginRequestInterface.fetchIndividualDetailFailure(error);
                    }
                } );
        VolleySingleton.getInstance().addToRequestQueue(loginRequest, Constants.LOGIN_REQUEST);
    }

    public void fetchFamilyDetail(String email, String password, String name, String domain){
        FamilyLoginRequest loginRequest =
                new FamilyLoginRequest(
                        email,
                        password,
                        name,
                        domain,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LoginRequestInterface.fetchFamilyDetailSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LoginRequestInterface.fetchFamilyDetailFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(loginRequest, Constants.LOGIN_REQUEST);
    }

    public void fetchProfessionalDetail(String email, String password, String name, String domain){
        ProfessionalLoginRequest loginRequest =
                new ProfessionalLoginRequest(
                        email,
                        password,
                        name,
                        domain,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LoginRequestInterface.fetchProfessionalDetailSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LoginRequestInterface.fetchProfessionalDetailFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(loginRequest, Constants.LOGIN_REQUEST);
    }

    public void fetchCorporateDetail(String email, String password, String name, String domain){
        CorporateLoginRequest loginRequest =
                new CorporateLoginRequest(
                        email,
                        password,
                        name,
                        domain,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LoginRequestInterface.fetchCorporateDetailSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LoginRequestInterface.fetchCorporateDetailFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(loginRequest, Constants.LOGIN_REQUEST);
    }
}
