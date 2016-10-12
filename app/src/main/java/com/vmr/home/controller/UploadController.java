package com.vmr.home.controller;

/*
 * Created by abhijit on 10/11/16.
 */

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.home.request.UploadRequest;
import com.vmr.model.UploadPacket;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

public class UploadController {

    private OnFileUpload onFileUpload;

    public UploadController(OnFileUpload onFileUpload) {
        this.onFileUpload = onFileUpload;
    }

    public void uploadFile(UploadPacket uploadPacket)  {

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.UploadFile.FILE_NAMES, Uri.encode(uploadPacket.getFileName()));
        formData.put(Constants.Request.FolderNavigation.UploadFile.CONTENT_TYPE, uploadPacket.getContentType());
        formData.put(Constants.Request.FolderNavigation.UploadFile.PARENT_NODE_REF, uploadPacket.getParentNodeRef());

        UploadRequest uploadRequest =
                new UploadRequest(
                        formData,
                        uploadPacket,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                onFileUpload.onFileUploadSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileUpload.onFileUploadFailure(error);
                            }
                        }
                );

//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                VmrDebug.printLogI(this.getClass(), uploadRequest.getHeaders().toString());
//                VmrDebug.printLogI(this.getClass(), new String(uploadRequest.getBody(), StandardCharsets.UTF_8));
//            }
//        } catch (AuthFailureError authFailureError) {
//            authFailureError.printStackTrace();
//        }
        VmrRequestQueue.getInstance()
                .addToRequestQueue(uploadRequest, Constants.Request.FolderNavigation.UploadFile.TAG);
    }

    public interface OnFileUpload{
        void onFileUploadSuccess(JSONObject response);
        void onFileUploadFailure(VolleyError error);
    }
}
