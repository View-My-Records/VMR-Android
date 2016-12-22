package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/25/16.
 */
public class MessageRequest extends PostLoginRequest<JSONObject> {

    private Map<String, String> formData;

    public MessageRequest(
            Map<String, String> formData,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String responseString = new String(response.data);
        JSONObject jsonObject;
        try {
            JSONArray jsonArray = new JSONArray(responseString);
            jsonObject = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(jsonObject, getCacheEntry());
    }
}
