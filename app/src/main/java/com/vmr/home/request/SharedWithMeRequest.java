package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.model.folder_structure.VmrTrashItem;
import com.vmr.network.NetworkRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/25/16.
 */
public class SharedWithMeRequest extends NetworkRequest<List<VmrTrashItem>> {

    private Map<String, String> formData;

    public SharedWithMeRequest (
            Map<String, String> formData,
            Response.Listener<List<VmrTrashItem>> successListener,
            Response.ErrorListener errorListener ) {
        super(Method.POST, Constants.Url.SHARE_RECORDS, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01" );
        headers.put("Accept-Encoding", "gzip, deflate" );
        headers.put("Accept-Language", "en-US,en;q=0.8" );
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" );
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
        return this.formData;
    }

    @Override
    protected Response<List<VmrTrashItem>> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        List<VmrTrashItem> vmrTrashItems;

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            vmrTrashItems = VmrTrashItem.parseTrashItems(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(vmrTrashItems, getCacheEntry());
    }
}
