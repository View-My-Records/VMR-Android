package com.vmr.network.controller;

import android.net.Uri;

import com.vmr.debug.VmrDebug;
import com.vmr.model.UploadPacket;
import com.vmr.network.controller.request.UploadTask;

/*
 * Created by abhijit on 11/17/16.
 */

public class UploadTaskController {

    private UploadTask uploadTask;

    private UploadTask.ProgressListener progressListener;
    private UploadPacket packet;

    public UploadTaskController(Uri fileUri, String parentNodeRef, UploadTask.ProgressListener progressListener) {
        this.progressListener = progressListener;
//        this.packet = new UploadPacket(fileUri, parentNodeRef);
    }

    public void uploadFile(){
        uploadTask = new UploadTask(packet, progressListener);
        uploadTask.execute();
    }

    public void cancelFileDownload() {
        uploadTask.cancel(true);
        VmrDebug.printLogI(this.getClass(), "File download canceled. " + uploadTask.getUploadPacket().getFileName());
    }
}
