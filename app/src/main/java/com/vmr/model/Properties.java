package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 9/9/16.
 */

public class Properties {

    public static Map<String , JSONObject > parseProperties(JSONObject jsonObject) throws JSONException {
        Map<String , JSONObject > classifications =  new HashMap<>();
        JSONArray jsonArray = jsonObject.getJSONArray("Properties");
        JSONObject j;
        for (int i= 0; i < jsonArray.length(); i++) {
            j = (JSONObject) jsonArray.get(i);
            classifications.put( j.getString("propertyName"), j );
        }
        return classifications;
    }
}
