package com.vmr.home.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.home.request.RecordExpiryRequest;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class RecordExpiryController {

    private OnFetchRecordExpiryListener fetchRecordExpiryListener;

    public RecordExpiryController(OnFetchRecordExpiryListener fetchRecordExpiryListener) {
        this.fetchRecordExpiryListener = fetchRecordExpiryListener;
    }

    public void fetchRecordDetails(String nodeRef, String emails, String recordId) {
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.Share.RecordLifespanCheck.PAGE_MODE, Constants.Request.Share.PageMode.SHARE_RECORDS_LIFESPAN_CHECK );
        formData.put(Constants.Request.Share.RecordLifespanCheck.RECORD_NODE_REF, nodeRef );
        formData.put(Constants.Request.Share.RecordLifespanCheck.SHARE_EMAILS, emails );
        formData.put(Constants.Request.Share.RecordLifespanCheck.RECORD_ID, recordId );

        RecordExpiryRequest request =
                new RecordExpiryRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                fetchRecordExpiryListener.onFetchRecordExpirySuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                fetchRecordExpiryListener.onFetchRecordExpiryFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.Share.TAG);
    }

    public interface OnFetchRecordExpiryListener {
        void onFetchRecordExpirySuccess(JSONObject jsonObject);
        void onFetchRecordExpiryFailure(VolleyError error);
    }
}
