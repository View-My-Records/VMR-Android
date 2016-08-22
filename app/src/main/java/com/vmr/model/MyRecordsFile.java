package com.vmr.model;

import java.util.Date;

/**
 * Created by abhijit on 8/20/16.
 */

public class MyRecordsFile {
    private String  category; //": "NORM",
    private Date    created; //": "Sat Aug 20 22:33:36 UTC 2016",
    private String  createdby; //": "674",
    private String  doctype; //": "vmrind:others",
    private Date    expiryDate; //": "Sun Aug 20 00:00:00 UTC 2017",
    private long    fileSize; //": 912,
    private boolean isfolder; //": false,
    private Date    lastUpdated; //": "Sat Aug 20 22:34:05 UTC 2016",
    private String  lastUpdatedBy; //": "674",
    private String  mimetype; //": "text/xml",
    private String  name; //": "VMR Requests.xml",
    private String  noderef; //": "workspace://SpacesStore/4268207b-bda8-45c2-8b3e-7ae1a83721d3",
    private String  owner; //": "674",
    private boolean shared; //": false

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
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

    public boolean isfolder() {
        return isfolder;
    }

    public void setIsfolder(boolean isfolder) {
        this.isfolder = isfolder;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoderef() {
        return noderef;
    }

    public void setNoderef(String noderef) {
        this.noderef = noderef;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
