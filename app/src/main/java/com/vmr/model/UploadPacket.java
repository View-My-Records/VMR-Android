package com.vmr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhijit on 9/12/16.
 */

public class UploadPacket {

    private ArrayList<File> files =  new ArrayList<>();
    private String fileNames ;
    private String contentType;
    private String parentNodeRef;

    public UploadPacket(ArrayList<String> files, String parentNodeRef) {
        setFilePaths(files);
        setFileNames();
        setContentType();
        setParentNodeRef(parentNodeRef);
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    private void setFilePaths(ArrayList<String> filePaths) {
        for(String filePath : filePaths) this.files.add(new File(filePath));
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames() {
        this.fileNames = fileNames;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType() {
        this.contentType = contentType;
    }

    public String getParentNodeRef() {
        return parentNodeRef;
    }

    public void setParentNodeRef(String parentNodeRef) {
        this.parentNodeRef = parentNodeRef;
    }
}
