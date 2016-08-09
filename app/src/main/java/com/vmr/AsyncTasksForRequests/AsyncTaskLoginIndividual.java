package com.vmr.AsyncTasksForRequests;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 * Created by abhijit on 8/7/16.
 */

public class AsyncTaskLoginIndividual extends AsyncTask<String,Void, String> {

    /*
        param[0] username
        param[1] password
        param[2] -NA-
     */
    @Override
    protected String doInBackground(String... params) {
        StringBuilder response = new StringBuilder();
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
        try {
            URL url = new URL("http://vmrdev.cloudapp.net:8080/vmr/mlogin.do");

            // Create connection for request
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Create header for request
            connection.setRequestMethod("POST");
            connection.setUseCaches (false);
            connection.setInstanceFollowRedirects( false );
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Create body for request
            String formBody =
                    URLEncoder.encode("emailID", "UTF-8") + "=" +
                        URLEncoder.encode(params[0], "UTF-8");
            formBody += "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(params[1], "UTF-8");
            formBody += "&" +
                    URLEncoder.encode("domain", "UTF-8") + "=" +
                        URLEncoder.encode("IND", "UTF-8");

            DataOutputStream outputStreamForBody = new DataOutputStream(connection.getOutputStream());
            outputStreamForBody.write(formBody.getBytes());
            outputStreamForBody.flush ();
            outputStreamForBody.close ();

            // Send Request and get Response
            InputStream is = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            bufferedReader.close();

            System.out.println("Response: " + response.toString());

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
//            }
//        });
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return response.toString();
    }

    protected void onPostExecute(String s){
        Log.e("TaskLoginIndividual", "Completed");
    }

}