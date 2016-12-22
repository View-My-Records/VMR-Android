package com.vmr.model;

import android.net.Uri;

import com.vmr.app.Vmr;
import com.vmr.utils.ContentUtil;

import java.io.FileNotFoundException;

/*
 * Created by abhijit on 9/12/16.
 */

public class UploadPacket {

    private Uri fileUri;
    private String fileName ;
    private String contentType;
    private String parentNodeRef;

    public UploadPacket(String fileUri, String parentNodeRef) throws FileNotFoundException {
        setFileUri(Uri.parse(fileUri));
//        setFilePaths(fileUri.getPath());
        setFileName();
        setContentType();
        setParentNodeRef(parentNodeRef);
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName() throws FileNotFoundException {
        this.fileName = ContentUtil.getFileName(Vmr.getContext(), getFileUri());
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType() {
        this.contentType = "multipart/form-data";
    }

    public String getParentNodeRef() {
        return parentNodeRef;
    }

    public void setParentNodeRef(String parentNodeRef) {
        this.parentNodeRef = parentNodeRef;
    }
}
