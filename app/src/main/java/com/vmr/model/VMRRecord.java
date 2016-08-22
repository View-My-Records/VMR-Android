package com.vmr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.vmr.utils.WebApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by abhijit on 8/17/16.
 */

public class VMRRecord implements Parcelable{
    private String shared;
    private String lastUpdated;
    private String createdby;
    private String folderCategory;
    private String isfolder;
    private String created;
    private String name;
    private String lastUpdatedBy;
    private String delete;
    private String write;
    private String owner;
    private String noderef;
    private String folderName;
    private String doctype;

    public VMRRecord() {

    }

    public VMRRecord(JSONObject jsonObject) {
        VMRRecord rec = new VMRRecord();
        try {
            rec.setShared(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_SHARED));
            rec.setLastUpdated(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_LASTUPDATED));
            rec.setCreatedby(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_CREATEDBY));
            rec.setFolderCategory(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_FOLDERCATEGORY));
            rec.setIsfolder(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_ISFOLDER));
            rec.setCreated(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_CREATED));
            rec.setName(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_NAME));
            rec.setLastUpdatedBy(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_LASTUPDATEDBY));
            rec.setDeleteable(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_DELETE));
            rec.setWriteable(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_WRITE));
            rec.setOwner(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_OWNER));
            rec.setNoderef(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_NODEREF));
            rec.setFolderName(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_FOLDERNAME));
            rec.setDoctype(jsonObject.getString(WebApiConstants.JSON_FILE_LIST_RECORD_DOCTYPE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected VMRRecord(Parcel in) {
        shared = in.readString();
        lastUpdated = in.readString();
        createdby = in.readString();
        folderCategory = in.readString();
        isfolder = in.readString();
        created = in.readString();
        name = in.readString();
        lastUpdatedBy = in.readString();
        delete = in.readString();
        write = in.readString();
        owner = in.readString();
        noderef = in.readString();
        folderName = in.readString();
        doctype = in.readString();
    }

    public static final Creator<VMRRecord> CREATOR = new Creator<VMRRecord>() {
        @Override
        public VMRRecord createFromParcel(Parcel in) {
            return new VMRRecord(in);
        }

        @Override
        public VMRRecord[] newArray(int size) {
            return new VMRRecord[size];
        }
    };

    public String isShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getFolderCategory() {
        return folderCategory;
    }

    public void setFolderCategory(String folderCategory) {
        this.folderCategory = folderCategory;
    }

    public String isfolder() {
        return isfolder;
    }

    public void setIsfolder(String isfolder) {
        this.isfolder = isfolder;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String isDeletable() {
        return delete;
    }

    public void setDeleteable(String delete) {
        this.delete = delete;
    }

    public String isWriteable() {
        return write;
    }

    public void setWriteable(String write) {
        this.write = write;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNoderef() {
        return noderef;
    }

    public void setNoderef(String noderef) {
        this.noderef = noderef;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(shared);
        parcel.writeString(lastUpdated);
        parcel.writeString(createdby);
        parcel.writeString(folderCategory);
        parcel.writeString(isfolder);
        parcel.writeString(created);
        parcel.writeString(name);
        parcel.writeString(lastUpdatedBy);
        parcel.writeString(delete);
        parcel.writeString(write);
        parcel.writeString(owner);
        parcel.writeString(noderef);
        parcel.writeString(folderName);
        parcel.writeString(doctype);
    }
}
