package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.vmr.model.UploadPacket;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;
import com.vmr.utils.VmrURL;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/*
 * Created by abhijit on 8/25/16.
 */
public class NewUploadRequest extends PostLoginRequest<JSONObject> {

    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;
    String boundary = "--" + String.valueOf(System.currentTimeMillis());
    private Map<String, String> formData;
//    private MultipartEntity entity = new MultipartEntity();
    private UploadPacket uploadPacket;


    public NewUploadRequest(
            Map<String, String> formData,
            UploadPacket uploadPacket,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFileUploadUrl(), successListener, errorListener);
        this.formData = formData;
        this.uploadPacket = uploadPacket;
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setBoundary(boundary);
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            entity.addTextBody(entry.getKey(), entry.getValue());
        }
        entity.addPart(Constants.Request.FolderNavigation.UploadFile.FILE, new FileBody(uploadPacket.getFile()));
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headerMap = super.getHeaders();
        headerMap.put("Content-Type", "multipart/form-data; " + boundary );
        return headerMap;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity = entity.build();
            httpentity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(jsonObject, getCacheEntry());
    }
}
