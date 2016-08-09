package com.vmr.JSONParsers;

/*
 * Created by abhijit on 8/7/16.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONParserLogin {

    private JSONObject  jsonObject;
    private String jsonString;

    public JSONParserLogin(String json) {
        jsonString = json;
    }

    public Boolean isValid(){
        try{
            Object obj = new JSONTokener(jsonString).nextValue();
            if (obj instanceof JSONObject || obj instanceof JSONArray){
                jsonObject = new JSONObject(jsonString);
                System.out.print("Is JSON");
                return true;
            } else {
                System.out.print("Is not JSON");
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getKey(String key){
        try {
            if(jsonObject.has(key)) {
                return jsonObject.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
