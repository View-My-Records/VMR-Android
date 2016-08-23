package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.model.MyRecords;
import com.vmr.model.UserInfo;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.network.JSONNetworkRequest;
import com.vmr.network.NetworkRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class MyRecordsRequest extends NetworkRequest<VmrFolder> {

    private Map<String, String> formData;

    public MyRecordsRequest(
            Map<String, String> formData,
            Response.Listener<VmrFolder> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST,Constants.Url.FOLDER_NAVIGATION, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<VmrFolder> parseNetworkResponse(NetworkResponse response) {

        String jsonString = new String(response.data);
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
