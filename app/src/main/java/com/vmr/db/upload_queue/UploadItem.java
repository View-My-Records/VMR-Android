package com.vmr.db.upload_queue;

/*
 * Created by abhijit on 10/11/16.
 */

import android.net.Uri;

import com.vmr.app.Vmr;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.io.FileNotFoundException;
import java.util.Date;

public class UploadItem {

    public static final int STATUS_PENDING      = 0;
    public static final int STATUS_UPLOADING    = 1;
    public static final int STATUS_SUCCESS      = 2;
    public static final int STATUS_FAILED       = 3;
    public static final int STATUS_ERROR        = 4;

    private int id;
    private String owner;
    private String fileUri;
    private String fileName;
    private String parentNodeRef;
    private String contentType;
    private int status;
    private Date createDate;

    public UploadItem() {
    }

    public UploadItem(Uri fileUri, String parentNodeRef) throws FileNotFoundException {
        this.owner = PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID);
        this.fileUri = fileUri.toString();
        this.fileName = FileUtils.getFileName(Vmr.getContext(), fileUri);
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

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
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
