package com.vmr.home.controller;

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;
import com.vmr.home.request.DownloadRequest;
import com.vmr.model.SearchResult;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import java.io.File;
import java.util.Map;

/*
 * Created by abhijit on 11/17/16.
 */

public class DownloadController {

    private OnFileDownload onFileDownload;

    public DownloadController(OnFileDownload onFileDownload) {
        this.onFileDownload = onFileDownload;
    }

    public void downloadFile(Record record, DownloadRequest.DownloadProgressListener progressListener){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        },
                        progressListener
                );
        VolleySingleton.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void downloadFile(TrashRecord record, DownloadRequest.DownloadProgressListener progressListener){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        },
                        progressListener
                );
        VolleySingleton.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void downloadFile(SharedRecord record, DownloadRequest.DownloadProgressListener progressListener){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        },
                        progressListener
                );
        VolleySingleton.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void downloadFile(SearchResult record, DownloadRequest.DownloadProgressListener progressListener){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        },
                        progressListener
                );
        VolleySingleton.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public interface OnFileDownload{
        void onFileDownloadSuccess(File file);
        void onFileDownloadFailure(VolleyError error);
    }
}
