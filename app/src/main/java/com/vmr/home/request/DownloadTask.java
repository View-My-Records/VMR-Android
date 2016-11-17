package com.vmr.home.request;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.model.DownloadPacket;
import com.vmr.utils.Constants;
import com.vmr.utils.VmrURL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/29/16.
 */
public class DownloadTask extends AsyncTask<String, String, String> {

    private DownloadProgressListener progressListener;
    private DownloadPacket downloadPacket;
    private String authenticationParams;

    private HttpURLConnection urlConnection;

    public DownloadTask(DownloadPacket downloadPacket, DownloadProgressListener progressListener) {
        this.downloadPacket = downloadPacket;
        this.progressListener = progressListener;
    }

    public static void removeUnwantedParams(){
        List<String> list = new ArrayList<>();
        list.add(Constants.Request.Login.DOMAIN);
        list.add(Constants.Request.Login.Individual.EMAIL_ID);
        list.add(Constants.Request.Login.Individual.PASSWORD);
        list.add(Constants.Request.Login.Family.EMAIL_ID);
        list.add(Constants.Request.Login.Family.PASSWORD);
        list.add(Constants.Request.Login.Family.NAME);
        list.add(Constants.Request.Login.Professional.EMAIL_ID);
        list.add(Constants.Request.Login.Professional.PASSWORD);
        list.add(Constants.Request.Login.Professional.NAME);
        list.add(Constants.Request.Login.Corporate.EMAIL_ID);
        list.add(Constants.Request.Login.Corporate.PASSWORD);
        list.add(Constants.Request.Login.Corporate.NAME);

        list.add(Constants.Request.Login.Custom.Individual.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Individual.PASSWORD);
        list.add(Constants.Request.Login.Custom.Family.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Family.PASSWORD);
        list.add(Constants.Request.Login.Custom.Family.NAME);
        list.add(Constants.Request.Login.Custom.Professional.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Professional.PASSWORD);
        list.add(Constants.Request.Login.Custom.Professional.NAME);
        list.add(Constants.Request.Login.Custom.Corporate.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Corporate.PASSWORD);
        list.add(Constants.Request.Login.Custom.Corporate.NAME);

        Vmr.getUserMap().keySet().retainAll(list);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(this.getClass().getName(), "Download initiated");
    }

    public void setProgressListener(DownloadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public DownloadPacket getDownloadPacket() {
        return this.downloadPacket;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
//            String urlString = "http://vmrdev.cloudapp.net:8080/vmr/mlogin.do";
            String urlString = VmrURL.getFolderNavigationUrl();

            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            // Request method
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01" );
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate" );
            urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.8" );
            urlConnection.setRequestProperty("Cookie", "JSESSIONID=E9CC64FF5DA0B812C5A4E4E4F8D6A967");
            urlConnection.setRequestProperty("DNT", "1" );
            urlConnection.setRequestProperty("Origin", "http://vmrdev.cloudapp.net:8080" );
            urlConnection.setRequestProperty("Referer", "http://vmrdev.cloudapp.net:8080/vmr/main.do" );
            urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest" );

            StringBuilder postParameters
                    = new StringBuilder()
//                    .append("emailID=" + "induser" + "&")
//                    .append("password=" + "Qwer!234" + "&")
//                    .append("domain=" + "IND" + "&")
                    .append("pageMode=" + "DOWNLOAD_FILE_STREAM" + "&")
                    .append("fileSelectedNodeRef=" + downloadPacket.getFileSelectedNodeRef() + "&")
                    .append("fileName=" + downloadPacket.getFileName() + "&")
                    .append("mimeType=").append(downloadPacket.getMimeType());

            removeUnwantedParams();
            Map<String, String> formData = Vmr.getUserMap();

            for (Map.Entry<String, String> entry : formData.entrySet()) {
                postParameters.append("&")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
//                System.out.println(entry.getKey() + "/" + entry.getValue());
            }

            VmrDebug.printLogI(this.getClass(), postParameters.toString());

            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(postParameters.toString());
            wr.flush();
            wr.close();

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            Log.i( this.getClass().getName(), "Response Code : " + responseCode);

            Log.i( this.getClass().getName(), "Response Content Length : " + downloadPacket.getFileLength());


            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), downloadPacket.getFileName());
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile.getAbsolutePath());

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte dataBuffer[] = new byte[1024];

            long copied = 0;
            int count;

            progressListener.onDownloadStarted();

            while ((count = bufferedInputStream.read(dataBuffer)) != -1) {
                copied += count;
                bufferedOutputStream.write(dataBuffer, 0, count);
                int progress = (int) ( copied * 100 / downloadPacket.getFileLength());
                Log.i(this.getClass().getName(), "Copied : " + copied + " bytes");
                Log.i(this.getClass().getName(), "Progress : " + progress + "%");
                progressListener.onDownloadProgress(downloadPacket.getFileLength(), copied, progress);
            }

            progressListener.onDownloadProgress(downloadPacket.getFileLength(), downloadPacket.getFileLength(), 100);

            bufferedInputStream.close();
            bufferedOutputStream.close();
            inputStream.close();
            fileOutputStream.close();

            progressListener.onDownloadFinish(tempFile);

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            progressListener.onDownloadFailed();
        }
        return "Done";
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        urlConnection.disconnect();
        progressListener.onDownloadCanceled();
    }

    public interface DownloadProgressListener {
        void onDownloadStarted();
        void onDownloadFailed();
        void onDownloadCanceled();
        void onDownloadProgress(long fileLength, long transferred, int progressPercent);
        void onDownloadFinish(File file);
    }
}
