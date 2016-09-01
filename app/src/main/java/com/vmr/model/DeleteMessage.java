package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 9/1/16.
 */

public class DeleteMessage {

    private String status  ;        //":"success",
    private String name;            //":"folder3",
    private String errorType;    //":"Successfully Deleted item(s)",
    private String objectType;        //":"Folder"

    public DeleteMessage(JSONObject deleteMessageJson) {
        try {
            this.setStatus(deleteMessageJson.getString("status"));
            this.setName(deleteMessageJson.getString("name"));
            this.setErrorType(deleteMessageJson.getString("errortype"));
            this.setObjectType(deleteMessageJson.getString("objtype"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<DeleteMessage> parseDeleteMessage(JSONObject response) {
        List<DeleteMessage> deleteMessages = new ArrayList<>();

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) response.get("Deletemessages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray.length() > 0) {
            JSONObject jsonobject;
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonobject = jsonArray.getJSONObject(i);
                    DeleteMessage deleteMessage = new DeleteMessage(jsonobject);
                    deleteMessages.add(deleteMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return deleteMessages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
