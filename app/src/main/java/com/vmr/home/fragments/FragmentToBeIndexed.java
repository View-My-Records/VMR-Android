package com.vmr.home.fragments;

import android.Manifest;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.context_menu.RecordOptionsMenu;
import com.vmr.home.controller.DownloadController;
import com.vmr.home.controller.HomeController;
import com.vmr.home.fragments.dialog.FolderPicker;
import com.vmr.home.fragments.dialog.IndexDialog;
import com.vmr.home.request.DownloadRequest;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFolder;
import com.vmr.network.VmrRequestQueue;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentToBeIndexed extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        RecordOptionsMenu.OnOptionClickListener
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private RecordOptionsMenu recordOptionsMenu;

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

        dbManager = Vmr.getDbManager();

        recordOptionsMenu = new RecordOptionsMenu();
        recordOptionsMenu.setOptionClickListener(this);

        recordStack = new Stack<>();
        recordStack.push(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.TO_BE_INDEXED);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_to_be_indexed, container, false);

        setupRecyclerView(fragmentView);
        setOnBackPress(fragmentView);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        records = dbManager.getAllUnIndexedRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);
//        refreshFolder();
    }

    @Override
    public void onResume() {
        super.onResume();
        records = dbManager.getAllUnIndexedRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);
//        refreshFolder();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        VmrDebug.printLine("Un-indexed files retrieved.");

        if(vmrFolder.getAll().size()>0)
            dbManager.removeAllRecords(recordStack.peek(), vmrFolder);
        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));

        if(dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null)
            VmrDebug.printLogI(this.getClass(), "Before->" + dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().toString());
        dbManager.updateTimestamp(recordStack.peek());
        if(dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null)
            VmrDebug.printLogI(this.getClass(), "After->" + dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().toString()+"");

        records = dbManager.getAllUnIndexedRecords(recordStack.peek());
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
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");

            VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);

            fragmentInteractionListener.onFragmentInteraction(record.getRecordName());

            recordStack.push(record.getNodeRef());

            VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp()+"");

            if ( dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null) {
                if (dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().before(new Date(System.currentTimeMillis() - 5* 60 * 1000))) {
                    refreshFolder();
                    mSwipeRefreshLayout.setRefreshing(true);
                    VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "Folder refreshed.");
                } else {
                    records = dbManager.getAllUnIndexedRecords(recordStack.peek());
                    recordsAdapter.updateDataset(records);
                    if(records.isEmpty()){
                        mRecyclerView.setVisibility(View.GONE);
                        mTextView.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.GONE);
                    }
                }
            } else {
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
                VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "Folder refreshed.");
            }
        } else {
            VmrDebug.printLogI(record.getRecordName() + " File clicked");
            dbManager.addNewRecent(record);
//            startActivity(ViewActivity.getLauncherIntent(getActivity(), record));
            if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                final ProgressDialog downloadProgress = new ProgressDialog(getActivity());
                DownloadController dlController = new DownloadController(new DownloadController.OnFileDownload() {
                    @Override
                    public void onFileDownloadSuccess(File file) {
                        VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "File download complete");
                        downloadProgress.dismiss();
                        try {
                            if (file != null) {
                                final File tempFile = new File(getActivity().getExternalCacheDir(), record.getRecordName());
                                if (tempFile.exists())
                                    tempFile.delete();
                                FileUtils.copyFile(file, tempFile);

                                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                                openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Uri fileUri = Uri.fromFile(tempFile);
                                openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(tempFile.getAbsolutePath()));
                                try {
                                    startActivity(openFileIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getActivity(), "No application to view this file", Toast.LENGTH_SHORT).show();
                                }
                            } else VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "null file");
                        } catch (Exception e) {
                            VmrDebug.printLogI(this.getClass(), "File download failed");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFileDownloadFailure(VolleyError error) {
                        VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "File download failed");
                    }
                });
                VmrDebug.printLogI(this.getClass(), "Downloading...");
                downloadProgress.setMessage("Receiving file...");
                downloadProgress.setCanceledOnTouchOutside(false);
                downloadProgress.setIndeterminate(true);
                downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                DownloadRequest.DownloadProgressListener progressListener = new DownloadRequest.DownloadProgressListener() {
                    @Override
                    public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                        if(progressPercent > 0){
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setProgress(progressPercent);
                        }
                    }
                };
                dlController.downloadFile(record, progressListener);
                downloadProgress.show();
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
    public void onItemOptionsClick(Record record, View view) {
        VmrDebug.printLine(record.getRecordName() + " Options clicked");
        recordOptionsMenu.setRecord(record);
        recordOptionsMenu.show(getActivity().getSupportFragmentManager(), recordOptionsMenu.getTag());
    }

    @Override
    public void onOpenClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Open button clicked" );
        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder opened");
            VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
            recordStack.push(record.getNodeRef());
            refreshFolder();
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            VmrDebug.printLogI(record.getRecordName() + " File clicked");
            dbManager.addNewRecent(record);
        }
    }

    @Override
    public void onIndexClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Index button clicked" );
        FragmentManager fm = getActivity().getFragmentManager();
        IndexDialog indexDialog = IndexDialog.newInstance(record);
        indexDialog.show(fm, "Index");
    }

    @Override
    public void onShareClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Share button clicked" );
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRenameClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Rename button clicked" );
        View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_rename, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewItemName);
        userInput.setText(record.getRecordName());
        userInput.setSelection(userInput.getText().length());

        final HomeController renameController = new HomeController(new VmrResponseListener.OnRenameItemListener() {
            @Override
            public void onRenameItemSuccess(JSONObject jsonObject) {
                try {
                    if (jsonObject.has("Response") && jsonObject.getString("Response").equals("success")) {
                        VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), record.getRecordName() + " renamed.");
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
                        renameController.renameItem(record, userInput.getText().toString());
                    }
                });

        // set dialog message
        alertDialogBuilder
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    snackBarOnOk.show();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

        if(record.getRecordOwner().equalsIgnoreCase("admin") ) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Can not modify system folders", Snackbar.LENGTH_SHORT).show();
        } else if(!record.getRecordOwner().equalsIgnoreCase(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID))) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "This folder belongs to someone else", Snackbar.LENGTH_SHORT).show();
        } else {
            alertDialog.show();
        }
    }

    @Override
    public void onDownloadClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Download button clicked" );
        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");
            VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
            recordStack.push(record.getNodeRef());
            refreshFolder();
            mSwipeRefreshLayout.setRefreshing(true);
        } else {

            if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                VmrDebug.printLogI(this.getClass(),record.getRecordName() + " File clicked");

                final int notificationId = new Random().nextInt();

                DownloadController dlController = new DownloadController(new DownloadController.OnFileDownload() {
                    @Override
                    public void onFileDownloadSuccess(File file) {
                        try {
                            if (file != null) {
                                String fileName = FileUtils.getNewFileName(record.getRecordName(), getActivity().getCacheDir() );
                                File newFile = new File(getActivity().getCacheDir(), fileName);
                                FileUtils.copyFile(file, newFile);
                                VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "File saved");
                                Snackbar.make(getActivity().findViewById(android.R.id.content), newFile.getName() + " downloaded", Snackbar.LENGTH_SHORT).show();
                                Notification downloadCompleteNotification =
                                    new Notification.Builder(getActivity())
                                            .setContentTitle(fileName)
                                            .setContentText("Download complete")
                                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                                            .setAutoCancel(true)
                                            .build();

                                NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                                nm.cancel(record.getRecordId(), notificationId);
                                nm.notify(notificationId, downloadCompleteNotification);
                                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                            }
                        } catch (Exception e) {
                            VmrDebug.printLogI(this.getClass(), "File download failed");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFileDownloadFailure(VolleyError error) {

                    }
                });
                final Notification.Builder downloadingNotification =
                        new Notification.Builder(getActivity())
                                .setContentTitle(record.getRecordName())
                                .setContentText("Downloading...")
                                .setSmallIcon(android.R.drawable.stat_sys_download)
                                .setProgress(0,0,true)
                                .setOngoing(true);
                DownloadRequest.DownloadProgressListener progressListener = new DownloadRequest.DownloadProgressListener() {
                    @Override
                    public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                        if(progressPercent == 0){
                            downloadingNotification.setProgress(0,0,true);
                        } else if(progressPercent == 100){
                            downloadingNotification.setProgress(0,0,true);
                            downloadingNotification.setContentText("Finalizing...");
                        } else {
                            downloadingNotification.setProgress(100,progressPercent,false);
                            downloadingNotification.setContentText("Downloading... " + progressPercent + "%");
                        }
                    }
                };
                dlController.downloadFile(record, progressListener);
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
    public void onMoveClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Move button clicked" );
        FragmentManager fm = getActivity().getFragmentManager();
        FolderPicker folderPicker = new FolderPicker();
        folderPicker.setOnFolderPickedListener(new FolderPicker.OnFolderPickedListener() {
            @Override
            public void onFolderPicked(Record record) {
                VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), record.getRecordName() + " received in fragment");
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Move item feature is not available.", Snackbar.LENGTH_SHORT).show();
            }
        });

        folderPicker.show(fm, "file_picker");
    }

    @Override
    public void onCopyClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Copy button clicked" );
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPasteClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Paste button clicked" );
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDuplicateClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Duplicate button clicked" );
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPropertiesClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Properties button clicked" );
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onMoveToTrashClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Delete button clicked" );
        final HomeController trashController = new HomeController(new VmrResponseListener.OnMoveToTrashListener() {
            @Override
            public void onMoveToTrashSuccess(List<DeleteMessage> deleteMessages) {
                VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success"))
                    Toast.makeText(getActivity(), dm.getObjectType() + " " + dm.getName() + " deleted" , Toast.LENGTH_SHORT).show();
                    dbManager.moveRecordToTrash(record);
                }
            }

            @Override
            public void onMoveToTrashFailure(VolleyError error) {
                Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
            }

        });

        if(record.getRecordOwner().equalsIgnoreCase("admin") ) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Can not delete system folders", Snackbar.LENGTH_SHORT).show();
        } else {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " moved to Trash", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " restored!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                trashController.moveToTrash(record);
                            }
                        });
                snackbar.show();
        }
    }

    @Override
    public void onOptionsMenuDismiss() {
        VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);
                    if (!recordStack.peek().equals(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF))) {
                        recordStack.pop();
                        if (recordStack.peek().equals(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF))) {
                            fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.MY_RECORDS);
                        } else {
                            fragmentInteractionListener.onFragmentInteraction(dbManager.getRecord(recordStack.peek()).getRecordName());
                        }

                        VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp()+"");

                        if ( dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null) {
                            if (dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().before(new Date(System.currentTimeMillis() - 60 * 1000))) {
                                refreshFolder();
                                mSwipeRefreshLayout.setRefreshing(true);
                                VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "Folder refreshed.");
                            } else {
                                records = dbManager.getAllUnIndexedRecords(recordStack.peek());
                                recordsAdapter.updateDataset(records);
                                if(records.isEmpty()){
                                    mRecyclerView.setVisibility(View.GONE);
                                    mTextView.setVisibility(View.VISIBLE);
                                } else {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mTextView.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            refreshFolder();
                            mSwipeRefreshLayout.setRefreshing(true);
                            VmrDebug.printLogI(FragmentToBeIndexed.this.getClass(), "Folder refreshed.");
                        }
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvToBeIndexed);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);
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
        homeController.fetchUnIndexed(recordStack.peek());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}