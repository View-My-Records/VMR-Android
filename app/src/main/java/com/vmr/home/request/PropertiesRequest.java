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
public class PropertiesRequest extends PostLoginRequest<Map<String , JSONObject >> {

    private Map<String, String> formData;

    public PropertiesRequest(
            Map<String, String> formData,
            Response.Listener<Map<String , JSONObject >> successListener,
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
    protected Response<Map<String , JSONObject >> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        Map<String , JSONObject > properties;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            properties = Properties.parseProperties(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(properties, getCacheEntry());
    }
}
