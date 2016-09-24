package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.model.UploadPacket;
import com.vmr.network.PostLoginRequest;
import com.vmr.network.error.FetchError;
import com.vmr.utils.Constants;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class OldUploadRequest extends PostLoginRequest<JSONObject> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private Map<String, String> formData;
    private Map<String, UploadPacket> uploadPacketMap;
    private MultipartEntity entity = new MultipartEntity();

    public OldUploadRequest(
            Map<String, String> formData,
            Map<String, UploadPacket> uploadPacketMap,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, Constants.Url.FILE_UPLOAD, successListener, errorListener);
        this.formData = formData;
        this.uploadPacketMap = uploadPacketMap;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
//        VmrDebug.printLogI(this.getClass(), formData.toString());
        return this.formData;
    }

    protected Map<String, UploadPacket> getByteData() throws AuthFailureError {
        return uploadPacketMap;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(dos, params, getParamsEncoding());
            }

            Map<String, UploadPacket> data = getByteData();
            if (data != null && data.size() > 1) {
                dataParse(dos, data);
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
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

    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    private void dataParse(DataOutputStream dataOutputStream, Map<String, UploadPacket> data) throws IOException {
        for (Map.Entry<String, UploadPacket> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        //dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    private void buildDataPart(DataOutputStream dataOutputStream, UploadPacket uploadPacket, String inputName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + uploadPacket.getFileName() + "\"" + lineEnd);

        if (uploadPacket.getContentType() != null && !uploadPacket.getContentType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + uploadPacket.getContentType() + lineEnd);
        }

        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(uploadPacket.getFileByteArray());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }
}
