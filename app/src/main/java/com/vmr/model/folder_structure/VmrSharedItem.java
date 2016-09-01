package com.vmr.model.folder_structure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 8/31/16.
 */
public class VmrSharedItem {
    private Date   recordLife;          //": "2016-09-01 05:00:00.0",
    private String sharedToEmailId;     //": "abhijit159@gmail.com",
    private String userId;              //": "admin",
    private String fileName;            //": "testing",
    private String permissions;         //": "View Only",
    private String nodeRef;             //": "workspace://SpacesStore/157f186c-77d1-404e-8b22-a5d3d7f32b98",
    private String sharedToName;        //": "Fname Family Admin"

    public VmrSharedItem(JSONObject fileJson) {
        try {
            this.setRecordLife(fileJson.getString("recordLife"));
            this.setSharedToEmailId(fileJson.getString("sharedToEmailId"));
            this.setUserId(fileJson.getString("userId"));
            this.setFileName(fileJson.getString("fileName"));
            this.setPermissions(fileJson.getString("permissions"));
            this.setNodeRef(fileJson.getString("nodeRef"));
            this.setSharedToName(fileJson.getString("sharedToName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<VmrSharedItem> parseSharedItems(JSONArray jsonArray) {
        List<VmrSharedItem> sharedItems = new ArrayList<>();

        if (jsonArray.length() > 0) {
            JSONObject jsonobject;
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonobject = jsonArray.getJSONObject(i);
                    VmrSharedItem sharedItem = new VmrSharedItem(jsonobject);
                    sharedItems.add(sharedItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return sharedItems;
    }

    public Date getRecordLife() {
        return recordLife;
    }

    public void setRecordLife(Date recordLife) {
        this.recordLife = recordLife;
    }

    public void setRecordLife(String recordLife) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH); //"2016-08-29 20:02:37.879"
        Date result = null;
        try {
            result = df.parse(recordLife);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.recordLife = result;
    }

    public String getSharedToEmailId() {
        return sharedToEmailId;
    }

    public void setSharedToEmailId(String sharedToEmailId) {
        this.sharedToEmailId = sharedToEmailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
    }

    public String getSharedToName() {
        return sharedToName;
    }

    public void setSharedToName(String sharedToName) {
        this.sharedToName = sharedToName;
    }
}
