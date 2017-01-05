package com.vmr.service;
/*
 * Created by abhijit on 9/12/16.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.upload_queue.UploadItem;
import com.vmr.debug.VmrDebug;
import com.vmr.model.UploadPacket;
import com.vmr.network.controller.UploadController;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.UploadRequest;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

public class UploadService extends IntentService {

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<UploadItem> uploadItem;
        try {
            uploadItem = Vmr.getDbManager().getUploadQueue();
        } catch (Exception e) {
            Vmr.setDbManager(new DbManager());
            uploadItem = Vmr.getDbManager().getUploadQueue();
        }

        if(uploadItem.size() > 0 ) {
            for(final UploadItem upload : uploadItem) {
                final int notificationId = new Random().nextInt();
                UploadController uploadController = new UploadController(new UploadController.OnFileUpload() {
                    @Override
                    public void onFileUploadSuccess(JSONObject jsonObject) {
                        if (jsonObject.has("files")) {
                            Vmr.getDbManager().updateUploadSuccess(upload.getId());
//                            Toast.makeText(Vmr.getContext(), "File uploaded successfully.", Toast.LENGTH_SHORT).show();
                            Notification uploadCompleteNotification =
                                    new Notification.Builder(Vmr.getContext())
                                            .setContentTitle(upload.getFileName())
                                            .setContentText("Upload complete")
                                            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                            .setAutoCancel(true)
                                            .build();

                            NotificationManager nm = (NotificationManager)Vmr.getContext().getSystemService(NOTIFICATION_SERVICE);
                            nm.cancel(upload.getFileName(), notificationId);
                            nm.notify(notificationId, uploadCompleteNotification);
                            UploadService.this.sendBroadcast(new Intent().setAction("upload_complete"));
                        }
                    }

                    @Override
                    public void onFileUploadFailure(VolleyError error) {
//                Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(Vmr.getContext(), "File upload failed", Toast.LENGTH_SHORT).show();
                        Vmr.getDbManager().updateUploadFailure(upload.getId());
                        Notification uploadFailedNotification =
                                new Notification.Builder(Vmr.getContext())
                                        .setContentTitle(upload.getFileName())
                                        .setContentText("Upload failed")
                                        .setSmallIcon(android.R.drawable.stat_notify_error)
                                        .setAutoCancel(true)
                                        .build();

                        NotificationManager nm = (NotificationManager) Vmr.getContext().getSystemService(NOTIFICATION_SERVICE);
                        nm.cancel(upload.getFileName(), notificationId);
                        nm.notify(notificationId, uploadFailedNotification);
                    }

                    @Override
                    public void onFileUploadCancel(VolleyError error) {
//                        Toast.makeText(Vmr.getContext(), "File upload canceled", Toast.LENGTH_SHORT).show();
                        Vmr.getDbManager().updateUploadFailure(upload.getId());
                        Notification uploadFailedNotification =
                                new Notification.Builder(Vmr.getContext())
                                        .setContentTitle(upload.getFileName())
                                        .setContentText("Upload canceled")
                                        .setSmallIcon(android.R.drawable.stat_notify_error)
                                        .setAutoCancel(true)
                                        .build();

                        NotificationManager nm = (NotificationManager) Vmr.getContext().getSystemService(NOTIFICATION_SERVICE);
                        nm.cancel(upload.getFileName(), notificationId);
                        nm.notify(notificationId, uploadFailedNotification);
                    }
                });

                UploadPacket uploadPacket = null;
                try {
                    uploadPacket = new UploadPacket(upload.getFileUri(), upload.getParentNodeRef());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    VmrDebug.printLogE(UploadService.this.getClass(), "File not found.");
                }

                final Notification.Builder downloadingNotification =
                        new Notification.Builder(getApplicationContext())
                                .setContentTitle(upload.getFileName())
                                .setContentText("Uploading...")
                                .setSmallIcon(android.R.drawable.stat_sys_upload)
                                .setProgress(0,0,true)
                                .setOngoing(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    downloadingNotification.setGroup(Constants.VMR_UPLOAD_NOTIFICATION_TAG);
                }

                final NotificationManager nm = (NotificationManager) Vmr.getContext().getSystemService(NOTIFICATION_SERVICE);

                UploadRequest.UploadProgressListener progressListener = new UploadRequest.UploadProgressListener() {
                    @Override
                    public void onUploadProgress(long fileLength, long transferred, int progressPercent) {
                        if(progressPercent == 0) {
                            downloadingNotification.setProgress(0, 0, true);
                            downloadingNotification.setContentText("Uploading...");
                        } else if( progressPercent >= 100){
                            downloadingNotification.setProgress(0, 0, true);
                            downloadingNotification.setContentText("Finalizing...");
                        } else {
                            downloadingNotification.setProgress(100, progressPercent, false);
                            downloadingNotification.setContentText( progressPercent + "%");
                        }
                        nm.notify(upload.getFileName(), notificationId ,downloadingNotification.build());
                    }
                };
                try {
                    uploadController.uploadFile(uploadPacket, upload.getId(), progressListener);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    VmrDebug.printLogE(UploadService.this.getClass(), "File not found.");
                }
                Vmr.getDbManager().updateUploadStatusUploading(upload.getId());
                nm.notify(upload.getFileName(), notificationId ,downloadingNotification.build());
            }
        }
    }
}
