package com.vmr.network.controller.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.RecordDetails;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class RecordDetailsRequest extends PostLoginRequest<RecordDetails> {

    private Map<String, String> formData;

    public RecordDetailsRequest(
            Map<String, String> formData,
            Response.Listener<RecordDetails> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getShareRecordsUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<RecordDetails> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);

        JSONObject jsonObject;
        RecordDetails recordDetails;
        try {
            jsonObject = new JSONObject(jsonString);
            recordDetails = RecordDetails.parseDetails(jsonObject);
//            VmrDebug.printLogI(this.getClass(), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(recordDetails, getCacheEntry());
    }
}
