package com.vmr.home.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbConstants;
import com.vmr.db.DbManager;
import com.vmr.db.recently_accessed.Recent;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.RecentAdapter;
import com.vmr.home.context_menu.RecentOptionsMenu;
import com.vmr.home.controller.DownloadTaskController;
import com.vmr.home.controller.HomeController;
import com.vmr.home.request.DownloadTask;
import com.vmr.utils.Constants;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FragmentRecentlyAccessed extends Fragment
        implements
        RecentAdapter.OnItemClickListener,
        RecentAdapter.OnItemOptionsClickListener,
        RecentOptionsMenu.OnOptionClickListener {

    final boolean DEBUG = true;

    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecentOptionsMenu optionsMenuSheet;

    // Controllers
    private HomeController homeController;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " File clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = Vmr.getDbManager().getRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);
            if (PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
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
    public void onItemOptionsClick(Recent recent, View view) {
        if(DEBUG) VmrDebug.printLine(recent.getName() + " Options clicked");
        optionsMenuSheet.setRecent(recent);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        TextView mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvRecent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recentRecords = dbManager.getAllRecentlyAccsseed();
                recentAdapter.updateDataset(recentRecords);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recentAdapter);
    }

    @Override
    public void onOpenClicked(Recent recent) {
        if (DEBUG) VmrDebug.printLogI(recent.getName() + " open clicked");
        if(recent.getLocation().equals(DbConstants.TABLE_RECORD)) {
            final Record record = Vmr.getDbManager().getRecord(recent.getNodeRef());
            dbManager.addNewRecent(record);
            if (PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
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
    public void onIndexClicked(Recent record) {

    }

    @Override
    public void onShareClicked(Recent record) {

    }

    @Override
    public void onRenameClicked(Recent record) {

    }

    @Override
    public void onMoveClicked(Recent record) {

    }

    @Override
    public void onCopyClicked(Recent record) {

    }

    @Override
    public void onDuplicateClicked(Recent record) {

    }

    @Override
    public void onPasteClicked(Recent record) {

    }

    @Override
    public void onDownloadClicked(Recent record) {

    }

    @Override
    public void onPropertiesClicked(Recent record) {

    }

    @Override
    public void onMoveToTrashClicked(Recent record) {

    }

    @Override
    public void onOptionsMenuDismiss() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
