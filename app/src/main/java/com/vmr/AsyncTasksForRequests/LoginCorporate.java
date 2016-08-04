package com.vmr.AsyncTasksForRequests;

import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * Created by abhijit on 8/3/16.
 */

public class LoginCorporate extends AsyncTask<String,Void, String> {

    static final String COOKIES_HEADER = "Set-Cookie";
    HttpURLConnection connection = null;
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    @Override
    protected String doInBackground(String... params) {


        StringBuilder response1 = new StringBuilder();
        StringBuilder response2 = new StringBuilder();
        try {
            URL url = new URL("http://vmrdev.cloudapp.net:8080/vmr/mlogin.do?d=0");

            // ********** First request to get Session ID
            Log.e("Request:", "1");

            // Create connection
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            connection.setRequestProperty("Accept-Language", "en-US,en,q=0.8");
            connection.setRequestProperty("DNT", "1");
            connection.setRequestProperty("Origin", "http://vmrdev.cloudapp.net:8080");
            connection.setRequestProperty("Referrer", "http://vmrdev.cloudapp.net:8080/vmr/mlogin.do");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.flush ();
            wr.close ();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = rd.readLine()) != null) {
                response1.append(line);
                response1.append('\r');
            }
            rd.close();

            Map<String, List<String>> headerFields = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                System.out.println("Key : " + entry.getKey()
                        + " ,Value : " + entry.getValue());
            }

            System.out.println("Response 1" + response1.toString());

            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            if(cookiesHeader != null)
            {
                for (String cookie : cookiesHeader)
                {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            connection.disconnect();

            // **********Second request
            Log.e("Request:", "2");
            System.out.println( "Cookie: " +TextUtils.join(";",msCookieManager.getCookieStore().getCookies()));

            String formBody = URLEncoder.encode("corpEmailID", "UTF-8") + "=" + URLEncoder.encode("admin", "UTF-8");
            formBody += "&" + URLEncoder.encode("corpPassword", "UTF-8") + "=" + URLEncoder.encode("Test*1234", "UTF-8");
            formBody += "&" + URLEncoder.encode("corpName", "UTF-8") + "=" + URLEncoder.encode("comp1", "UTF-8");
            formBody += "&" + URLEncoder.encode("domain", "UTF-8") + "=" + URLEncoder.encode("CORP", "UTF-8");

//            "corpEmailID=admin\ncorpPassword=Test*1234\ncorpName=comp1\ndomain=CORP";

            // Create connection
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            connection.setRequestProperty("Accept-Language", "en-US,en,q=0.8");
            connection.setRequestProperty("DNT", "1");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Cookie", TextUtils.join(";",msCookieManager.getCookieStore().getCookies()));
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");
//            connection.setRequestProperty( "Content-Length", Integer.toString( formBody.length() ));
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects( false );


            // Send request
            wr = new DataOutputStream ( connection.getOutputStream());
            wr.write(formBody.getBytes());
            wr.flush ();
            wr.close ();

//            System.out.println("FormData->" + formBody);
            // Get Response
            is = connection.getInputStream();
            rd = new BufferedReader(new InputStreamReader(is));

            while((line = rd.readLine()) != null) {
                response2.append(line);
                response2.append('\r');
            }
            rd.close();


            headerFields = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                System.out.println("Key : " + entry.getKey()
                        + " ,Value : " + entry.getValue());
            }

            System.out.println("Response 2" + response2.toString());

            connection.disconnect();


        } catch (Exception e) {

            e.printStackTrace();

        }

        return response2.toString();
    }

    protected void onPostExecute(String s){
        Log.e("AsyncTask", "Completed");
    }
}
