package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 * Created by abhijit on 8/20/16.
 */

public class MyRecords {

    private ArrayList<File>    indexedFiles ;
    private boolean            writeFlag;
    private String             sharedFolder;
    private ArrayList<Folder>  folders;
    private boolean            deleteFlag;
    private int                totalUnindexed;
    private ArrayList<File>    unindexedFiles;

    public ArrayList<File> getIndexedFiles() {
        return indexedFiles;
    }

    public void setIndexedFiles(ArrayList<File> indexedFiles) {
        this.indexedFiles = indexedFiles;
    }

    public void setIndexedFiles(JSONArray indexedFilesJSON) {
        this.indexedFiles = parseJSONFileArray(indexedFilesJSON);
    }

    public boolean isWriteFlag() {
        return writeFlag;
    }

    public void setWriteFlag(boolean writeFlag) {
        this.writeFlag = writeFlag;
    }

    public String getSharedFolder() {
        return sharedFolder;
    }

    public void setSharedFolder(String sharedFolder) {
        this.sharedFolder = sharedFolder;
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
    }

    public void setFolders(JSONArray foldersJSON) {
        this.folders = parseJSONFolderArray(foldersJSON);
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public int getTotalUnindexed() {
        return totalUnindexed;
    }

    public void setTotalUnindexed(int totalUnindexed) {
        this.totalUnindexed = totalUnindexed;
    }

    public ArrayList<File> getUnindexedFiles() {
        return unindexedFiles;
    }

    public void setUnindexedFiles(ArrayList<File> unindexedFiles) {
        this.unindexedFiles = unindexedFiles;
    }

    public void setUnindexedFiles(JSONArray unindexedFilesJSON) {

        this.unindexedFiles = parseJSONFileArray(unindexedFilesJSON);
    }

    private ArrayList<File> parseJSONFileArray(JSONArray indexedFilesJSON ) {

        ArrayList<File> indexedFiles = new ArrayList<>();
        JSONObject jsonobject;
        try {
            for (int i = 0; i < indexedFilesJSON.length(); i++) {
                jsonobject = indexedFilesJSON.getJSONObject(i);
                File file = new File();
                file.setShared(jsonobject.getBoolean("shared"));

                String dateString = jsonobject.getString("expiryDate");
                DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                Date result = null;
                try {
                    result = df.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                file.setExpiryDate(result);

                dateString = jsonobject.getString("lastUpdated");
                result = null;
                try {
                    result = df.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                file.setLastUpdated(result);
                file.setCreatedby(jsonobject.getString("createdby"));
                file.setIsfolder(jsonobject.getBoolean("isfolder"));
                file.setFileSize(jsonobject.getLong("fileSize"));
                file.setCategory(jsonobject.getString("category"));
                file.setMimetype(jsonobject.getString("mimetype"));

                dateString = jsonobject.getString("created");
                result = null;
                try {
                    result = df.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                file.setCreated(result);
                file.setName(jsonobject.getString("name"));
                file.setLastUpdatedBy(jsonobject.getString("lastUpdatedBy"));
                file.setOwner(jsonobject.getString("owner"));
                file.setNoderef(jsonobject.getString("noderef"));
                file.setDoctype(jsonobject.getString("doctype"));

                indexedFiles.add(file);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return indexedFiles;
    }

    private ArrayList<Folder> parseJSONFolderArray(JSONArray foldersJSON) {

        ArrayList<Folder> indexedFiles = new ArrayList<>();
        JSONObject jsonobject;
        try {
            for (int i = 0; i < foldersJSON.length(); i++) {
                jsonobject = foldersJSON.getJSONObject(i);
                Folder folder = new Folder();
                DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                String dateString;
                Date result = null;

                folder.setShared(jsonobject.getBoolean("shared"));
                {
                    dateString = jsonobject.getString("lastUpdated");
                    try {
                        result = df.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                folder.setLastUpdated(result);
                folder.setCreatedby(jsonobject.getString("createdby"));
                folder.setFolderCategory(jsonobject.getString("folderCategory"));
                folder.setIsfolder(jsonobject.getBoolean("isfolder"));
                {
                    dateString = jsonobject.getString("created");
                    result = null;
                    try {
                        result = df.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                folder.setCreated(result);
                folder.setName(jsonobject.getString("name"));
                folder.setLastUpdatedBy(jsonobject.getString("lastUpdatedBy"));
                folder.setDelete(jsonobject.getBoolean("delete"));
                folder.setWrite(jsonobject.getBoolean("write"));
                folder.setOwner(jsonobject.getString("owner"));
                folder.setNoderef(jsonobject.getString("noderef"));
                folder.setFolderName(jsonobject.getString("folderName"));
                folder.setDoctype(jsonobject.getString("doctype"));

                indexedFiles.add(folder);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return indexedFiles;
    }


    public class File {
        private String  category; //": "NORM",
        private Date    created; //": "Sat Aug 20 22:33:36 UTC 2016",
        private String  createdby; //": "674",
        private String  doctype; //": "vmrind:others",
        private Date    expiryDate; //": "Sun Aug 20 00:00:00 UTC 2017",
        private boolean isfolder; //": false,
        private Date    lastUpdated; //": "Sat Aug 20 22:34:05 UTC 2016",
        private String  lastUpdatedBy; //": "674",
        private String  mimetype; //": "text/xml",
        private String  name; //": "VMR Requests.xml",
        private String  noderef; //": "workspace://SpacesStore/4268207b-bda8-45c2-8b3e-7ae1a83721d3",
        private String  owner; //": "674",
        private boolean shared; //": false

        private long    fileSize; //": 912,

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

    public class Folder {

        private Date    created ;           //": "Mon Aug 08 02:05:22 UTC 2016",
        private String  createdby ;       //": "admin",
        private boolean delete ;         //": true,
        private String  doctype ;         //": "folder",
        private String  folderCategory ;  //": "system",
        private String  folderName ;      //": "Programs",
        private boolean isfolder ;       //": true,
        private Date    lastUpdated ;       //": "Mon Aug 08 02:05:23 UTC 2016",
        private String  lastUpdatedBy ;   //": "admin",
        private String  name ;            //": "Programs",
        private String  noderef ;         //": "workspace://SpacesStore/582876a4-adc4-4f42-99a1-f8da859705b8",
        private String  owner ;           //": "admin",
        private boolean shared ;         //": false,
        private boolean write ;          //": true

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

        public boolean isDelete() {
            return delete;
        }

        public void setDelete(boolean delete) {
            this.delete = delete;
        }

        public String getDoctype() {
            return doctype;
        }

        public void setDoctype(String doctype) {
            this.doctype = doctype;
        }

        public String getFolderCategory() {
            return folderCategory;
        }

        public void setFolderCategory(String folderCategory) {
            this.folderCategory = folderCategory;
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
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

        public boolean isWrite() {
            return write;
        }

        public void setWrite(boolean write) {
            this.write = write;
        }
    }

}
