package com.vmr.home.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.db.notification.Notification;
import com.vmr.home.request.MessageRequest;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

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
        formData.put(Constants.Request.FolderNavigation.Message.INBOX_ID, notification.getId());

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
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.FolderNavigation.Message.TAG);
    }

    public interface OnFetchMessageListener {
        void onFetchMessageSuccess(JSONObject jsonObject);
        void onFetchMessageFailure(VolleyError error);
    }
}
