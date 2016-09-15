package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.DeleteMessage;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class DeleteFromTrashRequest extends PostLoginRequest<List<DeleteMessage>> {

    private Map<String, String> formData;

    public DeleteFromTrashRequest(
            Map<String, String> formData,
            Response.Listener<List<DeleteMessage>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.FOLDER_NAVIGATION, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<List<DeleteMessage>> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        List<DeleteMessage> deleteMessages;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            deleteMessages = DeleteMessage.parseDeleteMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(deleteMessages, getCacheEntry());
    }
}