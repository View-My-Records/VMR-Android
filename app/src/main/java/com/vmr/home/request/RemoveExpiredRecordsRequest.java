package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.debug.VmrDebug;
import com.vmr.network.PostLoginRequest;
import com.vmr.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 9/1/16.
 */
public class RemoveExpiredRecordsRequest extends PostLoginRequest<String> {

    Map<String, String> formData =  new HashMap<>();

    public RemoveExpiredRecordsRequest(
            Map<String, String> formData,
            Response.Listener<String> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.SHARE_RECORDS, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        VmrDebug.printLogI(this.getClass(), this.formData.toString());
        return this.formData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String string = new String(response.data);
        VmrDebug.printLogI(this.getClass(), string);
        return Response.success(string, getCacheEntry());
    }
}