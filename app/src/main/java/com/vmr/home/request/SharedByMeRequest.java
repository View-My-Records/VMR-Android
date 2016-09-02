package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.model.folder_structure.VmrSharedItem;
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
public class SharedByMeRequest extends NetworkRequest<List<VmrSharedItem>> {

    private Map<String, String> formData;

    public SharedByMeRequest(
            Map<String, String> formData,
            Response.Listener<List<VmrSharedItem>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.SHARE_RECORDS, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*" );
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
        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + this.formData.toString());
        return this.formData;
    }

    @Override
    protected Response<List<VmrSharedItem>> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + jsonString);
        List<VmrSharedItem> vmrSharedItems;

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            vmrSharedItems = VmrSharedItem.parseSharedItems(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(vmrSharedItems, getCacheEntry());
    }
}
