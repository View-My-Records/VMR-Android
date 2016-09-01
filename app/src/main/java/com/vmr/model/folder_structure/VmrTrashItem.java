package com.vmr.model.folder_structure;

import android.annotation.TargetApi;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class VmrTrashItem {

    private boolean isFolder;
    private String createdBy;
    private String name;
    private String owner;
    private String nodeRef;

    public VmrTrashItem(JSONObject fileJson) {
        try {
            this.setIsFolder(fileJson.getBoolean("isFolder"));
            this.setCreatedBy(fileJson.getString("createdBy"));
            this.setName(fileJson.getString("name"));
            this.setOwner(fileJson.getString("owner"));
            this.setNodeRef(fileJson.getString("noderef"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<VmrTrashItem> parseTrashItems(JSONArray jsonArray) {
        List<VmrTrashItem> trashItems = new ArrayList<>();

        if (jsonArray.length() > 0) {
            JSONObject jsonobject;
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonobject = jsonArray.getJSONObject(i);
                    VmrTrashItem trashItem = new VmrTrashItem(jsonobject);
                    trashItems.add(trashItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(trashItems, new Comparator<VmrTrashItem>() {
            @Override
            public int compare(VmrTrashItem t1, VmrTrashItem t2) {
                if (t1.isFolder())
                    return -1;
                    return 0;
            }
        });

        return trashItems;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean folder) {
        isFolder = folder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
    }
}
