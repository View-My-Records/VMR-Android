package com.vmr.network.controller;

import com.android.volley.VolleyError;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrSharedItem;
import com.vmr.model.VmrTrashItem;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/23/16.
 */
public abstract class VmrResponseListener {

    public interface OnFetchRecordsListener {
        void onFetchRecordsSuccess(VmrFolder vmrFolder);
        void onFetchRecordsFailure(VolleyError error);
    }

    public interface OnFetchTrashListener {
        void onFetchTrashSuccess(List<VmrTrashItem> vmrTrashItems);
        void onFetchTrashFailure(VolleyError error);
    }

    public interface OnCreateFolderListener {
        void onCreateFolderSuccess(JSONObject jsonObject);
        void onCreateFolderFailure(VolleyError error);
    }

    public interface OnFetchSharedByMeListener{
        void onFetchSharedByMeSuccess(List<VmrSharedItem> vmrSharedItems);
        void onFetchSharedByMeFailure(VolleyError error);
    }

    public interface OnRenameItemListener {
        void onRenameItemSuccess(JSONObject jsonObject);
        void onRenameItemFailure(VolleyError error);
    }

    public interface OnMoveItemListener {
        void onMoveItemSuccess(JSONObject jsonObject);
        void onMoveItemFailure(VolleyError error);
    }

    public interface OnLinkItemListener {
        void onLinkItemSuccess(JSONObject jsonObject);
        void onLinkItemFailure(VolleyError error);
    }

    public interface OnCopyItemListener {
        void onCopyItemSuccess(JSONObject jsonObject);
        void onCopyItemFailure(VolleyError error);
    }

    public interface OnMoveToTrashListener {
        void onMoveToTrashSuccess(List<DeleteMessage> jsonObject);
        void onMoveToTrashFailure(VolleyError error);
    }

    public interface OnDeleteFromTrashListener {
        void onDeleteFromTrashSuccess(List<DeleteMessage> jsonObject);
        void onDeleteFromTrashFailure(VolleyError error);
    }

    public interface OnCheckUrlResponse {
        void onCheckUrlResponseSuccess(Integer responseCode);
        void onCheckUrlResponseFailure(VolleyError error);
    }

    public interface OnFetchTicketListener{
        void onFetchTicketSuccess(String ticket);
        void onFetchTicketFailure(VolleyError error);
    }

    public interface OnFetchClassifications{
        void onFetchClassificationsSuccess(Map<String , String > classifications);
        void onFetchClassificationsFailure(VolleyError error);
    }

    public interface OnFetchProperties{
        void onFetchPropertiesSuccess(Map<String , JSONObject> properties);
        void onFetchPropertiesFailure(VolleyError error);
    }

    public interface OnFileDownload{
        void onFileDownloadSuccess(File file);
        void onFileDownloadFailure(VolleyError error);
    }

    public interface OnFileUpload{
        void onFileUploadSuccess(JSONObject response);
        void onFileUploadFailure(VolleyError error);
    }

    public interface OnSaveIndex{
        void onSaveIndexSuccess(String  response);
        void onSaveIndexFailure(VolleyError error);
    }
}
