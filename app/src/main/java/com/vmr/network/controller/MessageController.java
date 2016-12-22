package com.vmr.network.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.db.notification.Notification;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.MessageRequest;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class MessageController {

    private OnFetchMessageListener onFetchMessageListener;

    public MessageController(OnFetchMessageListener onFetchMessageListener) {
        this.onFetchMessageListener = onFetchMessageListener;
    }

    public void fetchMessage(Notification notification){
        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.Message.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.INBOX_MESSAGE_DISPLAY );
        formData.put(Constants.Request.FolderNavigation.Message.INBOX_ID, notification.getInboxId());

        MessageRequest request =
                new MessageRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                onFetchMessageListener.onFetchMessageSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchMessageListener.onFetchMessageFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.FolderNavigation.Message.TAG);
    }

    public interface OnFetchMessageListener {
        void onFetchMessageSuccess(JSONObject response);
        void onFetchMessageFailure(VolleyError error);
    }
}
