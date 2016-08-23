package com.vmr.model.folder_structure;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Created by abhijit on 8/20/16.
 */

public class VmrFile extends VmrNode {

    private String  category; //": "NORM",
    private Date    expiryDate; //": "Sun Aug 20 00:00:00 UTC 2017",
    private long    fileSize; //": 912,
    private String  mimeType; //": "text/xml",

    public VmrFile(JSONObject fileJson) {
        try {
            String dateString = fileJson.getString("expiryDate");
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date result = null;
            try {
                result = df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.setExpiryDate(result);

            dateString = fileJson.getString("lastUpdated");
            result = null;
            try {
                result = df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.setLastUpdated(result);
            this.setCreatedBy(fileJson.getString("createdby"));
            this.setIsFolder(fileJson.getBoolean("isfolder"));
            this.setFileSize(fileJson.getLong("fileSize"));
            this.setCategory(fileJson.has("category")? fileJson.getString("category") : "NA");
            this.setMimeType(fileJson.getString("mimetype"));

            dateString = fileJson.getString("created");
            result = null;
            try {
                result = df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.setCreated(result);
            this.setName(fileJson.getString("name"));
            this.setLastUpdatedBy(fileJson.getString("lastUpdatedBy"));
            this.setOwner(fileJson.getString("owner"));
            this.setNodeRef(fileJson.getString("noderef"));
            this.setDocType(fileJson.getString("doctype"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
