package com.vmr.login.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/*
 * Created by abhijit on 8/16/16.
 */

public interface LoginRequestInterface {

    void fetchIndividualDetailFailure(VolleyError error);
    void fetchIndividualDetailSuccess(JSONObject jsonObject);

    void fetchFamilyDetailFailure(VolleyError error);
    void fetchFamilyDetailSuccess(JSONObject jsonObject);

    void fetchProfessionalDetailFailure(VolleyError error);
    void fetchProfessionalDetailSuccess(JSONObject jsonObject);

    void fetchCorporateDetailFailure(VolleyError error);
    void fetchCorporateDetailSuccess(JSONObject jsonObject);

}
