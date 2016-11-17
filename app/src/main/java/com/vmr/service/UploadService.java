package com.vmr.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.broadcast_receivers.CancelUploadReceiver;
import com.vmr.db.upload_queue.UploadQueue;
import com.vmr.home.controller.UploadController;
import com.vmr.home.request.UploadRequest;
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
                                    new Notification.Builder(Vmr.getVMRContext())
                                            .setContentTitle(upload.getFileName())
                                            .setContentText("Upload complete")
                                            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                            .setAutoCancel(true)
                                            .build();

                            NotificationManager nm = (NotificationManager)Vmr.getVMRContext().getSystemService(NOTIFICATION_SERVICE);
                            nm.cancel(upload.getFileName(), notificationId);
                            nm.notify(notificationId, uploadCompleteNotification);
                            UploadService.this.sendBroadcast(new Intent().setAction("upload_complete"));
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

                    @Override
                    public void onFileUploadCancel(VolleyError error) {
                        Toast.makeText(Vmr.getVMRContext(), "File upload canceled", Toast.LENGTH_SHORT).show();
                        UploadService.this.currentUpload = true;
                        Vmr.getDbManager().updateUploadFailure(upload.getId());
                        Notification uploadFailedNotification =
                                new Notification.Builder(Vmr.getVMRContext())
                                        .setContentTitle(upload.getFileName())
                                        .setContentText("Upload canceled")
                                        .setSmallIcon(android.R.drawable.stat_notify_error)
                                        .setAutoCancel(true)
                                        .build();

                        NotificationManager nm = (NotificationManager) Vmr.getVMRContext().getSystemService(NOTIFICATION_SERVICE);
                        nm.cancel(upload.getFileName(), notificationId);
                        nm.notify(notificationId, uploadFailedNotification);
                    }
                });

                UploadPacket uploadPacket = new UploadPacket(upload.getFilePath(), upload.getParentNodeRef());

                Intent cancelUploadIntent = new Intent(this, CancelUploadReceiver.class);
                cancelUploadIntent.putExtra(Constants.Request.FolderNavigation.UploadFile.TAG, upload.getId());
                PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, cancelUploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                final Notification.Builder downloadingNotification =
                        new Notification.Builder(Vmr.getVMRContext())
                                .setContentTitle(upload.getFileName())
                                .setContentText("Uploading...")
                                .setSmallIcon(android.R.drawable.stat_sys_upload)

                                .setProgress(0,0,true)
                                .setOngoing(true)
                                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                                            getApplicationContext().getResources().getString(android.R.string.cancel),
                                            pIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    downloadingNotification.setGroup(Constants.VMR_UPLOAD_NOTIFICATION_TAG);
                }
                final NotificationManager nm = (NotificationManager) Vmr.getVMRContext().getSystemService(NOTIFICATION_SERVICE);

                UploadRequest.UploadProgressListener progressListener = new UploadRequest.UploadProgressListener() {
                    @Override
                    public void onUploadProgress(long fileLength, long transferred, int progressPercent) {
                        if(progressPercent == 0) {
                            downloadingNotification.setProgress(0, 0, true);
                            downloadingNotification.setContentText("Uploading...");
                        } else if( progressPercent == 100){
                            downloadingNotification.setProgress(0, 0, true);
                            downloadingNotification.setContentText("Finalizing...");
                        } else {
                            downloadingNotification.setProgress(100, progressPercent, false);
                            downloadingNotification.setContentText( progressPercent + "%");
                        }
                        nm.notify(upload.getFileName(), notificationId ,downloadingNotification.build());
                    }
                };
                uploadController.uploadFile(uploadPacket, upload.getId(), progressListener);
                Vmr.getDbManager().updateUploadStatusUploading(upload.getId());
                nm.notify(upload.getFileName(), notificationId ,downloadingNotification.build());
            }
        } else {
            Toast.makeText(Vmr.getVMRContext(), "Upload queue empty", Toast.LENGTH_SHORT).show();
        }

    }
}
