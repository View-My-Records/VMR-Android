package com.vmr.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.db.upload_queue.UploadQueue;
import com.vmr.home.controller.UploadController;
import com.vmr.model.UploadPacket;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.List;
import java.util.Random;

/*
 * Created by abhijit on 9/12/16.
 */

public class UploadService extends IntentService {

    private boolean currentUpload = false;

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        List<UploadQueue> uploadQueue = Vmr.getDbManager().getUploadQueue();

        if(uploadQueue.size() > 0 ) {
            for(final UploadQueue upload : uploadQueue) {
                final int notificationId = new Random().nextInt();
                UploadController uploadController = new UploadController(new UploadController.OnFileUpload() {
                    @Override
                    public void onFileUploadSuccess(JSONObject jsonObject) {
                        if (jsonObject.has("files")) {
                            UploadService.this.currentUpload = true;
                            Vmr.getDbManager().updateUploadSuccess(upload.getId());
                            Toast.makeText(Vmr.getVMRContext(), "File uploaded successfully.", Toast.LENGTH_SHORT).show();
                            Notification uploadCompleteNotification =
                                    new NotificationCompat.Builder(Vmr.getVMRContext())
                                            .setContentTitle(upload.getFileName())
                                            .setContentText("Upload complete")
                                            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                            .setAutoCancel(true)
                                            .build();

                            NotificationManager nm = (NotificationManager)Vmr.getVMRContext().getSystemService(NOTIFICATION_SERVICE);
                            nm.cancel(upload.getFileName(), notificationId);
                            nm.notify(notificationId, uploadCompleteNotification);
                        }
                    }

                    @Override
                    public void onFileUploadFailure(VolleyError error) {
//                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                        Toast.makeText(Vmr.getVMRContext(), "File upload failed", Toast.LENGTH_SHORT).show();
                        UploadService.this.currentUpload = true;
                        Vmr.getDbManager().updateUploadFailure(upload.getId());
                        Notification uploadFailedNotification =
                                new Notification.Builder(Vmr.getVMRContext())
                                        .setContentTitle(upload.getFileName())
                                        .setContentText("Upload failed")
                                        .setSmallIcon(android.R.drawable.stat_notify_error)
                                        .setAutoCancel(true)
                                        .build();

                        NotificationManager nm = (NotificationManager) Vmr.getVMRContext().getSystemService(NOTIFICATION_SERVICE);
                        nm.cancel(upload.getFileName(), notificationId);
                        nm.notify(notificationId, uploadFailedNotification);
                    }
                });

                UploadPacket uploadPacket = new UploadPacket(upload.getFilePath(), upload.getParentNodeRef());

                uploadController.uploadFile(uploadPacket);

                Vmr.getDbManager().updateUploadStatusUploading(upload.getId());

                Notification downloadingNotification =
                        new NotificationCompat.Builder(Vmr.getVMRContext())
                                .setContentTitle(upload.getFileName())
                                .setContentText("Uploading...")
                                .setSmallIcon(android.R.drawable.stat_sys_upload)
                                .setGroup(Constants.VMR_UPLOAD_NOTIFICATION_TAG)
                                .setProgress(0,0,true)
                                .setOngoing(true)
                                .build();
                NotificationManager nm = (NotificationManager) Vmr.getVMRContext().getSystemService(NOTIFICATION_SERVICE);
                nm.notify(upload.getFileName(), notificationId ,downloadingNotification);

                while(currentUpload);
            }
        } else {
            Toast.makeText(Vmr.getVMRContext(), "Upload queue empty", Toast.LENGTH_SHORT).show();
        }

    }
}
