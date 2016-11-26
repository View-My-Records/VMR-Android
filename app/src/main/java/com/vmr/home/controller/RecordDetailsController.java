package com.vmr.home.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.home.request.RecordDetailsRequest;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class RecordDetailsController {

    private OnFetchRecordDetailsListener fetchRecordExpiryListener;

    public RecordDetailsController(OnFetchRecordDetailsListener fetchRecordExpiryListener) {
        this.fetchRecordExpiryListener = fetchRecordExpiryListener;
    }

    public void fetchRecordDetails(String nodeRef, String emails, String recordId) {
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.Share.RecordLifespanCheck.PAGE_MODE, Constants.Request.Share.PageMode.SHARE_RECORDS_LIFESPAN_CHECK );
        formData.put(Constants.Request.Share.RecordLifespanCheck.RECORD_NODE_REF, nodeRef );
        formData.put(Constants.Request.Share.RecordLifespanCheck.SHARE_EMAILS, emails );
        formData.put(Constants.Request.Share.RecordLifespanCheck.RECORD_ID, recordId );

        RecordDetailsRequest request =
                new RecordDetailsRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                fetchRecordExpiryListener.onFetchRecordDetailsSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                fetchRecordExpiryListener.onFetchRecordDetailsFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.Share.TAG);
    }

    public interface OnFetchRecordDetailsListener {
        void onFetchRecordDetailsSuccess(JSONObject jsonObject);
        void onFetchRecordDetailsFailure(VolleyError error);
    }
}
