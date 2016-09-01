package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.folder_structure.VmrTrashItem;
import com.vmr.network.NetworkRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/25/16.
 */
public class TrashRequest extends NetworkRequest<List<VmrTrashItem>> {

    private Map<String, String> formData;

    public TrashRequest(
            Map<String, String> formData,
            Response.Listener<List<VmrTrashItem>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.FOLDER_NAVIGATION, successListener, errorListener);
        this.formData = formData;
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
