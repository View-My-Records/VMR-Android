package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.model.MyRecords;
import com.vmr.model.UserInfo;
import com.vmr.network.JSONNetworkRequest;
import com.vmr.network.NetworkRequest;
import com.vmr.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class MyRecordsRequest extends NetworkRequest<MyRecords> {

    private Map<String, String> formData;

    public MyRecordsRequest(
            Map<String, String> formData,
            Response.Listener<MyRecords> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST,Constants.Url.FOLDER_NAVIGATION, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<MyRecords> parseNetworkResponse(NetworkResponse response) {

        String jsonString = new String(response.data);
        JSONObject jsonObject;
        MyRecords myRecords =  new MyRecords();

        try {
            jsonObject = new JSONObject(jsonString);
            myRecords.setIndexedFiles(jsonObject.getJSONArray("indexedFiles"));
            myRecords.setWriteFlag(jsonObject.getBoolean("writeFlag"));
            myRecords.setSharedFolder(jsonObject.getString("sharedFolder"));
            myRecords.setFolders(jsonObject.getJSONArray("folders"));
            myRecords.setDeleteFlag(jsonObject.getBoolean("deleteFlag"));
            myRecords.setTotalUnindexed(jsonObject.getInt("totalUnindexed"));
            myRecords.setUnindexedFiles(jsonObject.getJSONArray("unindexedFiles"));
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new VolleyError("Failed to retrieve records"));
        }

        return Response.success(myRecords, getCacheEntry());
    }
}
