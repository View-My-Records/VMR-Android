package com.vmr.home.fragments;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.context_menu.SharedWithMeOptionsMenu;
import com.vmr.home.controller.DownloadTaskController;
import com.vmr.home.controller.HomeController;
import com.vmr.home.request.DownloadTask;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFolder;
import com.vmr.network.VolleySingleton;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentSharedWithMe extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        SharedWithMeOptionsMenu.OnOptionClickListener {

    private boolean DEBUG = true;

    // Fragment interaction listener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private SharedWithMeOptionsMenu recordOptionsMenu;

    // Controllers
    private HomeController homeController;
    private DbManager dbManager;

    // Variables
    private List<Record> records = new ArrayList<>();
    private RecordsAdapter recordsAdapter;

    // Stack
    private Stack<String> recordStack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        homeController = new HomeController(this);
        recordsAdapter = new RecordsAdapter(records, this, this);

        recordOptionsMenu = new SharedWithMeOptionsMenu();
        recordOptionsMenu.setOptionClickListener(this);

        dbManager = Vmr.getDbManager();

        recordStack = new Stack<>();
        recordStack.push(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_SHARED_NODE_REF));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.SHARED_WITH_ME);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_shared_with_me, container, false);

        setupRecyclerView(fragmentView);
        setOnBackPress(fragmentView);

        mSwipeRefreshLayout.setRefreshing(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeController.fetchAllFilesAndFolders(recordStack.peek());
        records = dbManager.getAllSharedWithMeRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
      if (DEBUG) VmrDebug.printLogI(this.getClass(), "Records retrieved.");

        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));
        records = dbManager.getAllSharedWithMeRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);

        mSwipeRefreshLayout.setRefreshing(false);

        if(records.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(final Record record) {
        if(record.isFolder()){
            if (DEBUG) VmrDebug.printLine(record.getRecordName() + " Folder clicked");
            recordStack.push(record.getNodeRef());
            homeController.fetchAllFilesAndFolders(recordStack.peek());
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
          if (DEBUG) VmrDebug.printLine(record.getRecordName() + " File clicked");
            if (DEBUG) VmrDebug.printLogI(record.getRecordName() + " File clicked");
            dbManager.addNewRecent(record);
            if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                final DownloadTaskController downloadTaskController;

                final ProgressDialog downloadProgress = new ProgressDialog(getActivity());
                downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downloadProgress.setMessage("Downloading " + record.getRecordName());
                downloadProgress.setCancelable(true);
                downloadProgress.setCanceledOnTouchOutside(true);
                downloadProgress.setMax(100);
                downloadProgress.setIndeterminate(true);

                DownloadTask.DownloadProgressListener progressListener
                        = new DownloadTask.DownloadProgressListener() {
                    @Override
                    public void onDownloadStarted() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgress.setIndeterminate(false);
                                downloadProgress.setMessage("Downloading " + record.getRecordName());
                            }
                        });
                    }

                    @Override
                    public void onDownloadFailed() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgress.setMessage("Downloading failed");
                            }
                        });
                    }

                    @Override
                    public void onDownloadCanceled() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgress.setMessage("Downloading canceled");
                            }
                        });
                    }

                    @Override
                    public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                        downloadProgress.setProgress(progressPercent);
                    }

                    @Override
                    public void onDownloadFinish(File file) {
                        downloadProgress.dismiss();
                        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                        openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri fileUri = Uri.fromFile(file);
                        openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(file.getAbsolutePath()));
                        try {
                            startActivity(openFileIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getActivity(), "No application to view this file", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                downloadTaskController = new DownloadTaskController(record, progressListener);

                downloadProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getResources().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadTaskController.cancelFileDownload();
                            }
                        });

                downloadProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        downloadTaskController.cancelFileDownload();
                    }
                });

                downloadProgress.show();
                downloadTaskController.downloadFile();
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PermissionHandler.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onItemOptionsClick(Record record, View view) {
        if (DEBUG) VmrDebug.printLine(record.getRecordName() + " Options clicked");
        recordOptionsMenu.setRecord(record);
        recordOptionsMenu.show(getActivity().getSupportFragmentManager(), recordOptionsMenu.getTag());
    }

    @Override
    public void onOpenClicked(final Record record) {
        if (DEBUG) VmrDebug.printLogI(this.getClass(), "Open button clicked" );
        if(record.isFolder()){
          if (DEBUG) VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder opened");
            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
            recordStack.push(record.getNodeRef());
            refreshFolder();
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
          if (DEBUG) VmrDebug.printLogI(record.getRecordName() + " File clicked");
            dbManager.addNewRecent(record);
            if (PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                final DownloadTaskController downloadTaskController;

                final ProgressDialog downloadProgress = new ProgressDialog(getActivity());
                downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // downloadProgress.setMessage("Downloading " + record.getRecordName());
                downloadProgress.setCancelable(true);
                downloadProgress.setCanceledOnTouchOutside(true);
                downloadProgress.setMax(100);
                downloadProgress.setMessage("Starting download...");
                downloadProgress.setIndeterminate(true);

                final File[] downloadedFile = new File[1];

                DownloadTask.DownloadProgressListener progressListener
                        = new DownloadTask.DownloadProgressListener() {
                    @Override
                    public void onDownloadStarted() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgress.setIndeterminate(false);
                                downloadProgress.setMessage("Downloading " + record.getRecordName());
                            }
                        });
                    }

                    @Override
                    public void onDownloadFailed() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgress.setMessage("Downloading failed");
                            }
                        });
                    }

                    @Override
                    public void onDownloadCanceled() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadProgress.setMessage("Downloading canceled");
                            }
                        });
                    }

                    @Override
                    public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                        downloadProgress.setProgress(progressPercent);
                    }

                    @Override
                    public void onDownloadFinish(File file) {
                        downloadedFile[0] = file;
                    }
                };

                downloadTaskController = new DownloadTaskController(record, progressListener);

                downloadProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getResources().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadTaskController.cancelFileDownload();
                            }
                        });

                downloadProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        downloadTaskController.cancelFileDownload();
                    }
                });
                downloadProgress.setButton(DialogInterface.BUTTON_POSITIVE,
                        "Open",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                                openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Uri fileUri = Uri.fromFile(downloadedFile[0]);
                                openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(downloadedFile[0].getAbsolutePath()));
                                try {
                                    startActivity(openFileIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getActivity(), "No application to view this file", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                downloadProgress.show();
                downloadTaskController.downloadFile();
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PermissionHandler.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onDownloadClicked(final Record record) {
      if (DEBUG) VmrDebug.printLogI(this.getClass(), "Download button clicked" );
        if(record.isFolder()){
          if (DEBUG) VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");
            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
            recordStack.push(record.getNodeRef());
            refreshFolder();
            mSwipeRefreshLayout.setRefreshing(true);
        } else {

            if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
              if (DEBUG) VmrDebug.printLogI(this.getClass(),record.getRecordName() + " File clicked");

                final int notificationId = new Random().nextInt();

                final Notification.Builder downloadingNotification =
                        new Notification.Builder(getActivity())
                            .setContentTitle(record.getRecordName())
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setProgress(0,0,true)
                            .setOngoing(true);

                DownloadTask.DownloadProgressListener progressListener
                        = new DownloadTask.DownloadProgressListener() {
                    @Override
                    public void onDownloadStarted() {

                    }

                    @Override
                    public void onDownloadFailed() {
                        Notification downloadFailedNotification =
                                new Notification.Builder(getActivity())
                                    .setContentTitle(record.getRecordName())
                                    .setContentText("Download failed")
                                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                                    .setAutoCancel(true)
                                    .build();

                        NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        nm.cancel(record.getRecordId(), notificationId);
                        nm.notify(notificationId, downloadFailedNotification);
                    }

                    @Override
                    public void onDownloadCanceled() {
                        Notification downloadCanceledNotification =
                                new Notification.Builder(getActivity())
                                        .setContentTitle(record.getRecordName())
                                        .setContentText("Download canceled")
                                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                                        .setAutoCancel(true)
                                        .build();

                        NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        nm.cancel(record.getRecordId(), notificationId);
                        nm.notify(notificationId, downloadCanceledNotification);
                    }

                    @Override
                    public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                      if (DEBUG) VmrDebug.printLogI(FragmentSharedWithMe.this.getClass(), "Progress " +  progressPercent);

                        if(progressPercent == 0){
                            downloadingNotification.setProgress(0,0,true);
                        } else if(progressPercent == 100) {
                            downloadingNotification.setProgress(0,0,true);
                            downloadingNotification.setContentText("Finalizing...");
                        } else {
                            downloadingNotification.setProgress(100,progressPercent,false);
                            downloadingNotification.setContentText("Downloading... " + progressPercent + "%");
                        }

                        NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                        nm.notify(record.getRecordId(), notificationId ,downloadingNotification.build());
                    }

                    @Override
                    public void onDownloadFinish(File file) {
                        if (file != null) {
                            try {
                                String fileName = FileUtils.getNewFileName(record.getRecordName(), getActivity().getCacheDir() );
                                File newFile = new File(getActivity().getCacheDir(), fileName);
                                    FileUtils.copyFile(file, newFile);

                              if (DEBUG) VmrDebug.printLogI(FragmentSharedWithMe.this.getClass(), "File saved");
                                Snackbar.make(getActivity().findViewById(android.R.id.content), newFile.getName() + " downloaded", Snackbar.LENGTH_SHORT).show();

                                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                                openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Uri fileUri = Uri.fromFile(file);
                                openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(file.getAbsolutePath()));

                                PendingIntent pendingIntent
                                        = PendingIntent.getActivity(Vmr.getContext(), notificationId, openFileIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                                Notification.Builder downloadCompleteNotification =
                                        new Notification.Builder(getActivity())
                                                .setContentTitle(fileName)
                                                .setContentText("Download complete")
                                                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                                                .setAutoCancel(true);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                                    downloadCompleteNotification.addAction(new Notification.Action(0, "View", pendingIntent));
                                } else {
                                    downloadCompleteNotification.setContentIntent(pendingIntent);
                                }

                                NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                                nm.cancel(record.getRecordId(), notificationId);
                                nm.notify(notificationId, downloadCompleteNotification.build());
                                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                new DownloadTaskController(record,progressListener).downloadFile();

                NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                nm.notify(record.getRecordId(), notificationId ,downloadingNotification.build());

            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PermissionHandler.requestPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onMoveToTrashClicked(final Record record) {
      if (DEBUG) VmrDebug.printLogI(this.getClass(), "Delete button clicked" );
        final HomeController trashController = new HomeController(new VmrResponseListener.OnMoveToTrashListener() {
            @Override
            public void onMoveToTrashSuccess(List<DeleteMessage> deleteMessages) {
              if (DEBUG) VmrDebug.printLogI(FragmentSharedWithMe.this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success")) {
                      if (DEBUG) VmrDebug.printLogI(FragmentSharedWithMe.this.getClass(), dm.getObjectType() + " " + dm.getName() + " deleted");
                        dbManager.moveRecordToTrash(record);
                    }
                }
            }

            @Override
            public void onMoveToTrashFailure(VolleyError error) {
              if (DEBUG) VmrDebug.printLogI(FragmentSharedWithMe.this.getClass(), "Can't delete. File may be already removed.");
                refreshFolder();
            }

        });

        if(record.getRecordOwner().equalsIgnoreCase("admin") ) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Can not delete system folders", Snackbar.LENGTH_SHORT).show();
        } else {
            final boolean[] isCanceled = {false};
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " moved to Trash", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " restored!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                                isCanceled[0] = true;
                            }
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                if(!isCanceled[0]) {
                                    trashController.moveToTrash(record);
                                }
                            }
                        });
                snackbar.show();
        }
    }

    @Override
    public void onOptionsMenuDismiss() {
      if (DEBUG) VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
                    if (!recordStack.peek().equals(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_SHARED_NODE_REF))) {
                        recordStack.pop();
                        records = dbManager.getAllSharedWithMeRecords(recordStack.peek());
                        recordsAdapter.updateDataset(records);
                        refreshFolder();
                        mSwipeRefreshLayout.setRefreshing(true);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvSharedWithMe);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListSharedWithMe.TAG);
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recordsAdapter);
    }

    private void refreshFolder(){
        homeController.fetchAllFilesAndFolders(recordStack.peek());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}