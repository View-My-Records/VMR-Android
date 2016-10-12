package com.vmr.db.upload_queue;

/*
 * Created by abhijit on 10/11/16.
 */

import com.vmr.app.Vmr;

import java.io.File;
import java.util.Date;

public class UploadQueue {

    public static final int STATUS_PENDING      = 0;
    public static final int STATUS_UPLOADING    = 1;
    public static final int STATUS_SUCCESS      = 2;
    public static final int STATUS_FAILED       = 3;
    public static final int STATUS_ERROR        = 4;

    private int id;
    private String owner;
    private String filePath;
    private String fileName;
    private String parentNodeRef;
    private String contentType;
    private int status;
    private Date createDate;

    public UploadQueue() {

    }

    public UploadQueue(File file, String parentNodeRef) {
        this.owner = Vmr.getLoggedInUserInfo().getLoggedinUserId();
        this.filePath = file.getAbsolutePath();
        this.fileName = file.getName();
        this.parentNodeRef = parentNodeRef;
        this.contentType = "multipart/form-data";
        this.status = STATUS_PENDING;
        this.createDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParentNodeRef() {
        return parentNodeRef;
    }

    public void setParentNodeRef(String parentNodeRef) {
        this.parentNodeRef = parentNodeRef;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
