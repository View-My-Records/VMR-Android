package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.VmrTrashItem;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.network.error.ParseError;
import com.vmr.utils.VmrURL;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/25/16.
 */
public class TrashRequest extends PostLoginRequest<List<VmrTrashItem>> {

    private Map<String, String> formData;

    public TrashRequest(
            Map<String, String> formData,
            Response.Listener<List<VmrTrashItem>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
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
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }

        return Response.success(vmrTrashItems, getCacheEntry());
    }
}
