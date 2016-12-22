package com.vmr.network.controller;

import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.model.DownloadPacket;
import com.vmr.network.controller.request.DownloadTask;

/*
 * Created by abhijit on 11/17/16.
 */

public class DownloadTaskController {

    private DownloadTask downloadTask;

    private DownloadTask.ProgressListener progressListener;
    private DownloadPacket downloadPacket;

    public DownloadTaskController(Record record, DownloadTask.ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloadPacket = new DownloadPacket(record);
    }

    public DownloadTaskController(TrashRecord record, DownloadTask.ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloadPacket = new DownloadPacket(record);
    }

    public DownloadTaskController(SharedRecord record, DownloadTask.ProgressListener progressListener) {
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
