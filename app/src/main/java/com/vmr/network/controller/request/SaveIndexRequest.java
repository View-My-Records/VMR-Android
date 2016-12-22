package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.debug.VmrDebug;
import com.vmr.network.PostLoginRequest;

import java.util.Map;

/*
 * Created by abhijit on 9/21/16.
 */

public class SaveIndexRequest extends PostLoginRequest<String> {

    private Map<String, String> formData;

    public SaveIndexRequest(
            Map<String, String> formData,
            Response.Listener<String> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
//        VmrDebug.printLogI(this.getClass(), formData.toString());
        return this.formData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
//        JSONObject jsonObject;
//        try {
////            jsonObject = new JSONObject(jsonString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return Response.error(new FetchError());
//        }
        VmrDebug.printLogI(this.getClass(), jsonString);

        return Response.success(jsonString, getCacheEntry());
    }
}
