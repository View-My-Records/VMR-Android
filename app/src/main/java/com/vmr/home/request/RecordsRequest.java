package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.debug.VmrDebug;
import com.vmr.model.VmrFolder;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.VmrURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class RecordsRequest extends PostLoginRequest<VmrFolder> {

    private boolean DEBUG = true;

    private Map<String, String> formData;

    public RecordsRequest(
            Map<String, String> formData,
            Response.Listener<VmrFolder> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<VmrFolder> parseNetworkResponse(NetworkResponse response) {

        String jsonString = new String(response.data);

        if(DEBUG) VmrDebug.printLogI(this.getClass(), jsonString);

        VmrFolder folder;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            folder = new VmrFolder(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(folder, getCacheEntry());
    }
}
