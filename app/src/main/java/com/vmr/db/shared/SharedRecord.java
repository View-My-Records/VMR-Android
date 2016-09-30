package com.vmr.db.shared;

import com.vmr.app.Vmr;
import com.vmr.model.VmrSharedItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Created by abhijit on 9/8/16.
 */
public class SharedRecord {

    private int id;
    private String masterRecordOwner;
    private String nodeRef;
    private String ownerName;
    private boolean isFolder;
    private Date   recordLife;
    private String sharedToEmailId;
    private String userId;
    private String fileName;
    private String permissions;

    public SharedRecord() {

    }

    public static List<SharedRecord> getSharedRecordsList(List<VmrSharedItem> vmrSharedItems, String parentNodeRef){
        List<SharedRecord> recordList = new ArrayList<>();
        SharedRecord sharedRecord;
        for (VmrSharedItem sharedItem : vmrSharedItems) {
            sharedRecord = new SharedRecord();
            sharedRecord.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
            sharedRecord.setOwnerName(sharedItem.getOwnerName());
            sharedRecord.setIsFolder(sharedItem.isFolder());
            sharedRecord.setRecordLife(sharedItem.getRecordLife());
            sharedRecord.setSharedToEmailId(sharedItem.getSharedToEmailId());
            sharedRecord.setUserId(sharedItem.getUserId());
            sharedRecord.setFileName(sharedItem.getName());
            sharedRecord.setPermissions(sharedItem.getPermissions());
            sharedRecord.setNodeRef(sharedItem.getNodeRef());
            recordList.add(sharedRecord);
        }
        return recordList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMasterRecordOwner() {
        return masterRecordOwner;
    }

    public void setMasterRecordOwner(String masterRecordOwner) {
        this.masterRecordOwner = masterRecordOwner;
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

    public void setIsFolder(boolean folder) {
        isFolder = folder;
    }

    public Date getRecordLife() {
        return recordLife;
    }

    public void setRecordLife(Date recordLife) {
        this.recordLife = recordLife;
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

    public String getRecordName() {
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
