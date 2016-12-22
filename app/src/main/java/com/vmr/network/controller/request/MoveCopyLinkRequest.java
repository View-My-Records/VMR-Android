package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.debug.VmrDebug;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class MoveCopyLinkRequest extends PostLoginRequest<JSONObject> {

    private boolean DEBUG = false;

    private Map<String, String> formData;

    public MoveCopyLinkRequest(
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
        String jsonString = new String(response.data);

        if(DEBUG) VmrDebug.printLogI(this.getClass(), jsonString);

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(jsonObject, getCacheEntry());
    }
}
