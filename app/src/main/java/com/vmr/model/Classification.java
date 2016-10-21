package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by abhijit on 9/9/16.
 */

public class Classification {

    public static Map<String , String > parseClassifications(JSONObject jsonObject) throws JSONException {
        Map<String , String > classifications =  new HashMap<>();
        JSONArray jsonArray = jsonObject.getJSONArray("DocTypes");
        JSONObject j;
        for (int i= 0; i < jsonArray.length(); i++) {
            j = (JSONObject) jsonArray.get(i);
            classifications.put( j.getString("DocName"), j.getString("DocType") );
        }
        return classifications;
    }

    public static Map<String , String > getDocumentTypes(){
        Map<String , String > classifications =  new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject("{ \"DocTypes\": " +
                    "[ " +
                        "{ \"DocType\": \"vmrind_bill\", \"DocName\": \"Bills & Warranty Cards\" }, " +
                        "{ \"DocType\": \"vmrind_education\", \"DocName\": \"Education\" }, " +
                        "{ \"DocType\": \"vmrind_fin\", \"DocName\": \"Financial Records\" }, " +
                        "{ \"DocType\": \"vmrind_identityproof\", \"DocName\": \"Identity/Address Proof\" }, " +
                        "{ \"DocType\": \"vmrind_individual\", \"DocName\": \"Individual's Records\" }, " +
                        "{ \"DocType\": \"vmrind_insurance\", \"DocName\": \"Insurance\" }, " +
                        "{ \"DocType\": \"vmrind_legal\", \"DocName\": \"Legal Documents\" }, " +
                        "{ \"DocType\": \"vmrind_healthcare\", \"DocName\": \"Medical/Healthcare\" }, " +
                        "{ \"DocType\": \"vmrind_others\", \"DocName\": \"Others\" }, " +
                        "{ \"DocType\": \"vmrind_photo\", \"DocName\": \"Photo/Video\" }, " +
                        "{ \"DocType\": \"vmrind_property\", \"DocName\": \"Property Related\" }, " +
                        "{ \"DocType\": \"vmrind_publications\", \"DocName\": \"Publications\" }, " +
                        "{ \"DocType\": \"vmrind_vehicle\", \"DocName\": \"Vehicle Related\" }" +
                    " ] }");
            classifications = parseClassifications(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classifications;
    }
}
