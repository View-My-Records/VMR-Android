package com.vmr.db.trash;

import com.vmr.app.Vmr;
import com.vmr.model.VmrTrashItem;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 9/8/16.
 */
public class TrashRecord {

    private int id;
    private String masterRecordOwner;
    private String nodeRef;
    private String parentNodeRef;
    private boolean isFolder;
    private String createdBy;
    private String name;
    private String owner;

    public TrashRecord() {

    }

    public static List<TrashRecord> getTrashRecordList(List<VmrTrashItem> trashItems, String parentNodeRef){
        List<TrashRecord> trashRecords = new ArrayList<>();
        TrashRecord trashRecord;
        for (VmrTrashItem trashItem : trashItems) {
            trashRecord =  new TrashRecord();
            trashRecord.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
            trashRecord.setNodeRef(trashItem.getNodeRef());
            trashRecord.setParentNodeRef(parentNodeRef);
            trashRecord.setIsFolder(trashItem.isFolder());
            trashRecord.setCreatedBy(trashItem.getCreatedBy());
            trashRecord.setName(trashItem.getName());
            trashRecord.setOwner(trashItem.getOwner());
            trashRecords.add(trashRecord);
        }
        return trashRecords;
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

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean folder) {
        isFolder = folder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getRecordName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
    }

    public String getParentNodeRef() {
        return parentNodeRef;
    }

    public void setParentNodeRef(String parentNodeRef) {
        this.parentNodeRef = parentNodeRef;
    }
}
