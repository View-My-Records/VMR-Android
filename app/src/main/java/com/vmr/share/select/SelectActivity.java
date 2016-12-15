package com.vmr.share.select;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.controller.HomeController;
import com.vmr.model.VmrFolder;
import com.vmr.network.VolleySingleton;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.share.select.adapter.FolderAdapter;
import com.vmr.utils.Constants;

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

    String rootNodeRef;
    Uri fileUri;

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
        launcherIntent.putExtra(FILE_URI, fileUri);
        return launcherIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Select folder");

        Intent intent = getIntent();
        rootNodeRef = intent.getStringExtra(NODE_REF);
        fileUri = intent.getParcelableExtra(FILE_URI);

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
            case R.id.action_select:
                // TODO: 12/14/16 Code to upload file to current folder and create notification
                Toast.makeText(this, "File will be uploaded here", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_create_new_folder:
                // TODO: 12/14/16 Code to create new folder
                Toast.makeText(this, "New folder will be created here", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_cancel:
                // TODO: 12/14/16 Cancel action
                Toast.makeText(this, "File upload cancel", Toast.LENGTH_SHORT).show();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshFolder();
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
                getSupportActionBar().setTitle("Select folder");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setTitle(record.getRecordName());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            if (record.getLastUpdateTimestamp()
                    .before(new Date(System.currentTimeMillis() - 60 * 1000))) {
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
            } else {
                records = dbManager.getFolders(recordStack.peek());
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
                    records = dbManager.getFolders(recordStack.peek());
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
    }

    @Override
    public void onItemLongClick(Record record) {
        // TODO: 12/14/16 Select folder on long click
        VmrDebug.printLogI(this.getClass(), "Folder long clicked.");
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        VmrDebug.printLogI(this.getClass(), "Records retrieved.");


        dbManager.removeAllRecords(recordStack.peek(), vmrFolder);

        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));


        dbManager.updateTimestamp(recordStack.peek());

        records = dbManager.getFolders(recordStack.peek());

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