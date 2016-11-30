package com.vmr.db.recently_accessed;

import java.util.Date;

/*
 * Created by abhijit on 9/28/16.
 */

public class Recent {

    private long id;
    private String masterRecordOwner;
    private String nodeRef;
    private String name;
    private boolean indexed;
    private String location;
    private Date lastAccess;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMasterRecordOwner() {
        return masterRecordOwner;
    }

    public void setMasterRecordOwner(String masterRecordOwner) {
        this.masterRecordOwner = masterRecordOwner;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }
}
