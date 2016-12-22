package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.SearchResult;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class SearchRequest extends PostLoginRequest<List<SearchResult>> {

    private Map<String, String> formData;

    public SearchRequest(
            Map<String, String> formData,
            Response.Listener<List<SearchResult>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<List<SearchResult>> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        List<SearchResult> results;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            results = SearchResult.getResultsList(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(results, getCacheEntry());
    }
}
