package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 8/20/16.
 */

public class VmrFolder extends VmrItem {

    List<VmrFolder>     folders = new ArrayList<>();
    List<VmrFile>       indexedFiles = new ArrayList<>();
    List<VmrFile>       unIndexedFiles = new ArrayList<>();

    private boolean     delete ;         //": true,
    private String      folderCategory ;  //": "system",
    private String      folderName ;      //": "Programs",
    private boolean     write ;          //": true

    private boolean     writeFlag;
    private String      sharedFolder;
    private boolean     deleteFlag;
    private int         totalUnIndexed;


    public VmrFolder(JSONObject folderJson) {
        try {
            this.setIndexedFilesFromJSON(folderJson.has("indexedFiles")? folderJson.getJSONArray("indexedFiles") : null);
            this.setWriteFlag(folderJson.has("writeFlag") && folderJson.getBoolean("writeFlag"));
            this.setWriteFlag(folderJson.has("write") && folderJson.getBoolean("write"));
            this.setSharedFolder(folderJson.has("sharedFolder")? folderJson.getString("sharedFolder") : "");
            this.setFoldersFromJSON(folderJson.has("folders") ? folderJson.getJSONArray("folders") : null);
            this.setDeleteFlag(folderJson.has("deleteFlag") && folderJson.getBoolean("deleteFlag"));
            this.setDeleteFlag(folderJson.has("delete") && folderJson.getBoolean("delete"));
            this.setTotalUnIndexed(folderJson.has("totalUnindexed")? folderJson.getInt("totalUnindexed") : 0);
            this.setUnIndexedFilesFromJSON(folderJson.has("unindexedFiles") ? folderJson.getJSONArray("unindexedFiles") : null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addFolder(VmrFolder folder){
        folder.setParent(this);
        this.folders.add(folder);
    }

    public void addIndexedFile(VmrFile file){
        file.setParent(this);
        this.indexedFiles.add(file);
    }

    public void addUnIndexedFile(VmrFile file){
        file.setParent(this);
        this.unIndexedFiles.add(file);
    }

    public List<VmrItem> getAll(){
        List<VmrItem> list = new ArrayList<>();
        list.addAll(folders);
        list.addAll(indexedFiles);
        list.addAll(unIndexedFiles);
        return list;
    }

    public List<VmrItem> getAllUnindexed(){
        List<VmrItem> list = new ArrayList<>();
        list.addAll(folders);
        list.addAll(unIndexedFiles);
        return list;
    }

    public List<VmrFolder> getFolders() {
        return folders;
    }

    public void setFolders(List<VmrFolder> folders) {
        this.folders = new ArrayList<>();
        for (int i = 0; i < folders.size(); i++) {
            folders.get(i).setParent(this);
            this.folders.add(folders.get(i));
        }
    }

    public void setFoldersFromJSON(JSONArray folders) {
        if (folders != null && folders.length() > 0) {
            JSONObject jsonobject;
            try {
                for (int i = 0; i < folders.length(); i++) {
                    jsonobject = folders.getJSONObject(i);
                    VmrFolder folder = new VmrFolder(jsonobject);
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
                    folder.setCreatedBy(jsonobject.getString("createdby"));
                    folder.setFolderCategory(jsonobject.getString("folderCategory"));
                    folder.setIsFolder(jsonobject.getBoolean("isfolder"));
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
                    folder.setNodeRef(jsonobject.getString("noderef"));
                    folder.setFolderName(jsonobject.getString("folderName"));
                    folder.setDocType(jsonobject.getString("doctype"));

                    this.addFolder(folder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<VmrFile> getIndexedFiles() {
        return indexedFiles;
    }

    public void setIndexedFiles(List<VmrFile> indexedFiles) {

        this.indexedFiles = new ArrayList<>();
        for (int i = 0; i < indexedFiles.size(); i++) {
            indexedFiles.get(i).setParent(this);
            this.indexedFiles.add(indexedFiles.get(i));
        }
    }

    public void setIndexedFilesFromJSON(JSONArray indexedFiles) {
        if (indexedFiles != null && indexedFiles.length() > 0) {
            JSONObject jsonobject;
            try {
                for (int i = 0; i < indexedFiles.length(); i++) {
                    jsonobject = indexedFiles.getJSONObject(i);
                    VmrFile file = new VmrFile(jsonobject);
                    this.addIndexedFile(file);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<VmrFile> getUnIndexedFiles() {
        return unIndexedFiles;
    }

    public void setUnIndexedFiles(List<VmrFile> unIndexedFiles) {
        this.unIndexedFiles = new ArrayList<>();
        for (int i = 0; i < unIndexedFiles.size(); i++) {
            unIndexedFiles.get(i).setParent(this);
            this.unIndexedFiles.add(unIndexedFiles.get(i));
        }
    }

    public void setUnIndexedFilesFromJSON(JSONArray unIndexedFiles) {
        if (unIndexedFiles != null && unIndexedFiles.length() > 0) {
            JSONObject jsonobject;
            try {
                for (int i = 0; i < unIndexedFiles.length(); i++) {
                    jsonobject = unIndexedFiles.getJSONObject(i);
                    VmrFile file = new VmrFile(jsonobject);
                    this.addUnIndexedFile(file);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
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

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
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

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public int getTotalUnIndexed() {
        return totalUnIndexed;
    }

    public void setTotalUnIndexed(int totalUnIndexed) {
        this.totalUnIndexed = totalUnIndexed;
    }
}
