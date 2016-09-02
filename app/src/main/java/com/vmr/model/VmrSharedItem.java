package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 8/31/16.
 */
public class VmrSharedItem {

    private String ownerName;           //":"Internal1 member of malya",
    private boolean isFolder;           //":true,
    private Date   recordLife;          //": "Sep 30, 2016 11:00:00 AM",
    private String sharedToEmailId;     //": "abhijit159@gmail.com",
    private String userId;              //": "admin",
    private String fileName;            //": "testing",
    private String permissions;         //": "View Only",
    private String nodeRef;             //": "workspace://SpacesStore/157f186c-77d1-404e-8b22-a5d3d7f32b98",

    public VmrSharedItem(JSONObject fileJson) throws JSONException, ParseException {
            this.setOwnerName(fileJson.has("ownerName") ? fileJson.getString("recordLife") : null );
            this.setFolder(fileJson.has("isFolder") && fileJson.getBoolean("isFolder"));
            this.setRecordLife(fileJson.has("recordLife") ? fileJson.getString("recordLife") : null);
            this.setSharedToEmailId(fileJson.has("sharedToEmailId") ? fileJson.getString("sharedToEmailId") : null );
            this.setUserId(fileJson.has("userId") ? fileJson.getString("userId") : null );
            this.setFileName(fileJson.has("name") ? fileJson.getString("name") : null );
            this.setPermissions(fileJson.has("permissions") ? fileJson.getString("permissions") : null );
            this.setNodeRef(fileJson.has("noderef") ? fileJson.getString("noderef") : null );
    }

    public static List<VmrSharedItem> parseSharedItems(JSONArray jsonArray) throws ParseException, JSONException{
        List<VmrSharedItem> sharedItems = new ArrayList<>();

        if (jsonArray.length() > 0) {
            JSONObject jsonobject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonobject = jsonArray.getJSONObject(i);
                VmrSharedItem sharedItem = new VmrSharedItem(jsonobject);
                sharedItems.add(sharedItem);
            }
        }

        return sharedItems;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public Date getRecordLife() {
        return recordLife;
    }

    public void setRecordLife(Date recordLife) throws JSONException {
        this.recordLife = recordLife;
    }

    public void setRecordLife(String recordLife) throws JSONException, ParseException {
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.ENGLISH); // "Sep 30, 2016 11:00:00 AM"
        this.recordLife = df.parse(recordLife);
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

    public String getName() {
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

}
