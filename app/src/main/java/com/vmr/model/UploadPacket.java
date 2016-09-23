package com.vmr.model;

import java.io.File;
import java.io.FileInputStream;

/*
 * Created by abhijit on 9/12/16.
 */

public class UploadPacket {

    private String filePath;
    private String fileName ;
    private String contentType;
    private String parentNodeRef;

    public UploadPacket(String filePath, String parentNodeRef) {
        setFilePaths(filePath);
        setFileName();
        setContentType();
        setParentNodeRef(parentNodeRef);
    }

    public byte[] getFileByteArray() {
        FileInputStream fileInputStream=null;
        File file = new File(filePath);
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileBytes);
            fileInputStream.close();

            for (byte fileByte : fileBytes) {
                System.out.print((char) fileByte);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return fileBytes;
    }

    public File getFile() {
        return new File(filePath);
    }

    private void setFilePaths(String filePath) {
        this.filePath =filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName() {
        this.fileName = new File(filePath).getName();
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
