package com.vmr.screen.share.select;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.vmr.model.VmrFolder;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.HomeController;
import com.vmr.network.controller.VmrResponseListener;
import com.vmr.network.controller.request.Constants;
import com.vmr.screen.share.select.adapter.FolderAdapter;
import com.vmr.service.UploadService;
import com.vmr.utils.ErrorMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class SelectActivity
        extends AppCompatActivity
        implements FolderAdapter.OnItemClickListener,
        VmrResponseListener.OnFetchRecordsListener {

    public static final String NODE_REF = "noderef";
    public static final String FILE_URI = "file_uri";
    public static final String NUMBER_OF_URI = "uri_number";

    String rootNodeRef;
    int numberOfFiles;

    Uri uri;
    ArrayList<Uri> uriList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mTextView;

    DbManager  dbManager;
    HomeController homeController;
    private Stack<String> recordStack;
    private List<Record> records = new ArrayList<>();
    private FolderAdapter folderAdapter;
    private boolean backPressedOnce;

    public static Intent getLauncherIntent(Context context,
                                           String nodeRef,
                                           Uri fileUri){
        Intent launcherIntent = new Intent(context, SelectActivity.class);
        launcherIntent.putExtra(NODE_REF, nodeRef);
        launcherIntent.putExtra(NUMBER_OF_URI, 1);
        launcherIntent.putExtra(FILE_URI, fileUri);
        launcherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return launcherIntent;
    }

    public static Intent getLauncherIntent(Context context,
                                           String nodeRef,
                                           ArrayList<Uri> uris,
                                           int length){
        Intent launcherIntent = new Intent(context, SelectActivity.class);
        launcherIntent.putExtra(NODE_REF, nodeRef);
        launcherIntent.putParcelableArrayListExtra(FILE_URI, uris);
        launcherIntent.putExtra(NUMBER_OF_URI, length);
        launcherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return launcherIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Select folder");

        Intent intent = getIntent();
        rootNodeRef = intent.getStringExtra(NODE_REF);
        numberOfFiles = intent.getIntExtra(NUMBER_OF_URI, 0);
        VmrDebug.printLogI(this.getClass(), "Number of files " + numberOfFiles);
        if(numberOfFiles == 1) {
            uri = intent.getParcelableExtra(FILE_URI);
            VmrDebug.printLogI(this.getClass(), "File uri " + uri);
        } else if(numberOfFiles > 1){
            uriList = intent.getParcelableArrayListExtra(FILE_URI);
            VmrDebug.printLogI(this.getClass(), "File uri " + uriList.get(0));
        } else {
            Toast.makeText(this, "No files received", Toast.LENGTH_SHORT).show();
        }

        if(Vmr.getDbManager() != null){
            dbManager = Vmr.getDbManager();
        } else {
            dbManager = new DbManager();
        }

        homeController = new HomeController(this);
        folderAdapter = new FolderAdapter(records, this);

        setupRecyclerView();

        recordStack = new Stack<>();
        recordStack.push(rootNodeRef);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!recordStack.peek().equals(rootNodeRef)) {
                    switchToParent();
                }
                return true;
            case R.id.action_upload:
                Toast.makeText(this, "File will be uploaded here", Toast.LENGTH_SHORT).show();
                handleUpload(recordStack.peek());
                return true;
            case R.id.action_create_new_folder:
                Toast.makeText(this, "New folder will be created here", Toast.LENGTH_SHORT).show();
                createNewFolder();
                return true;
            case R.id.action_cancel:
                Toast.makeText(this, "File upload cancel", Toast.LENGTH_SHORT).show();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleUpload(String parentNodeRef) {
        try {
            if (numberOfFiles == 1) {
                dbManager.queueUpload(uri, parentNodeRef);
            } else if (numberOfFiles > 1) {
                for (int i = 0; i < numberOfFiles; i++) {
                    dbManager.queueUpload(uriList.get(i), parentNodeRef);
                }
            }
            initiateUpload();
            Toast.makeText(Vmr.getContext(), "Upload started", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(Vmr.getContext(), "Couldn't find the file", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void initiateUpload(){
        Intent uploadIntent = new Intent(this, UploadService.class);
        Vmr.getContext().startService(uploadIntent);
    }

    private void createNewFolder() {
        VmrDebug.printLogI(this.getClass(), "create folder button clicked" );
        final View promptsView = View.inflate(this, R.layout.dialog_fragment_create_folder, null);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewFolderName);

        final HomeController createFolderController
                = new HomeController(new VmrResponseListener.OnCreateFolderListener() {
            @Override
            public void onCreateFolderSuccess(JSONObject jsonObject) {
                try {
                    if (jsonObject.has("result") && jsonObject.getString("result").equals("success")) {
//                        Toast.makeText(Vmr.getContext(), "New folder created.", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(true);
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
                Snackbar.make(findViewById(android.R.id.content), "New folder created",Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(findViewById(android.R.id.content), "Canceled", Snackbar.LENGTH_SHORT)
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
                = new AlertDialog.Builder(this)
                .setView(promptsView)
                .setCancelable(false)
                .setTitle("Create New Folder")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
    protected void onStart() {
        super.onStart();
        refreshFolder();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onBackPressed() {
        VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
        if (!recordStack.peek().equals(rootNodeRef)) {
            switchToParent();
            return;
        } else if (backPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.backPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2000);
    }

    private void switchToParent() {
        recordStack.pop();
        Record record = dbManager.getRecord(recordStack.peek());
        if ( record.getLastUpdateTimestamp() != null) {
            if(recordStack.peek().equals(rootNodeRef)){
                //noinspection ConstantConditions
                getSupportActionBar().setTitle("Select folder");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(record.getRecordName());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            if (record.getLastUpdateTimestamp()
                    .before(new Date(System.currentTimeMillis() - 60 * 1000))) {
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
            } else {
                records = dbManager.getAllRecords(recordStack.peek());
                folderAdapter.updateDataset(records);
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
        }
    }

    private void setupRecyclerView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

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

        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(folderAdapter);
    }

    private void refreshFolder() {
        homeController.fetchAllFilesAndFolders(recordStack.peek());
    }

    @Override
    public void onItemClick(Record record) {
        if(record.isFolder()){
            VmrDebug.printLogI(this.getClass(),record.getRecordName() + " Folder clicked");

            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);

            recordStack.push(record.getNodeRef());
            record = dbManager.getRecord(recordStack.peek());
            getSupportActionBar().setTitle(record.getRecordName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if ( record.getLastUpdateTimestamp() != null) {
                if (record.getLastUpdateTimestamp().before(new Date(System.currentTimeMillis() - 5* 60 * 1000))) {
                    refreshFolder();
                    mSwipeRefreshLayout.setRefreshing(true);
                } else {
                    records = dbManager.getAllRecords(recordStack.peek());
                    folderAdapter.updateDataset(records);
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
            }
        } else {
            Toast.makeText(this, "Please select a folder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClick(Record record) {
        if(record.isFolder()) {
            handleUpload(record.getNodeRef());
        } else {
            Toast.makeText(this, "Please select a folder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        VmrDebug.printLogI(this.getClass(), "Records retrieved.");


        dbManager.removeAllRecords(recordStack.peek(), vmrFolder);

        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));


        dbManager.updateTimestamp(recordStack.peek());

        records = dbManager.getAllRecords(recordStack.peek());

        folderAdapter.updateDataset(records);

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
    }
}