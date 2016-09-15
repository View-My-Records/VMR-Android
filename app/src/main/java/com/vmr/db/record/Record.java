package com.vmr.db.record;

/*
 * Created by abhijit on 9/3/16.
 */

import com.vmr.app.Vmr;
import com.vmr.model.VmrFile;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Record {

    // For Android
    private String recordId;
    private String masterRecordOwner;

    // Properties in vmrItem
    private String  recordNodeRef;           //  varchar(60) NOT NULL,
    private String  recordParentNodeRef;   // varchar(60) NOT NULL,
    private String  recordName;               //  varchar(100) NOT NULL,
    private String  recordDocType;          // integer NOT NULL,

    private boolean isFolder;               //  boolean NOT NULL,
    private boolean isShared;              // boolean NOT NULL,

    private String  recordOwner;             // varchar(100) NOT NULL,
    private String  createdBy;             // integer NOT NULL,
    private Date    createdDate;           // integer NOT NULL,
    private String  updatedBy;              //  integer NOT NULL,
    private Date    updatedDate;           // integer NOT NULL

    // File Specific
    private String  fileCategory;          // varchar(20) NOT NULL,
    private int     fileSize;               //  integer NOT NULL,
    private String  fileMimeType;              // TYPE varchar(100) NOT NULL,
    private Date  fileExpiryDate;           // integer NOT NULL,

    // Folder specific
    private String  folderCategory;         //  varchar(100) NOT NULL,
    private boolean isWritable;                  // integer NOT NULL,
    private boolean isDeletable;                 // integer NOT NULL,


    public Record() {

    }

    public static List<Record> getRecordList(List<VmrItem> vmrItems, String recordParentNodeRef) {
        List<Record> recordList = new ArrayList<>();
        Record record;
        for(VmrItem item: vmrItems){
            record = new Record();
            record.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
            record.setRecordNodeRef(item.getNodeRef());
            record.setRecordParentNodeRef(recordParentNodeRef);
            record.setRecordName(item.getName());
            record.setRecordDocType(item.getDocType());
            record.setIsFolder(item.isFolder());
            record.setIsShared(item.isShared());
            record.setRecordOwner(item.getOwner());
            record.setCreatedBy(item.getCreatedBy());
            record.setCreatedDate(item.getCreatedDate());
            record.setUpdatedBy(item.getLastUpdatedBy());
            record.setUpdatedDate(item.getLastUpdated());
            if(item instanceof VmrFile){
                record.setFileCategory(((VmrFile) item).getCategory());
                record.setFileSize((int) ((VmrFile) item).getFileSize());
                record.setFileMimeType(((VmrFile) item).getMimeType());
                record.setFileExpiryDate(((VmrFile) item).getExpiryDate());
            } else {
                record.setFileCategory(null);
                record.setFileSize(0);
                record.setFileMimeType(null);
                record.setFileExpiryDate(null);
            }
            if (item instanceof VmrFolder){
                record.setFolderCategory(((VmrFolder) item).getFolderCategory());
                record.setIsWritable(((VmrFolder) item).isWrite());
                record.setIsDeletable(((VmrFolder) item).isDelete());
            } else {
                record.setFolderCategory(null);
                record.setIsWritable(false);
                record.setIsDeletable(false);
            }
            recordList.add(record);
        }
        return recordList;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getMasterRecordOwner() {
        return masterRecordOwner;
    }

    public void setMasterRecordOwner(String masterRecordOwner) {
        this.masterRecordOwner = masterRecordOwner;
    }

    public String getParentNodeRef() {
        return recordParentNodeRef;
    }

    public void setRecordParentNodeRef(String recordParentNodeRef) {
        this.recordParentNodeRef = recordParentNodeRef;
    }

    public String getNodeRef() {
        return recordNodeRef;
    }

    public void setRecordNodeRef(String recordNodeRef) {
        this.recordNodeRef = recordNodeRef;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordDocType() {
        return recordDocType;
    }

    public void setRecordDocType(String recordDocType) {
        this.recordDocType = recordDocType;
    }

    public String getFolderCategory() {
        return folderCategory;
    }

    public void setFolderCategory(String folderCategory) {
        this.folderCategory = folderCategory;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public Date getFileExpiryDate() {
        return fileExpiryDate;
    }

    public void setFileExpiryDate(Date fileExpiryDate) {
        this.fileExpiryDate = fileExpiryDate;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean folder) {
        this.isFolder = folder;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setIsShared(boolean shared) {
        this.isShared = shared;
    }

    public boolean isWritable() {
        return isWritable;
    }

    public void setIsWritable(boolean writable) {
        this.isWritable = writable;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setIsDeletable(boolean deletable) {
        this.isDeletable = deletable;
    }

    public String getRecordOwner() {
        return recordOwner;
    }

    public void setRecordOwner(String recordOwner) {
        this.recordOwner = recordOwner;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
