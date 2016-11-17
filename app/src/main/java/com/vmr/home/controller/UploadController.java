package com.vmr.home.controller;

/*
 * Created by abhijit on 10/11/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.home.request.UploadRequest;
import com.vmr.model.UploadPacket;
import com.vmr.network.VmrRequestQueue;
import com.vmr.network.error.CancelError;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

public class UploadController {

    private OnFileUpload onFileUpload;

    private UploadRequest uploadRequest;
    private int requestTag;

    public UploadController(OnFileUpload onFileUpload) {
        this.onFileUpload = onFileUpload;
    }

    public void uploadFile(UploadPacket uploadPacket, int uploadId, UploadRequest.UploadProgressListener progressListener)  {

        requestTag = uploadId;

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.UploadFile.FILE_NAMES, Uri.encode(uploadPacket.getFileName()));
        formData.put(Constants.Request.FolderNavigation.UploadFile.CONTENT_TYPE, uploadPacket.getContentType());
        formData.put(Constants.Request.FolderNavigation.UploadFile.PARENT_NODE_REF, uploadPacket.getParentNodeRef());

        uploadRequest =
                new UploadRequest(
                        formData,
                        uploadPacket,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
//                                Vmr.getVMRContext().unregisterReceiver(cancelUploadReceiver);
                                onFileUpload.onFileUploadSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                Vmr.getVMRContext().unregisterReceiver(cancelUploadReceiver);
                                onFileUpload.onFileUploadFailure(error);
                            }
                        },
                        progressListener
                );

        uploadRequest.setTag(requestTag);

        LocalBroadcastManager.getInstance(Vmr.getVMRContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("CancelUpload")) {
                    int uploadTag = intent.getIntExtra(Constants.Request.FolderNavigation.UploadFile.TAG, 0);
                    VmrDebug.printLogI(this.getClass(), "Upload Cancel action for tag ->" + uploadTag);
                    if (requestTag == uploadTag) {
                        uploadRequest.cancel();
                        onFileUpload.onFileUploadCancel(new CancelError());
                        VmrDebug.printLogI(this.getClass(), "Upload Canceled ->" + uploadTag);
                    }
                }
            }
        }, new IntentFilter("CancelUpload"));

        VmrRequestQueue.getInstance()
                .addToRequestQueue(uploadRequest, String.valueOf(uploadId));
    }

    public interface OnFileUpload{
        void onFileUploadSuccess(JSONObject response);
        void onFileUploadFailure(VolleyError error);
        void onFileUploadCancel(VolleyError error);
    }

//    private BroadcastReceiver cancelUploadReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals("CancelUpload")) {
//                String uploadTag = intent.getStringExtra(Constants.Request.FolderNavigation.UploadFile.TAG);
//                VmrDebug.printLogI(this.getClass(), "Upload Cancel action for tag ->" + uploadTag);
//                if (requestTag.equals(uploadTag)) {
//                    uploadRequest.cancel();
//                    onFileUpload.onFileUploadCancel(new CancelError());
//                    VmrDebug.printLogI(this.getClass(), "Upload Canceled ->" + uploadTag);
//                }
//            }
//        }
//    };
}
