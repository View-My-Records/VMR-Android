package com.vmr.home.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.vmr.network.PostLoginRequest;
import com.vmr.utils.VmrURL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class DownloadRequest extends PostLoginRequest<File> {

    private Map<String, String> formData;
    private DownloadProgressListener progressListener;

    public DownloadRequest(
            Map<String, String> formData,
            Response.Listener<File> successListener,
            Response.ErrorListener errorListener,
            DownloadProgressListener progressListener) {
        super(Method.POST, VmrURL.getFolderNavigationUrl(), successListener, errorListener);
        this.formData = formData;
        this.progressListener = progressListener;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
//        VmrDebug.printLogI(this.getClass(), formData.toString());
        return this.formData;
    }

    @Override
    protected void deliverResponse(File response) {
        super.deliverResponse(response);
    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) {
        File tempFile = null;
        try {
            InputStream fileInputStream = new ByteArrayInputStream(response.data);

            tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), null);

            FileOutputStream fileOutputStream = new FileOutputStream(tempFile.getAbsolutePath());

            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte dataBuffer[] = new byte[1024]; // rename to buffer

            long total = response.data.length;
            long copied = 0;
            int count;

            while ((count = bufferedInputStream.read(dataBuffer)) != -1) {
                copied += count;
                bufferedOutputStream.write(dataBuffer, 0, count);
                int progress = (int) ( copied * 100 / total );
                progressListener.onDownloadProgress(total, copied, progress);
            }

//            FileOutputStream fileOutputStream = new FileOutputStream(tempFile.getAbsolutePath());
//            fileOutputStream.write(response.data);
            fileInputStream.close();
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.success(tempFile, getCacheEntry());
    }

    public interface DownloadProgressListener {
        void onDownloadProgress(long fileLength, long transferred, int progressPercent);
    }

    private static class InputStreamWithProgress extends FilterOutputStream {
        private final DownloadProgressListener progressListener;
        private long transferred;
        private long fileLength;

        InputStreamWithProgress(final OutputStream out, long fileLength,
                                 final DownloadProgressListener listener) {
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
                this.progressListener.onDownloadProgress(this.fileLength, this.transferred, progress);
            }
        }

        public void write(int b) throws IOException {
            out.write(b);
            if (progressListener != null) {
                this.transferred++;
                int progress = (int) (transferred * 100 / fileLength);
                this.progressListener.onDownloadProgress(this.fileLength , this.transferred, progress);
            }
        }

    }
}
