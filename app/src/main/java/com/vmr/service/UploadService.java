package com.vmr.service;

import android.app.IntentService;
import android.content.Intent;

import com.vmr.debug.VmrDebug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 9/12/16.
 */

public class UploadService extends IntentService {

    List<File> fileList =  new ArrayList<>();
    String fileNames;
    String contentType;
    String parentNodeRef;

    public UploadService(String name) {
        super(name);
    }

//    public UploadService(String name, List<File> files, String fileNames, String contentType, String parentNodeRef ) {
//        super(name);
//        this.fileList = files;
//        this.fileNames = fileNames;
//        this.contentType = contentType;
//        this.parentNodeRef =parentNodeRef;
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        VmrDebug.printLogI(this.getClass(), "File upload started.");

    }
}
