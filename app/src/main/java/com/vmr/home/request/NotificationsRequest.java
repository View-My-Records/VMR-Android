package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.NotificationItem;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.VmrURL;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class NotificationsRequest extends PostLoginRequest<List<NotificationItem>> {

    private Map<String, String> formData;

    public NotificationsRequest(
            Map<String, String> formData,
            Response.Listener<List<NotificationItem>> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getNotificationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<List<NotificationItem>> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        List<NotificationItem> notificationItemList;

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            notificationItemList = NotificationItem.getInboxList(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(notificationItemList, getCacheEntry());
    }
}
