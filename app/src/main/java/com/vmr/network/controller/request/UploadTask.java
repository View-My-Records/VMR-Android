package com.vmr.network.controller.request;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.model.UploadPacket;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.net.URLConnection.guessContentTypeFromName;

/*
 * Created by abhijit on 8/29/16.
 */
public class UploadTask extends AsyncTask<String, String, String> {

    public static final String HYPHANS = "--";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;
    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";
    private ProgressListener progressListener;
    private UploadPacket uploadPacket;
    private String BOUNDARY =  "----" + String.valueOf(System.currentTimeMillis());

    private HttpURLConnection connection;
    private OutputStream outputStream;

    private PrintWriter writer;

    public UploadTask(UploadPacket uploadPacket, ProgressListener progressListener) {
        this.uploadPacket = uploadPacket;
        this.progressListener = progressListener;
    }

    private static void removeUnwantedParams(){
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
        Log.i(this.getClass().getName(), "Upload initiated");
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public UploadPacket getUploadPacket() {
        return this.uploadPacket;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String urlString = VmrURL.getFileUploadUrl();

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            setRequestHeader(connection);
            outputStream = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);
            addPostParameter(Constants.Request.FolderNavigation.UploadFile.FILE_NAMES, uploadPacket.getFileName());
            addPostParameter(Constants.Request.FolderNavigation.UploadFile.CONTENT_TYPE, uploadPacket.getContentType());
            addPostParameter(Constants.Request.FolderNavigation.UploadFile.PARENT_NODE_REF, uploadPacket.getParentNodeRef());

            removeUnwantedParams();
            Map<String, String> formData = Vmr.getUserMap();
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                addPostParameter(entry.getKey(), entry.getValue());
            }

            addPostParameter(Constants.Request.FolderNavigation.UploadFile.FILE, uploadPacket.getFileUri());

            finishRequest();

            int responseCode = connection.getResponseCode();
            Log.i( this.getClass().getName(), "Response Code : " + responseCode);

            BufferedReader bufferedReader;
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                bufferedReader = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
            }

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();

            String response = stringBuilder.toString();
            VmrDebug.printLogI(this.getClass(), "Response -> " + response);

//            progressListener.onUploadProgress(uploadPacket.getFile().length(), uploadPacket.getFile().length(), 100);

            JSONObject responseJson = new JSONObject(response);

            progressListener.onUploadFinish(responseJson);

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UploadTask Error :", "Upload failed");
            progressListener.onUploadFailed();
        }
        return "Done";
    }

    private void finishRequest() {
        writer.append(CRLF).append(HYPHANS).append(BOUNDARY).append(HYPHANS).append(CRLF);
        writer.close();
    }

    private void setRequestHeader(HttpURLConnection connection) throws ProtocolException {
        // Request method
        connection.setRequestMethod("POST");

        // Request Headers
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01" );
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate" );
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8" );
        connection.setRequestProperty("Cookie", "JSESSIONID=E9CC64FF5DA0B812C5A4E4E4F8D6A967");
        connection.setRequestProperty("DNT", "1" );
        connection.setRequestProperty("Origin", "http://vmrdev.cloudapp.net:8080" );
        connection.setRequestProperty("Referer", "http://vmrdev.cloudapp.net:8080/vmr/main.do" );
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest" );
        connection.setRequestProperty("Content-Type", "multipart/form-data; " + " BOUNDARY=" + BOUNDARY);

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
    }

    private void addPostParameter(String name, String value) {
        writer.append(HYPHANS).append(BOUNDARY).append(CRLF)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(CRLF)
                .append("Content-Type: text/plain; charset=").append(CHARSET)
                .append(CRLF).append(CRLF).append(value).append(CRLF);
    }

    private void addPostParameter(final String fieldName, final Uri fileUri) throws IOException {
        String[] projection = {MediaStore.MediaColumns.DATA};
        ContentResolver cr = Vmr.getContext().getContentResolver();
        Cursor metaCursor = cr.query(fileUri, projection, null, null, null);
        String path;
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    path = metaCursor.getString(0);
                    File uploadFile = new File(path);
                    final String fileName = uploadFile.getName();
                    writer.append("--").append(BOUNDARY).append(CRLF)
                            .append("Content-Disposition: form-data; name=\"")
                            .append(fieldName).append("\"; filename=\"").append(fileName)
                            .append("\"").append(CRLF).append("Content-Type: ")
                            .append(guessContentTypeFromName(fileName)).append(CRLF)
                            .append("Content-Transfer-Encoding: binary").append(CRLF)
                            .append(CRLF);
                    writer.flush();
                    outputStream.flush();

                    byte dataBuffer[] = new byte[1024*1024];

                    long copied = 0;
                    int count;

                    final FileInputStream inputStream = new FileInputStream(uploadFile);
                    while ((count = inputStream.read(dataBuffer)) != -1) {
                        copied += count;
                        outputStream.write(dataBuffer, 0, count);
                        int progress = (int) (copied * 100 / uploadFile.length());
                        Log.i(this.getClass().getName(), "Copied : " + copied + " bytes");
                        Log.i(this.getClass().getName(), "Progress : " + progress + "%");
                        progressListener.onUploadProgress(uploadFile.length(), copied, progress);
                    }
                    outputStream.flush();
                    writer.append(CRLF);
                }
            } finally {
                metaCursor.close();
            }
        }
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
        connection.disconnect();
        progressListener.onUploadCanceled();
    }

    public interface ProgressListener {
        void onUploadStarted();
        void onUploadFailed();
        void onUploadCanceled();
        void onUploadProgress(long fileLength, long transferred, int progressPercent);
        void onUploadFinish(JSONObject jsonObject);
    }
}
