package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.vmr.debug.VmrDebug;
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
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/*
 * Created by abhijit on 8/25/16.
 */
public class UploadRequest extends PostLoginRequest<JSONObject> {

    private MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    private HttpEntity httpentity;
    private String boundary = String.valueOf(System.currentTimeMillis());
    private Map<String, String> formData;
//    private MultipartEntity entity = new MultipartEntity();
    private UploadPacket uploadPacket;
    private UploadProgressListener progressListener;

    public UploadRequest(
            Map<String, String> formData,
            UploadPacket uploadPacket,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener,
            UploadProgressListener progressListener) {
        super(Method.POST, VmrURL.getFileUploadUrl(), successListener, errorListener);
        this.formData = formData;
        this.uploadPacket = uploadPacket;
        this.progressListener = progressListener;
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setBoundary(boundary);
        buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headerMap = super.getHeaders();
        headerMap.put("Content-Type", "multipart/form-data; " + " boundary=" + boundary );
        return headerMap;
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity = entity.build();
            httpentity.writeTo(new OutputStreamWithProgress(bos, uploadPacket.getFile().length(),
                    progressListener));
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);

        VmrDebug.printLogI(this.getClass(), response.headers.toString());
        VmrDebug.printLogI(this.getClass(),  new String(response.data));

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new FetchError());
        }

        return Response.success(jsonObject, getCacheEntry());
    }

    private void buildMultipartEntity() {
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            entity.addTextBody(entry.getKey(), entry.getValue());
        }
        entity.addPart(Constants.Request.FolderNavigation.UploadFile.FILE, new FileBody(uploadPacket.getFile()));
    }

    public interface UploadProgressListener {
        void onUploadProgress(long fileLength, long transferred, int progressPercent);
    }

    private static class OutputStreamWithProgress extends FilterOutputStream {
        private final UploadProgressListener progressListener;
        private long transferred;
        private long fileLength;

        OutputStreamWithProgress(final OutputStream out, long fileLength,
                                 final UploadProgressListener listener) {
            super(out);
            this.fileLength = fileLength;
            this.progressListener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            if (progressListener != null) {
                this.transferred += len;
                int progress = (int) (transferred * 100 / fileLength);
                this.progressListener.onUploadProgress(this.fileLength, this.transferred, progress);
            }
        }

        public void write(int b) throws IOException {
            out.write(b);
            if (progressListener != null) {
                this.transferred++;
                int progress = (int) (transferred * 100 / fileLength);
                this.progressListener.onUploadProgress(this.fileLength , this.transferred, progress);
            }
        }

    }
}
