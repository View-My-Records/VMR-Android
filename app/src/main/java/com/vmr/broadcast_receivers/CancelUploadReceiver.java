package com.vmr.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.vmr.debug.VmrDebug;
import com.vmr.network.controller.request.Constants;

/*
 * Created by abhijit on 10/11/16.
 */

public class CancelUploadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int uploadTag = intent.getIntExtra(Constants.Request.FolderNavigation.UploadFile.TAG, 0);

        Intent cancelIntent = new Intent();
        cancelIntent.setAction("CancelUpload");
//        Intent cancelIntent = new Intent(Constants.Request.FolderNavigation.UploadFile.TAG);
        cancelIntent.putExtra(Constants.Request.FolderNavigation.UploadFile.TAG, uploadTag);
        LocalBroadcastManager.getInstance(context).sendBroadcast(cancelIntent);
//        Vmr.getContext().sendBroadcast(cancelIntent);
        VmrDebug.printLogI(this.getClass(), "Upload Canceled ->" +  uploadTag);
    }
}
