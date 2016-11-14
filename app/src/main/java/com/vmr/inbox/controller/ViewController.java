package com.vmr.inbox.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.inbox.request.ViewRequest;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class ViewController {

    private OnViewListener onViewListener;

    public ViewController(OnViewListener onViewListener) {
        this.onViewListener = onViewListener;
    }

    /*
        pageMode : "MULTIPLE_SHARE_ACTION",
        fileSelectedNodeRef : <documentAccessReqId.docId in JSON>,
        shareFromId: <userdetails.slNo in JSON>,
        shareToId: <toUserId in JSON>,
        inboxRefId: <referenceId in JSON>

        url : "shareRecords.do"
     */

    public void viewMessage(String fileSelectedNodeRef, String shareFromId, String shareToId, String inboxRefId) {
        Map<String, String> formData = Vmr.getUserMap();

        formData.put(Constants.Request.Share.ViewRecord.PAGE_MODE, Constants.Request.Share.PageMode.MULTIPLE_SHARE_ACTION);
        formData.put(Constants.Request.Share.ViewRecord.FILE_SELECTED_NODE_REF, fileSelectedNodeRef);
        formData.put(Constants.Request.Share.ViewRecord.SHARE_FROM_ID, shareFromId);
        formData.put(Constants.Request.Share.ViewRecord.SHARE_TO_ID, shareToId);
        formData.put(Constants.Request.Share.ViewRecord.INBOX_REF_ID, inboxRefId);

        ViewRequest request =
                new ViewRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                onViewListener.onViewSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onViewListener.onViewFailure(error);
                            }
                        }
                );
        VmrDebug.printLogI(this.getClass(), formData.toString());
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.Share.ViewRecord.TAG);
    }

    public interface OnViewListener {
        void onViewSuccess(JSONObject response);
        void onViewFailure(VolleyError error);
    }
}
