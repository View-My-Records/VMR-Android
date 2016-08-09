package com.vmr.AsyncTasksForRequests;

import android.os.AsyncTask;

import com.vmr.JSONParsers.JSONParserLogin;

/*
 * Created by abhijit on 8/8/16.
 */

public class AsyncTaskLoginParseJSON extends AsyncTask<String, Void, String>{

    @Override
    protected String doInBackground(String... params) {
        JSONParserLogin jsonParser = new JSONParserLogin(params[0]);
        if(jsonParser.isValid()){
            if(jsonParser.getKey("result").equals("success")) {
                return "Login success.";
            } else {
                return "Invalid credentials.";
            }
        } else {
            return "Invalid JSON.";
        }
    }
}
