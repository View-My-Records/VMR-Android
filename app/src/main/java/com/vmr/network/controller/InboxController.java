package com.vmr.network.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.model.NotificationItem;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.InboxRequest;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class InboxController {

    private OnFetchInboxListener onFetchInboxListener;

    public InboxController(OnFetchInboxListener onFetchInboxListener) {
        this.onFetchInboxListener = onFetchInboxListener;
    }

    public void fetchNotifications(){
        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.Inbox.DATA_TYPE, "JSON");

        InboxRequest request =
                new InboxRequest(
                        formData,
                        new Response.Listener<List<NotificationItem>>() {
                            @Override
                            public void onResponse(List<NotificationItem> inboxList) {
                                onFetchInboxListener.onFetchNotificationsSuccess(inboxList);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchInboxListener.onFetchNotificationsFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.Inbox.TAG);
    }

    public interface OnFetchInboxListener {
        void onFetchNotificationsSuccess(List<NotificationItem> notificationItemList);
        void onFetchNotificationsFailure(VolleyError error);
    }
}
