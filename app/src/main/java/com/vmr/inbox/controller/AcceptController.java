package com.vmr.inbox.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.inbox.request.AcceptRequest;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class AcceptController {

    private OnAcceptListener onAcceptListener;

    public AcceptController(OnAcceptListener onAcceptListener) {
        this.onAcceptListener = onAcceptListener;
    }

    /*
        pageMode:"ADD_PARTNER",
        clientId: < toUserId in JSON>
        partnerId: < userdetails.slNo in JSON>
        inboxRowId: <inboxid in JSON>
        associationTypeIndex: <referenceId in JSON>

        url: accountSetup.do
     */

    public void accept(String clientId, String partnerId, String inboxId, String referenceId ){
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.Inbox.Accept.PAGE_MODE, Constants.Request.Inbox.PageMode.ADD_PARTNER);
        formData.put(Constants.Request.Inbox.Accept.CLIENT_ID, clientId);
        formData.put(Constants.Request.Inbox.Accept.PARTNER_ID, partnerId);
        formData.put(Constants.Request.Inbox.Accept.INBOX_ROW_ID, inboxId);
        formData.put(Constants.Request.Inbox.Accept.ASSOCIATION_TYPE_INDEX, referenceId);

        AcceptRequest request =
                new AcceptRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                onAcceptListener.onAcceptSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onAcceptListener.onAcceptFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.Inbox.Accept.TAG);
    }

    public interface OnAcceptListener {
        void onAcceptSuccess(JSONObject response);
        void onAcceptFailure(VolleyError error);
    }
}
