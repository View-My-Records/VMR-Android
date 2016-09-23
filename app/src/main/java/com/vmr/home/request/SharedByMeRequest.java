package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.VmrSharedItem;
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
public class SharedByMeRequest extends PostLoginRequest<List<VmrSharedItem>> {

    private Map<String, String> formData;

    public SharedByMeRequest(
            Map<String, String> formData,
            Response.Listener<List<VmrSharedItem>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getShareRecordsUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
//        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + this.formData.toString());
        return this.formData;
    }

    @Override
    protected Response<List<VmrSharedItem>> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
//        VmrDebug.printLogI(this.getClass().getSimpleName() + ": " + jsonString);
        List<VmrSharedItem> vmrSharedItems;

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            vmrSharedItems = VmrSharedItem.parseSharedItems(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }

        return Response.success(vmrSharedItems, getCacheEntry());
    }
}
