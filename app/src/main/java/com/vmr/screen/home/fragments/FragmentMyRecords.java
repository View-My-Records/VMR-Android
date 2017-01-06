package com.vmr.screen.home.fragments;

import android.Manifest;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.model.DeleteMessage;
import com.vmr.model.RecordDetails;
import com.vmr.model.VmrFolder;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.DownloadTaskController;
import com.vmr.network.controller.HomeController;
import com.vmr.network.controller.RecordDetailsController;
import com.vmr.network.controller.VmrResponseListener;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.DownloadTask;
import com.vmr.screen.home.HomeActivity;
import com.vmr.screen.home.adapters.RecordsAdapter;
import com.vmr.screen.home.context_menu.AddItemMenu;
import com.vmr.screen.home.context_menu.RecordOptionsMenu;
import com.vmr.screen.home.fragments.dialog.ShareDialog;
import com.vmr.screen.home.interfaces.Interaction;
import com.vmr.screen.index.IndexActivity;
import com.vmr.service.UploadService;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.vmr.R.id.fab;

public class FragmentMyRecords extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        Interaction.HomeToMyRecordsInterface,
        Interaction.OnHomeClickListener,
        Interaction.OnPasteClickListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        AddItemMenu.OnItemClickListener,
        RecordOptionsMenu.OnOptionClickListener {

    private static final int PERMISSION_CODE_READ_STORAGE = 1;
    private static final int PERMISSION_CODE_WRITE_STORAGE = 2;
    private static final int PERMISSION_CODE_CAMERA = 3;
    private static int FILE_PICKER_INTENT = 100;
    private static int REQUEST_IMAGE_CAPTURE = 101;
    private static int REQUEST_INDEX_FILE = 102;
    private boolean DEBUG = true;
    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;
    // Views
    private FloatingActionButton mFabAddItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private AddItemMenu addItemMenu;
    private RecordOptionsMenu recordOptionsMenu;
    // Controllers
    private HomeController homeController;
    private DbManager dbManager;
    // Variables
    private List<Record> records = new ArrayList<>();
    private RecordsAdapter recordsAdapter;
    // Stack
    private Stack<String> recordStack;
    BroadcastReceiver broadCastUploadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "File upload success broadcast received.");
            refreshFolder();
        }
    };
    private String cameraImagePath;
    private File photoFile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ((HomeActivity) getActivity()).setSendToMyRecords(this);
        ((HomeActivity) getActivity()).setHomeClickListener(this);
        ((HomeActivity) getActivity()).setPasteClickListener(this);

        homeController = new HomeController(this);
        recordsAdapter = new RecordsAdapter(records, this, this);

        addItemMenu = new AddItemMenu();
        addItemMenu.setItemClickListener(this);

        dbManager = Vmr.getDbManager();

        recordOptionsMenu = new RecordOptionsMenu();
        recordOptionsMenu.setOptionClickListener(this);

        recordStack = new Stack<>();
        recordStack.push(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));

        if(DEBUG) VmrDebug.printLogI(this.getClass(), recordStack.peek());
        if(DEBUG) VmrDebug.printLogI(this.getClass(), PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // User interface to change the Title in the Activity
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.MY_RECORDS);

        View fragmentView = inflater.inflate(R.layout.home_fragment_my_records, container, false);

        setupRecyclerView(fragmentView);
        setupFab(fragmentView);
        setOnBackPress(fragmentView);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        records = dbManager.getAllRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);
        getActivity().registerReceiver(this.broadCastUploadComplete, new IntentFilter("upload_complete"));
    }

    @Override
    public void onResume() {
        super.onResume();
        records = dbManager.getAllRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);
//        refreshFolder();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
        getActivity().unregisterReceiver(broadCastUploadComplete);
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        VmrDebug.printLogI(this.getClass(), "Records retrieved.");


        dbManager.removeAllRecords(recordStack.peek(), vmrFolder);

        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));

//        if(dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null)
//            VmrDebug.printLogI(this.getClass(), "Before->" + dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().toString());

        dbManager.updateTimestamp(recordStack.peek());

//        if(dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null)
//            VmrDebug.printLogI(this.getClass(), "After->" + dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().toString());

        records = dbManager.getAllRecords(recordStack.peek());

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
//        Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveFromActivitySuccess(List<Record> records) {
        recordsAdapter.updateDataset(records);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onReceiveFromActivityFailure(VolleyError error) {
//        Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            VmrDebug.printLogI(this.getClass(), "Home pressed");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final Record record) {

//        Toast.makeText(getActivity(), record.getRecordName() + " clicked", Toast.LENGTH_SHORT).show();

        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");

            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);

            recordStack.push(record.getNodeRef());
            fragmentInteractionListener.onFragmentInteraction(record.getRecordName());
            fragmentInteractionListener.setBackButton(true);

//            if (DEBUG)
//                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp()+"");

            if ( dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null) {
                if (dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().before(new Date(System.currentTimeMillis() - 5* 60 * 1000))) {
                    refreshFolder();
                    mSwipeRefreshLayout.setRefreshing(true);
                    VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Folder refreshed.");
                } else {
                    records = dbManager.getAllRecords(recordStack.peek());
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
                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Folder refreshed.");
            }
        } else {
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

                DownloadTask.ProgressListener progressListener
                        = new DownloadTask.ProgressListener() {
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
                                PermissionHandler.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_CODE_WRITE_STORAGE);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onItemOptionsClick(Record record, View view) {
        if(DEBUG) VmrDebug.printLine(record.getRecordName() + " Options clicked");
        recordOptionsMenu.setRecord(record);
        mFabAddItem.hide();
        recordOptionsMenu.show(getActivity().getSupportFragmentManager(), recordOptionsMenu.getTag());
    }

    @Override
    public void onScanClick() {
        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Camera button clicked" );
        if(PermissionHandler.checkPermission(Manifest.permission.CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    try {
                        photoFile = createImageFile();
                        if (photoFile != null) {
                            Uri photoURI = Uri.fromFile(photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            try {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            } catch (ActivityNotFoundException e){
                                Snackbar.make(getActivity().findViewById(R.id.clayout), "Couldn't find camera application", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e){
                        Snackbar.make(getActivity().findViewById(R.id.clayout), "Couldn't create the file", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PermissionHandler.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_CODE_WRITE_STORAGE);
                                }
                            })
                            .show();
                }
            } else {
                Snackbar.make(getActivity().findViewById(R.id.clayout), "No camera application found", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to use Camera", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(getActivity(), Manifest.permission.CAMERA, PERMISSION_CODE_CAMERA);
                        }
                    })
                    .show();
        }
    }

    private File createImageFile() throws IOException {
        // Timestamp for image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // File Directory
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        // File name
        String fileName = "IMG_"+ timeStamp;

        // create temp file
        File imageFile = File.createTempFile(
                fileName,       /* prefix */
                ".jpg",         /* suffix */
                mediaStorageDir /* directory */
        );
        cameraImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onPickFileClick() {
        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Upload file button clicked");
        if(PermissionHandler.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            startActivityForResult(intent, FILE_PICKER_INTENT);
        } else {
            Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE_READ_STORAGE);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onScanClick();
                }
                break;

            case PERMISSION_CODE_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPickFileClick();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri returnUri;
        if(resultCode == RESULT_OK){
            if(requestCode == FILE_PICKER_INTENT){
                VmrDebug.printLogE(this.getClass(), "File picked ");
                returnUri = data.getData();
                if(returnUri != null) {
                    onFilePicked(returnUri);
                    initiateUpload();
                } else {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            VmrDebug.printLogE(this.getClass(), "Uri " + i + " : " + uri);
                            onFilePicked(uri);
                        }
                        initiateUpload();
                    }
                }
            } else if(requestCode == REQUEST_IMAGE_CAPTURE){
                returnUri = Uri.fromFile(photoFile);
                onFilePicked(returnUri);
                initiateUpload();
            } else if(requestCode == REQUEST_INDEX_FILE){
                refreshFolder();
            }
        }
    }

    private void onFilePicked(Uri selectedFileUri){
        VmrDebug.printLogE(this.getClass(), "File URI :" + selectedFileUri);
        try {
            dbManager.queueUpload(selectedFileUri, recordStack.peek());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(getActivity().findViewById(R.id.clayout)
                    , "Couldn't create a local file"
                    , Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void initiateUpload(){
        Intent uploadIntent = new Intent(getActivity(), UploadService.class);
        getActivity().startService(uploadIntent);
    }

    @Override
    public void onNewFolderClick() {
        VmrDebug.printLogI(this.getClass(), "create folder button clicked" );
        final View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_create_folder, null);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewFolderName);

        final HomeController createFolderController
            = new HomeController(new VmrResponseListener.OnCreateFolderListener() {
            @Override
            public void onCreateFolderSuccess(JSONObject jsonObject) {
                try {
                    if (jsonObject.has("result") && jsonObject.getString("result").equals("success")) {
                        Toast.makeText(Vmr.getContext(), "New folder created.", Toast.LENGTH_SHORT).show();
                        refreshFolder();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCreateFolderFailure(VolleyError error) {
                Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
            }
        });

        final Snackbar snackbar =
            Snackbar.make(getActivity().findViewById(R.id.clayout), "New folder created",Snackbar.LENGTH_LONG)
            .setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(getActivity().findViewById(R.id.clayout), "Canceled", Snackbar.LENGTH_SHORT)
                            .show();
                }
            })
            .setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    createFolderController.createFolder( userInput.getText().toString(), recordStack.peek() );
                }
            });

        // set dialog message
        final AlertDialog alertDialog
                = new AlertDialog.Builder(getActivity())
                .setView(promptsView)
                .setCancelable(true)
                .setTitle("Create New Folder")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                Button buttonPositive = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setEnabled(false);
                buttonPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(userInput.length() == 0) {
                            userInput.setError("Only alphabets, numbers and spaces are allowed");
                        } else if(checkRecordWithNameAlreadyExist(userInput.getText().toString())) {
                            userInput.setError("Folder with same name already exists");
                        } else {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                            dialogInterface.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    snackbar.show();
                                }
                            }, 1000);
                        }
                    }

                    private boolean checkRecordWithNameAlreadyExist(String s) {
                        for (Record r : dbManager.getFolders(recordStack.peek())) {
                            if(r.getRecordName().equals(s)){
                                return true;
                            }
                        }
                        return false;
                    }
                });

                Button buttonNegative = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonNegative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                        dialogInterface.dismiss();
                    }
                });
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

        // show it
        alertDialog.show();
    }

    @Override
    public void onAddItemsMenuDismiss() {
        VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
        mFabAddItem.show();
    }

    @Override
    public void onOpenClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Open button clicked" );

        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder opened");
            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);

            fragmentInteractionListener.onFragmentInteraction(record.getRecordName());

            recordStack.push(record.getNodeRef());

            VmrDebug.printLogI(FragmentMyRecords.this.getClass(), dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp()+"");

            if ( dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null) {
                if (dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().before(new Date(System.currentTimeMillis() - 5* 60 * 1000))) {
                    refreshFolder();
                    mSwipeRefreshLayout.setRefreshing(true);
                    VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Folder refreshed.");
                } else {
                    records = dbManager.getAllRecords(recordStack.peek());
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
                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Folder refreshed.");
            }
        } else {
            VmrDebug.printLogI(record.getRecordName() + " File clicked");
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

                DownloadTask.ProgressListener progressListener
                        = new DownloadTask.ProgressListener() {
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
                Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PermissionHandler.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_CODE_WRITE_STORAGE);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onIndexClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Index button clicked" );

        Intent indexIntent = IndexActivity.newInstance(getActivity(), record);
        startActivityForResult(indexIntent, REQUEST_INDEX_FILE);
    }

    @Override
    public void onShareClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Share button clicked" );
        FragmentManager fm = getActivity().getFragmentManager();
        ShareDialog shareDialog = ShareDialog.newInstance(record);
        shareDialog.show(fm, "Share");
    }

    @Override
    public void onRenameClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Rename button clicked" );
        View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_rename, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewItemName);

        final String recordName;
        if(record.getRecordName().contains(".")) {
            recordName = record.getRecordName().substring(0, record.getRecordName().lastIndexOf('.'));
        } else {
            recordName = record.getRecordName();
        }

        userInput.setText(recordName);
        userInput.setSelection(userInput.getText().length());

        final HomeController renameController = new HomeController(new VmrResponseListener.OnRenameItemListener() {
            @Override
            public void onRenameItemSuccess(JSONObject jsonObject) {
                try {
                    if (jsonObject.has("Response") && jsonObject.getString("Response").equals("success")) {
                        VmrDebug.printLogI(FragmentMyRecords.this.getClass(), record.getRecordName() + " renamed.");
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
                Snackbar.make(getActivity().findViewById(R.id.clayout), record.getRecordName() + " restored!", Snackbar.LENGTH_SHORT);

        final Snackbar snackBarOnOk =
                Snackbar.make(getActivity().findViewById(R.id.clayout), record.getRecordName() + " renamed",Snackbar.LENGTH_LONG)
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
                        String suffix;
                        if(record.getRecordName().contains(".")) {
                            suffix = "." + record.getRecordName().substring(1, record.getRecordName().lastIndexOf('.'));
                        } else {
                            suffix = "";
                        }
                        String completeName = userInput.getText().toString() + suffix;
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
            Snackbar.make(getActivity().findViewById(R.id.clayout), "Can not modify system folders", Snackbar.LENGTH_SHORT).show();
        } else {
            alertDialog.show();
        }
    }

    @Override
    public void onDownloadClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Download button clicked" );
        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");
            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
            recordStack.push(record.getNodeRef());
            refreshFolder();
            mSwipeRefreshLayout.setRefreshing(true);
        } else {

            if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                VmrDebug.printLogI(this.getClass(),record.getRecordName() + " File clicked");

                final int notificationId = new Random().nextInt();

                final Notification.Builder downloadingNotification =
                        new Notification.Builder(getActivity())
                            .setContentTitle(record.getRecordName())
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setProgress(0,0,true)
                            .setOngoing(true);

                DownloadTask.ProgressListener progressListener
                        = new DownloadTask.ProgressListener() {
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
                        VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Progress " +  progressPercent);

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

                                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "File saved");
                                Snackbar.make(getActivity().findViewById(R.id.clayout), newFile.getName() + " downloaded", Snackbar.LENGTH_SHORT).show();

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
                Snackbar.make(getActivity().findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PermissionHandler.requestPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_CODE_WRITE_STORAGE);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onMoveClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Move button clicked" );
        Pair<Record, Integer> clip = new Pair<>(record, 2 ); // 2-Move, 3-Copy
        Vmr.setClipBoard(clip);
        Snackbar.make(getActivity().findViewById(R.id.clayout), "File copied in clipboard", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCopyClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Copy button clicked" );
        Pair<Record, Integer> clip = new Pair<>(record, 3 ); // 2-Move, 3-Copy
        Vmr.setClipBoard(clip);
        Snackbar.make(getActivity().findViewById(R.id.clayout), "File copied in clipboard", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPasteClicked(Record record) {
        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Paste button clicked" );

        if(Vmr.getClipBoard() !=  null){
            pasteRecord(recordStack.peek());
        } else {
            Snackbar.make(getActivity().findViewById(R.id.clayout), "Clipboard is empty", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void pasteRecord(final String parentNode){

        final boolean[] isCanceled = {false};

        final HomeController copyController =  new HomeController(new VmrResponseListener.OnCopyItemListener() {
            @Override
            public void onCopyItemSuccess(JSONObject jsonObject) {
                VmrDebug.printLogI(this.getClass(), "Copy success" );
                refreshFolder();
            }

            @Override
            public void onCopyItemFailure(VolleyError error) {
                VmrDebug.printLogI(this.getClass(), "Copy failed" );
            }
        });

        final HomeController moveController =  new HomeController(new VmrResponseListener.OnMoveItemListener() {
            @Override
            public void onMoveItemSuccess(JSONObject jsonObject) {
                VmrDebug.printLogI(this.getClass(), "Move success" );
                refreshFolder();
            }

            @Override
            public void onMoveItemFailure(VolleyError error) {
                VmrDebug.printLogI(this.getClass(), "Move failed" );
            }
        });

        final Pair<Record, Integer> clipBoard = Vmr.getClipBoard();

        final Snackbar moveSnackBar
                = Snackbar
                .make(getActivity().findViewById(R.id.clayout), clipBoard.first.getRecordName() + " moved", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar
                                .make(getActivity().findViewById(R.id.clayout), "Action canceled", Snackbar.LENGTH_SHORT)
                                .show();
                        isCanceled[0] = true;
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if(!isCanceled[0]) {
                            Record parent = dbManager.getRecord(parentNode);
                            VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Parent record name " + parent.getRecordName());
                            moveController.moveItem(clipBoard.first, parent);
                        }
                    }
                });

        final Snackbar copySnackBar
                = Snackbar
                .make(getActivity().findViewById(R.id.clayout), clipBoard.first.getRecordName() + " pasted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar
                                .make(getActivity().findViewById(R.id.clayout), "Action canceled", Snackbar.LENGTH_SHORT)
                                .show();
                        isCanceled[0] = true;
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if(!isCanceled[0]) {
                            Record parent = dbManager.getRecord(parentNode);
                            copyController.copyItem(clipBoard.first, parent);
                        }
                    }
                });

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        switch (clipBoard.second){
                            case 2: { // Move
                                moveSnackBar.show();
                                break;
                            }
                            case 3: {
                                copySnackBar.show();
                                break;
                            }
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    @Override
    public void onDuplicateClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Duplicate button clicked" );

        final HomeController duplicateController =  new HomeController(new VmrResponseListener.OnCopyItemListener() {
            @Override
            public void onCopyItemSuccess(JSONObject jsonObject) {
                refreshFolder();
            }

            @Override
            public void onCopyItemFailure(VolleyError error) {
                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Duplicate request failed." );
            }
        });
        final boolean[] isCanceled = {false};
        Snackbar.make(getActivity().findViewById(R.id.clayout), "New copy of " + record.getRecordName() + " created", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(getActivity().findViewById(R.id.clayout), "Action canceled", Snackbar.LENGTH_SHORT)
                                .show();
                        isCanceled[0] = true;
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if(!isCanceled[0]) {
                            Record parent = dbManager.getRecord(recordStack.peek());
                            duplicateController.copyItem(record, parent);
                        }
                    }
                })
                .show();
    }

    @Override
    public void onPropertiesClicked(Record record) {
        VmrDebug.printLogI(this.getClass(), "Properties button clicked" );
//        Snackbar.make(getActivity().findViewById(R.id.clayout), "This feature is not available.", Snackbar.LENGTH_SHORT).show();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        final RecordDetailsController recordDetailsController = new RecordDetailsController(new RecordDetailsController.OnFetchRecordDetailsListener() {
            @Override
            public void onFetchRecordDetailsSuccess(RecordDetails recordDetails) {
                progressDialog.dismiss();
                new AlertDialog
                    .Builder(getActivity())
                    .setMessage(recordDetails.getRecordName() + " details fetched")
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
    public void onMoveToTrashClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Delete button clicked" );
        final HomeController trashController = new HomeController(new VmrResponseListener.OnMoveToTrashListener() {
            @Override
            public void onMoveToTrashSuccess(List<DeleteMessage> deleteMessages) {
                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success")) {
                        VmrDebug.printLogI(FragmentMyRecords.this.getClass(), dm.getObjectType() + " " + dm.getName() + " deleted");
                        dbManager.moveRecordToTrash(record);
                    }
                }
            }

            @Override
            public void onMoveToTrashFailure(VolleyError error) {
                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Can't delete. File may be already removed.");
                refreshFolder();
            }

        });

        if(record.getRecordOwner().equalsIgnoreCase("admin") ) {
            Snackbar.make(getActivity().findViewById(R.id.clayout), "Can not delete system folders", Snackbar.LENGTH_SHORT).show();
        } else {
            final boolean[] isCanceled = {false};
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.clayout), record.getRecordName() + " moved to Trash", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(getActivity().findViewById(R.id.clayout), record.getRecordName() + " restored!", Snackbar.LENGTH_SHORT);
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
        VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
        mFabAddItem.show();
    }

    private void setupFab(View view){
        mFabAddItem = (FloatingActionButton) view.findViewById(fab);

        if(!PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID).equals("admin")) {
            mFabAddItem.setVisibility(View.VISIBLE);
            mFabAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFabAddItem.hide();
                    addItemMenu.show(getActivity().getSupportFragmentManager(), addItemMenu.getTag());
                }
            });
        } else {
            mFabAddItem.setVisibility(View.GONE);
        }
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP && switchToParent();
            }
        });
    }

    private boolean switchToParent() {
        VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
        if (!recordStack.peek().equals(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF))) {
            recordStack.pop();
            if (recordStack.peek().equals(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF))) {
                fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.MY_RECORDS);
                fragmentInteractionListener.setBackButton(false);
            } else {
                fragmentInteractionListener.onFragmentInteraction(dbManager.getRecord(recordStack.peek()).getRecordName());
            }

            VmrDebug.printLogI(FragmentMyRecords.this.getClass(), dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp()+"");

            if ( dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp() != null) {
                if (dbManager.getRecord(recordStack.peek()).getLastUpdateTimestamp().before(new Date(System.currentTimeMillis() - 60 * 1000))) {
                    refreshFolder();
                    mSwipeRefreshLayout.setRefreshing(true);
                    VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Folder refreshed.");
                } else {
                    records = dbManager.getAllRecords(recordStack.peek());
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
                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "Folder refreshed.");
            }
            return true;
        } else {
            return false;
        }
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvMyRecords);

        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recordsAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                VmrDebug.printLogI(FragmentMyRecords.this.getClass(), "newState->"+newState);
                if (newState == 0){
                    mFabAddItem.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                } else {
                    mFabAddItem.animate().translationY(mFabAddItem.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
                }
            }
        });
    }

    private void refreshFolder(){
        homeController.fetchAllFilesAndFolders(recordStack.peek());
    }

    @Override
    public void onHomeClick() {
        VmrDebug.printLogI(this.getClass(), "Home button clicked.");
        switchToParent();
    }

    @Override
    public void onPasteClick() {
        if (DEBUG) VmrDebug.printLogI(this.getClass(), "Paste clicked" );
        pasteRecord(recordStack.peek());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
        void setBackButton(boolean value);
    }
}