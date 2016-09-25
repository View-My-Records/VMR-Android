package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.network.PostLoginRequest;
import com.vmr.utils.VmrURL;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class DownloadRequest extends PostLoginRequest<File> {

    private Map<String, String> formData;

    public DownloadRequest(
            Map<String, String> formData,
            Response.Listener<File> successListener,
            Response.ErrorListener errorListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
//        VmrDebug.printLogI(this.getClass(), formData.toString());
        return this.formData;
    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.success(tempFile, getCacheEntry());
    }
}
