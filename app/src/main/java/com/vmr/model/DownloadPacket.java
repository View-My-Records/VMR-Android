package com.vmr.model;

import android.net.Uri;

import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;

/*
 * Created by abhijit on 9/12/16.
 */

public class DownloadPacket {

    private String fileSelectedNodeRef;
    private String fileName ;
    private long fileLength;
    private String mimeType;

    public DownloadPacket(Record record) {
        setFileSelectedNodeRef(record.getNodeRef());
        setFileName(record.getRecordName());
        setFileLength(record.getFileSize());
        setMimeType(Uri.encode("application/octet-stream"));
    }

    public DownloadPacket(TrashRecord record) {
        setFileSelectedNodeRef(record.getNodeRef());
        setFileName(record.getRecordName());
        setFileLength(0);
        setMimeType(Uri.encode("application/octet-stream"));
    }

    public DownloadPacket(SharedRecord record) {
        setFileSelectedNodeRef(record.getNodeRef());
        setFileName(record.getRecordName());
        setFileLength(0);
        setMimeType(Uri.encode("application/octet-stream"));
    }

    public String getFileSelectedNodeRef() {
        return fileSelectedNodeRef;
    }

    public void setFileSelectedNodeRef(String fileSelectedNodeRef) {
        this.fileSelectedNodeRef = fileSelectedNodeRef;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
