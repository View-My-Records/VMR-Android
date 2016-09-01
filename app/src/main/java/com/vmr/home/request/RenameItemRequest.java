package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.network.NetworkRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class RenameItemRequest extends NetworkRequest<JSONObject> {

    private Map<String, String> formData;

    public RenameItemRequest(
            Map<String, String> formData,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.FOLDER_NAVIGATION, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01" );
        headers.put("Accept-Encoding", "gzip, deflate" );
        headers.put("Accept-Language", "en-US,en;q=0.8" );
        headers.put("Cookie", "JSESSIONID=" + VMR.getUserInfo().getHttpSessionId());
        headers.put("DNT", "1" );
        headers.put("Origin", "http://vmrdev.cloudapp.net:8080" );
        headers.put("Referer", "http://vmrdev.cloudapp.net:8080/vmr/main.do" );
        headers.put("X-Requested-With", "XMLHttpRequest" );
        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + headers.toString());
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
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
