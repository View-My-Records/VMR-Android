package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.Classification;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.VmrURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class ClassificationRequest extends PostLoginRequest<Map<String , String >> {

    private Map<String, String> formData;

    public ClassificationRequest(
            Map<String, String> formData,
            Response.Listener<Map<String , String >> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<Map<String , String >> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        Map<String , String > classifications;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
//            VmrDebug.printLogI(this.getClass(), "Classification request headers:" + formData.toString());
//            VmrDebug.printLogI(this.getClass(), jsonObject.toString());
            classifications = Classification.parseClassifications(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(classifications, getCacheEntry());
    }
}
