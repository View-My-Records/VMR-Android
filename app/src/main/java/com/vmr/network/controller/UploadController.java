package com.vmr.network.controller;

/*
 * Created by abhijit on 10/11/16.
 */

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.model.UploadPacket;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.UploadRequest;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.Map;

public class UploadController {

    private OnFileUpload onFileUpload;

    public UploadController(OnFileUpload onFileUpload) {
        this.onFileUpload = onFileUpload;
    }

    public void uploadFile(UploadPacket uploadPacket, int uploadId, UploadRequest.UploadProgressListener progressListener) throws FileNotFoundException {

        Map<String, String> formData = Vmr.getUserMap();
//        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.UploadFile.FILE_NAMES, Uri.encode(uploadPacket.getFileName()));
        formData.put(Constants.Request.FolderNavigation.UploadFile.CONTENT_TYPE, uploadPacket.getContentType());
        formData.put(Constants.Request.FolderNavigation.UploadFile.PARENT_NODE_REF, uploadPacket.getParentNodeRef());

        UploadRequest uploadRequest = new UploadRequest(
                formData,
                uploadPacket,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
//                                Vmr.getContext().unregisterReceiver(cancelUploadReceiver);
                        onFileUpload.onFileUploadSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                                Vmr.getContext().unregisterReceiver(cancelUploadReceiver);
                        onFileUpload.onFileUploadFailure(error);
                    }
                },
                progressListener
        );

        uploadRequest.setTag(uploadId);

        VolleySingleton.getInstance()
                .addToRequestQueue(uploadRequest, String.valueOf(uploadId));
    }

    public interface OnFileUpload{
        void onFileUploadSuccess(JSONObject response);
        void onFileUploadFailure(VolleyError error);
        void onFileUploadCancel(VolleyError error);
    }
}
