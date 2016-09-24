package com.vmr.home.fragments;

import android.Manifest;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.ViewActivity;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.bottomsheet_behaviors.RecordOptionsMenuSheet;
import com.vmr.home.fragments.dialog.FolderPicker;
import com.vmr.home.fragments.dialog.IndexDialog;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFolder;
import com.vmr.network.VmrRequestQueue;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentToBeIndexed extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        RecordOptionsMenuSheet.OnOptionClickListener
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private RecordOptionsMenuSheet recordOptionsMenuSheet;

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

        recordOptionsMenuSheet = new RecordOptionsMenuSheet();
        recordOptionsMenuSheet.setOptionClickListener(this);

        dbManager = ((HomeActivity) getActivity()).getDbManager();

        recordStack = new Stack<>();
        recordStack.push(Vmr.getLoggedInUserInfo().getRootNodref());
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        VmrDebug.printLine("Un-indexed files retrieved.");

        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));
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
        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Record record) {
        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");
            VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);
            recordStack.push(record.getNodeRef());
            refreshFolder();
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            startActivity(ViewActivity.getLauncherIntent(((HomeActivity)getActivity()), record));
            VmrDebug.printLogI(record.getRecordName() + " File clicked");
        }
    }

    @Override
    public void onItemOptionsClick(Record record, View view) {
        VmrDebug.printLine(record.getRecordName() + " Options clicked");
        recordOptionsMenuSheet.setRecord(record);
        recordOptionsMenuSheet.show(getActivity().getSupportFragmentManager(), recordOptionsMenuSheet.getTag());
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
            Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
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
                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
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
        } else if(!record.getRecordOwner().equalsIgnoreCase(Vmr.getLoggedInUserInfo().getLoggedinUserId())) {
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
                HomeController dlController = new HomeController(new VmrResponseListener.OnFileDownload() {
                    @Override
                    public void onFileDownloadSuccess(byte[] bytes) {
                        try {
                            if (bytes != null) {
                                String fileName = FileUtils.getNewFileName(record.getRecordName());
                                File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                                if(!newFile.exists() && newFile.createNewFile()) {
                                    FileOutputStream outputStream = new FileOutputStream(newFile, false);
                                    outputStream.write(bytes);
                                    outputStream.close();
                                    VmrDebug.printLogI(this.getClass(), "File download complete");
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), newFile.getName() + " downloaded", Snackbar.LENGTH_SHORT).show();
                                    Notification downloadCompleteNotification =
                                            new Notification.Builder(getActivity())
                                                    .setContentTitle(fileName)
                                                    .setContentText("Download complete")
                                                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                                                    .setAutoCancel(true)
                                                    .build();

                                    NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                                    nm.cancel(fileName, Integer.valueOf(record.getRecordId()));
                                    nm.notify(Integer.valueOf(record.getRecordId()), downloadCompleteNotification);
                                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                                } else {
                                    VmrDebug.printLogI(this.getClass(), "File already exist or couldn't be created");
                                }
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
                dlController.downloadFile(record);
                Notification downloadingNotification =
                        new Notification.Builder(getActivity())
                        .setContentTitle(record.getRecordName())
                        .setContentText("Download in progress")
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setProgress(0,0,true)
                        .setOngoing(true)
                        .build();
                NotificationManager nm = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                nm.notify(record.getRecordName(), Integer.valueOf(record.getRecordId()) ,downloadingNotification);

            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PermissionHandler.requestPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                VmrDebug.printLogI(this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success"))
                    Toast.makeText(getContext(), dm.getObjectType() + " " + dm.getName() + " deleted" , Toast.LENGTH_SHORT).show();
                    dbManager.moveRecordToTrash(record);
                }
            }

            @Override
            public void onMoveToTrashFailure(VolleyError error) {
                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
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
                    if (!recordStack.peek().equals(Vmr.getLoggedInUserInfo().getRootNodref())) {
                        recordStack.pop();
                        records = dbManager.getAllUnIndexedRecords(recordStack.peek());
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