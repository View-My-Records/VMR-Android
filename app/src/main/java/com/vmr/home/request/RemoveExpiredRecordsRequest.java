package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.network.NetworkRequest;
import com.vmr.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 9/1/16.
 */
public class RemoveExpiredRecordsRequest extends NetworkRequest<String> {

    Map<String, String> formData =  new HashMap<>();

    public RemoveExpiredRecordsRequest(
            Map<String, String> formData,
            Response.Listener<String> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.SHARE_RECORDS, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01" );
        headers.put("Accept-Encoding", "gzip, deflate" );
        headers.put("Accept-Language", "en-US,en;q=0.8" );
        headers.put("Cookie", "JSESSIONID=" + VMR.getLoggedInUserInfo().getHttpSessionId());
        headers.put("DNT", "1" );
        headers.put("Origin", "http://vmrdev.cloudapp.net:8080" );
        headers.put("Referer", "http://vmrdev.cloudapp.net:8080/vmr/main.do" );
        headers.put("X-Requested-With", "XMLHttpRequest" );
        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + headers.toString());
        return headers;
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