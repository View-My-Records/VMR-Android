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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.vmr.db.shared.SharedRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.SharedByMeAdapter;
import com.vmr.home.context_menu.SharedByMeOptionsMenu;
import com.vmr.home.controller.DownloadTaskController;
import com.vmr.home.controller.HomeController;
import com.vmr.home.controller.RecordDetailsController;
import com.vmr.home.request.DownloadTask;
import com.vmr.model.VmrSharedItem;
import com.vmr.network.VolleySingleton;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentSharedByMe extends Fragment
        implements
        VmrResponseListener.OnFetchSharedByMeListener,
        SharedByMeAdapter.OnItemClickListener,
        SharedByMeAdapter.OnItemOptionsClickListener,
        SharedByMeOptionsMenu.OnOptionClickListener {

    private boolean DEBUG = true;

    // Fragment interaction listener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private SharedByMeOptionsMenu optionsMenuSheet;

    // Controllers
    private HomeController homeController;
    private DbManager dbManager;

    // Variables
    private List<SharedRecord> sharedRecords = new ArrayList<>();
    private SharedByMeAdapter sharedByMeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        homeController = new HomeController(this);
        sharedByMeAdapter = new SharedByMeAdapter(sharedRecords, this, this);

        optionsMenuSheet = new SharedByMeOptionsMenu();
        optionsMenuSheet.setOptionClickListener(this);

        dbManager = Vmr.getDbManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.SHARED_BY_ME);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_shared_by_me, container, false);

        setupRecyclerView(fragmentView);
        setOnBackPress(fragmentView);

        mSwipeRefreshLayout.setRefreshing(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeController.fetchSharedByMe();
        sharedRecords = dbManager.getAllSharedByMe();
        sharedByMeAdapter.updateDataset(sharedRecords);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchSharedByMeSuccess(List<VmrSharedItem> vmrSharedItems) {
        VmrDebug.printLogI(this.getClass(), "My Records retrieved.");

        dbManager.updateAllSharedByMe(SharedRecord.getSharedRecordsList(vmrSharedItems, "NA"));
        sharedRecords = dbManager.getAllSharedByMe();
        sharedByMeAdapter = new SharedByMeAdapter(sharedRecords, this, this);
        mRecyclerView.setAdapter(sharedByMeAdapter);

        mSwipeRefreshLayout.setRefreshing(false);

        if(vmrSharedItems.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchSharedByMeFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(final SharedRecord record) {
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
                    downloadProgress.setIndeterminate(true);
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

    @Override
    public void onItemOptionsClick(SharedRecord record, View view) {
        VmrDebug.printLine( record.getRecordName() + " options clicked.");
        optionsMenuSheet.setRecord(record);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    @Override
    public void onOpenClicked(final SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " open clicked.");
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
                            downloadProgress.setIndeterminate(true);
                            downloadProgress.setMessage("Downloading " + record.getRecordName());
                            downloadProgress.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
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
                public void onDownloadFinish(final File file) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                            downloadProgress.setMessage("Download Complete");
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setProgress(100);
                            downloadedFile[0] = file;
                        }
                    });

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

    @Override
    public void onDownloadClicked(final SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " download clicked.");

        if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " File clicked");

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
                    VmrDebug.printLogI(FragmentSharedByMe.this.getClass(), "Progress " +  progressPercent);

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

                            VmrDebug.printLogI(FragmentSharedByMe.this.getClass(), "File saved");
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

    @Override
    public void onRevokeAccessClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " revoke access clicked.");
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPropertiesClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " properties clicked.");
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        final RecordDetailsController recordDetailsController = new RecordDetailsController(new RecordDetailsController.OnFetchRecordDetailsListener() {
            @Override
            public void onFetchRecordDetailsSuccess(JSONObject jsonObject) {
                progressDialog.dismiss();
                new AlertDialog
                        .Builder(getActivity())
                        .setMessage(jsonObject.toString())
                        .show();
            }

            @Override
            public void onFetchRecordDetailsFailure(VolleyError error) {
                Toast.makeText(getContext(), "Failed to process request", Toast.LENGTH_SHORT).show();
            }
        });

        recordDetailsController.fetchRecordDetails(
                record.getNodeRef(),
                PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_EMAIL),
                record.getRecordId());

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.Share.TAG);
            }
        });
        progressDialog.show();
    }

    @Override
    public void onMoveToTrashClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " trash clicked.");
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionsMenuDismiss() {
        VmrDebug.printLine( "Options dismissed");
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }
                return false;
            }
        });
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvSharedByMe);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListSharedByMe.TAG);
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void refreshFolder(){
        homeController.removeExpiredRecords();
        homeController.fetchSharedByMe();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
