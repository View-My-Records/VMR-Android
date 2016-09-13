package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.Properties;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class DownloadRequest extends PostLoginRequest<byte[]> {

    private Map<String, String> formData;

    public DownloadRequest(
            Map<String, String> formData,
            Response.Listener<byte[]> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.FOLDER_NAVIGATION, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
//        VmrDebug.printLogI(this.getClass(), formData.toString());
        return this.formData;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, getCacheEntry());
    }
}
