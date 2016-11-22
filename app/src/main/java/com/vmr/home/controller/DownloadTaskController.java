package com.vmr.home.controller;

import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.request.DownloadTask;
import com.vmr.model.DownloadPacket;

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
    }

    public DownloadTaskController(TrashRecord record, DownloadTask.DownloadProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloadPacket = new DownloadPacket(record);
    }

    public DownloadTaskController(SharedRecord record, DownloadTask.DownloadProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloadPacket = new DownloadPacket(record);
    }

    public void downloadFile(){
        downloadTask = new DownloadTask(downloadPacket, progressListener);
        downloadTask.execute();
    }


    public void cancelFileDownload() {
        downloadTask.cancel(true);
        VmrDebug.printLogI(this.getClass(), "File download canceled. " + downloadTask.getDownloadPacket().getFileName());
    }
}
