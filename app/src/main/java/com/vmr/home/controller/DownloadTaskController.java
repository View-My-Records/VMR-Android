package com.vmr.home.controller;

import com.android.volley.VolleyError;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.request.DownloadTask;
import com.vmr.model.DownloadPacket;

import java.io.File;

/*
 * Created by abhijit on 11/17/16.
 */

public class DownloadTaskController {

    private DownloadTask downloadTask;

    private DownloadTask.DownloadProgressListener progressListener;
    private DownloadPacket downloadPacket;

    public DownloadTaskController(Record record, DownloadTask.DownloadProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloadPacket = new DownloadPacket(record);
//        downloadFile(new DownloadPacket(record), progressListener);
    }

    public void downloadFile(){
        downloadTask = new DownloadTask(downloadPacket, progressListener);
        downloadTask.execute();
    }

//    public void downloadFile(TrashRecord record, DownloadRequest.DownloadProgressListener progressListener){
//
//        Map<String, String> formData = Vmr.getUserMap();
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");
//
//        DownloadRequest downloadRequest =
//                new DownloadRequest(
//                        formData,
//                        new Response.Listener<File>() {
//                            @Override
//                            public void onResponse(File file) {
//                                onFileDownload.onFileDownloadSuccess(file);
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                onFileDownload.onFileDownloadFailure(error);
//                            }
//                        },
//                        progressListener
//                );
//        VmrRequestQueue.getInstance()
//                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
//    }
//
//    public void downloadFile(SharedRecord record, DownloadRequest.DownloadProgressListener progressListener){
//
//        Map<String, String> formData = Vmr.getUserMap();
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");
//
//        DownloadRequest downloadRequest =
//                new DownloadRequest(
//                        formData,
//                        new Response.Listener<File>() {
//                            @Override
//                            public void onResponse(File file) {
//                                onFileDownload.onFileDownloadSuccess(file);
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                onFileDownload.onFileDownloadFailure(error);
//                            }
//                        },
//                        progressListener
//                );
//        VmrRequestQueue.getInstance()
//                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
//    }
//
//    public void downloadFile(SearchResult record, DownloadRequest.DownloadProgressListener progressListener){
//
//        Map<String, String> formData = Vmr.getUserMap();
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
//        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");
//
//        DownloadRequest downloadRequest =
//                new DownloadRequest(
//                        formData,
//                        new Response.Listener<File>() {
//                            @Override
//                            public void onResponse(File file) {
//                                onFileDownload.onFileDownloadSuccess(file);
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                onFileDownload.onFileDownloadFailure(error);
//                            }
//                        },
//                        progressListener
//                );
//        VmrRequestQueue.getInstance()
//                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
//    }

    public void cancelFileDownload() {
        downloadTask.cancel(true);
        VmrDebug.printLogI(this.getClass(), "File download canceled. " + downloadTask.getDownloadPacket().getFileName());
    }

    public interface OnFileDownload{
        void onFileDownloadStart();
        void onFileDownloadSuccess(File file);
        void onFileDownloadFailure(VolleyError error);
        void onFileDownloadCancel(File file);
        void onFileDownloadProgress(File file);
    }


}
