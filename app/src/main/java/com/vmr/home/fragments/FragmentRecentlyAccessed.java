package com.vmr.home.fragments;

import android.Manifest;
import android.app.FragmentManager;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbConstants;
import com.vmr.db.DbManager;
import com.vmr.db.recently_accessed.Recent;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.activity.IndexActivity;
import com.vmr.home.adapters.RecentAdapter;
import com.vmr.home.context_menu.RecentOptionsMenu;
import com.vmr.home.controller.DownloadTaskController;
import com.vmr.home.controller.HomeController;
import com.vmr.home.controller.RecordDetailsController;
import com.vmr.home.fragments.dialog.ShareDialog;
import com.vmr.home.request.DownloadTask;
import com.vmr.model.DeleteMessage;
import com.vmr.network.VolleySingleton;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;


public class FragmentRecentlyAccessed extends Fragment
        implements
        RecentAdapter.OnItemClickListener,
        RecentAdapter.OnItemOptionsClickListener,
        RecentOptionsMenu.OnOptionClickListener {

    private static int REQUEST_INDEX_FILE = 102;
    boolean DEBUG = true;
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecentOptionsMenu optionsMenuSheet;

    // Controllers
    private DbManager dbManager;

    // Variables
    private List<Recent> recentRecords = new ArrayList<>();
    private RecentAdapter recentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        recentAdapter = new RecentAdapter(recentRecords, this, this);

        dbManager = Vmr.getDbManager();

        optionsMenuSheet = new RecentOptionsMenu();
        optionsMenuSheet.setOptionClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener = (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.RECENTLY_ACCESSED);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_recently_accessed, container, false);

        setupRecyclerView(fragmentView);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        recentRecords = dbManager.getAllRecentlyAccsseed();
        recentAdapter.updateDataset(recentRecords);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(Recent recent) {

        if (DEBUG) VmrDebug.printLogI(recent.getName() + " recent item clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
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
        } else {
            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
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
    public void onItemOptionsClick(Recent recent, View view) {
        if(DEBUG) VmrDebug.printLine(recent.getName() + " Options clicked");
        optionsMenuSheet.setRecent(recent);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    @Override
    public void onOpenClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " open clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
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
        } else {
            Toast.makeText(this.getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIndexClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " open clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);

            Intent indexIntent = IndexActivity.newInstance(getActivity(), record);
            startActivityForResult(indexIntent, REQUEST_INDEX_FILE);
        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
//            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
//            dbManager.addNewRecent(record);
            Toast.makeText(getActivity(), "Not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onShareClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " share clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
//            dbManager.addNewRecent(record);
            FragmentManager fm = getActivity().getFragmentManager();
            ShareDialog shareDialog = ShareDialog.newInstance(record);
            shareDialog.show(fm, "Share");

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
//            dbManager.addNewRecent(record);
            FragmentManager fm = getActivity().getFragmentManager();
            ShareDialog shareDialog = ShareDialog.newInstance(record);
            shareDialog.show(fm, "Share");
        }
    }

    @Override
    public void onRenameClicked(final Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " rename clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
//            dbManager.addNewRecent(record);

            View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_rename, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewItemName);

            final String recordName = record.getRecordName().substring(0, record.getRecordName().lastIndexOf('.'));
            final String recordExt  = record.getRecordName().substring(1, record.getRecordName().lastIndexOf('.'));

            userInput.setText(recordName);
            userInput.setSelection(userInput.getText().length());

            final HomeController renameController = new HomeController(new VmrResponseListener.OnRenameItemListener() {
                @Override
                public void onRenameItemSuccess(JSONObject jsonObject) {
                    try {
                        if (jsonObject.has("Response") && jsonObject.getString("Response").equals("success")) {
                            VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), record.getRecordName() + " renamed.");
                            dbManager.deleteRecent(recent);
                            refreshFolder();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRenameItemFailure(VolleyError error) {
                    Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                }

            });

            final Snackbar snackBarOnUndo =
                    Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " restored!", Snackbar.LENGTH_SHORT);

            final Snackbar snackBarOnOk =
                    Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " renamed",Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackBarOnUndo.show();
                                }
                            })
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);
                                    String completeName = userInput.getText().toString() + '.' + recordExt;
                                    renameController.renameItem(record, completeName);
                                }
                            });

            // set dialog message
            alertDialogBuilder
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            snackBarOnOk.show();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
                    .setTitle("Rename");
            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });

            userInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(TextUtils.isEmpty(editable)){
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            });

            if(record.getRecordOwner().equals("admin") ) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Can not modify system folders", Snackbar.LENGTH_SHORT).show();
            } else {
                alertDialog.show();
            }

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
//            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
//            dbManager.addNewRecent(record);
            Toast.makeText(getActivity(), "This feature is not available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCopyClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " copy clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
            Pair<Record, Integer> clip = new Pair<>(record, 3 ); // 2-Move, 3-Copy
            Vmr.setClipBoard(clip);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "File copied in clipboard", Snackbar.LENGTH_SHORT).show();

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
//            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
            Toast.makeText(getActivity(), "This feature is not available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownloadClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " download clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);

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
                        VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), "Progress " +  progressPercent);

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

                                VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), "File saved");
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

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);

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
                        nm.cancel(String.valueOf(record.getRecordId()), notificationId);
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
                        nm.cancel(String.valueOf(record.getRecordId()), notificationId);
                        nm.notify(notificationId, downloadCanceledNotification);
                    }

                    @Override
                    public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                        VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), "Progress " +  progressPercent);

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
                        nm.notify(String.valueOf(record.getRecordId()), notificationId ,downloadingNotification.build());
                    }

                    @Override
                    public void onDownloadFinish(File file) {
                        if (file != null) {
                            try {
                                String fileName = FileUtils.getNewFileName(record.getRecordName(), getActivity().getCacheDir() );
                                File newFile = new File(getActivity().getCacheDir(), fileName);
                                FileUtils.copyFile(file, newFile);

                                VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), "File saved");
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
                                nm.cancel(String.valueOf(record.getRecordId()), notificationId);
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
                nm.notify(String.valueOf(record.getRecordId()), notificationId ,downloadingNotification.build());

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
    public void onPropertiesClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " properties clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());

            final RecordDetailsController recordDetailsController
                    = new RecordDetailsController(new RecordDetailsController.OnFetchRecordDetailsListener() {
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

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);
            Toast.makeText(getActivity(), "Not available", Toast.LENGTH_SHORT).show();
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());

            final RecordDetailsController recordDetailsController
                    = new RecordDetailsController(new RecordDetailsController.OnFetchRecordDetailsListener() {
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
                    String.valueOf(record.getRecordId()));

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
    }

    @Override
    public void onMoveToTrashClicked(final Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " move to trash clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = dbManager.getRecord(recent.getNodeRef());
            final HomeController trashController
                    = new HomeController(new VmrResponseListener.OnMoveToTrashListener() {
                @Override
                public void onMoveToTrashSuccess(List<DeleteMessage> deleteMessages) {
                    VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), deleteMessages.toString() );
                    refreshFolder();

                    for (DeleteMessage dm : deleteMessages) {
                        if(dm.getStatus().equals("success")) {
                            VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), dm.getObjectType() + " " + dm.getName() + " deleted");
                            dbManager.moveRecordToTrash(record);
                        }
                    }
                }

                @Override
                public void onMoveToTrashFailure(VolleyError error) {
                    VmrDebug.printLogI(FragmentRecentlyAccessed.this.getClass(), "Can't delete. File may be already removed.");
                    Toast.makeText(getActivity(), "Request failed. File may be already removed.", Toast.LENGTH_SHORT).show();
                    dbManager.deleteRecent(recent);
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
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        record.getRecordName() + " restored!",
                                        Snackbar.LENGTH_SHORT)
                                        .show();
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

        } else if(recent.getLocation().equals(DbConstants.TABLE_SHARED)) {
//            final SharedRecord record = dbManager.getSharedRecord(recent.getNodeRef());
//            dbManager.addNewRecent(record);
            Toast.makeText(getActivity(), "Not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOptionsMenuDismiss() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_INDEX_FILE){
                refreshFolder();
            }
        }
    }

    private void refreshFolder(){
        recentRecords = dbManager.getAllRecentlyAccsseed();
        recentAdapter.updateDataset(recentRecords);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        final TextView mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvRecent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFolder();
                if(recentRecords.size() == 0){
                    mTextView.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                } else {
                    mTextView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recentAdapter);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
