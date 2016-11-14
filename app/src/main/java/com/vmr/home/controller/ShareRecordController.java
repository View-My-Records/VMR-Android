package com.vmr.home.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.home.request.ShareRequest;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class ShareRecordController {

    private OnShareRecordListener shareRecordListener;

    public ShareRecordController(OnShareRecordListener shareRecordListener) {
        this.shareRecordListener = shareRecordListener;
    }

    public void shareRecord(JSONObject shareJson, int totalCount, int currentCount, String recordNames){
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.Share.ShareRecord.PAGE_MODE, Constants.Request.Share.PageMode.SHARE_RECORDS);
        formData.put(Constants.Request.Share.ShareRecord.SHARE_JSON, shareJson.toString());
        formData.put(Constants.Request.Share.ShareRecord.TOTAL_RECORD_COUNT, String.valueOf(totalCount));
        formData.put(Constants.Request.Share.ShareRecord.CURRENT_RECORD_COUNT, String.valueOf(currentCount));
        formData.put(Constants.Request.Share.ShareRecord.SHARED_FOLDER_OR_FILENAMES, recordNames);

        ShareRequest request =
                new ShareRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                shareRecordListener.onShareRecordSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                shareRecordListener.onShareRecordFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.FolderNavigation.Message.TAG);
    }

    public interface OnShareRecordListener {
        void onShareRecordSuccess(JSONObject jsonObject);
        void onShareRecordFailure(VolleyError error);
    }
}
