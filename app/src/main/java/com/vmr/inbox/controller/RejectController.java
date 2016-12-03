package com.vmr.inbox.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.inbox.request.RejectRequest;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class RejectController {

    private OnRejectListener onRejectListener;

    public RejectController(OnRejectListener onRejectListener) {
        this.onRejectListener = onRejectListener;
    }

    /*
        pageMode:"REJECT_PARTNER_LINKING",
        inboxId: <inboxid in JSON>
        clientActionComments: <the text in textarea>
        associationType2: <referenceId in JSON>

        url : "accountSetup.do”
     */

    public void reject(String inboxId, String clientActionComments, String associationType){
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.Inbox.Reject.PAGE_MODE, Constants.Request.Inbox.PageMode.REJECT_PARTNER_LINKING);
        formData.put(Constants.Request.Inbox.Reject.INBOX_ID, inboxId);
        formData.put(Constants.Request.Inbox.Reject.CLIENT_ACTION_COMMENTS, clientActionComments);
        formData.put(Constants.Request.Inbox.Reject.ASSOCIATION_TYPE, associationType);

        RejectRequest request =
                new RejectRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                onRejectListener.onRejectSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onRejectListener.onRejectFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.Inbox.Reject.TAG);
    }

    public interface OnRejectListener {
        void onRejectSuccess(JSONObject response);
        void onRejectFailure(VolleyError error);
    }
}
