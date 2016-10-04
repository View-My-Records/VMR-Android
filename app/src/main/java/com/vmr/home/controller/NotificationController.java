package com.vmr.home.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.home.request.InboxRequest;
import com.vmr.model.NotificationItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class NotificationController {

    private OnFetchNotificationsListener onFetchNotificationsListener;

    public NotificationController(OnFetchNotificationsListener onFetchNotificationsListener) {
        this.onFetchNotificationsListener = onFetchNotificationsListener;
    }

    public void fetchNotifications(){
        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.Notification.DATA_TYPE, "JSON");

        InboxRequest request =
                new InboxRequest(
                        formData,
                        new Response.Listener<List<NotificationItem>>() {
                            @Override
                            public void onResponse(List<NotificationItem> inboxList) {
                                onFetchNotificationsListener.onFetchNotificationsSuccess(inboxList);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchNotificationsListener.onFetchNotificationsFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.Notification.TAG);
    }

    public interface OnFetchNotificationsListener {
        void onFetchNotificationsSuccess(List<NotificationItem> notificationItemList);
        void onFetchNotificationsFailure(VolleyError error);
    }
}
