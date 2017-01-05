package com.vmr.screen.search;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.model.Classification;
import com.vmr.model.SearchResult;
import com.vmr.network.controller.DownloadTaskController;
import com.vmr.network.controller.SearchController;
import com.vmr.network.controller.request.DownloadTask;
import com.vmr.screen.search.adapter.ResultAdapter;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResultActivity
        extends AppCompatActivity
        implements
        SearchController.OnFetchResultsListener,
        ResultAdapter.OnItemClickListener,
        SearchView.OnQueryTextListener{


    String location;
    String nodeRef;
    String recordName;
    String isFolder;

    String queryString;
    String docType;

    SwitchCompat mSwitch;
    Spinner spDocType;

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView mTextViewNoResults;

    SearchController searchController;
    ResultAdapter resultAdapter;

    List<SearchResult> searchResults = new ArrayList<>();
    private TextView mTextViewResultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        searchController = new SearchController(this);
        resultAdapter = new ResultAdapter(searchResults, this);

        setupToolBar();
        setupRecyclerView();

        Intent intent  = getIntent();
        String intentDataString = intent.getDataString();
        String[] parts;
        if(intentDataString != null) {
            parts = intentDataString.split("#");
            location     =  parts[0];
            nodeRef      =  parts[1];
            recordName   =  parts[2];
            isFolder     =  parts[3];
        }
        queryString = intent.getExtras().getString(SearchManager.QUERY);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            VmrDebug.printLogI(this.getClass(), "-------------------Action Search");
            VmrDebug.printLogI(this.getClass(), "-----Query->" + queryString);
            initiateSearch();
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            VmrDebug.printLogI(this.getClass(), "-------------------Action View");
            VmrDebug.printLogI(this.getClass(), "-----Location->"+location);
            VmrDebug.printLogI(this.getClass(), "-----NodeRef->"+nodeRef);
            VmrDebug.printLogI(this.getClass(), "-----RecordName->"+recordName);
            VmrDebug.printLogI(this.getClass(), "-----IsFolder->"+isFolder);
//            getRecord();
        }
    }

    private void setupToolBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwitch = (SwitchCompat) findViewById(R.id.switchWidget);
        spDocType = (Spinner) findViewById(R.id.spDocType);
        spDocType.setEnabled(false);
        mSwitch.setChecked(false);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spDocType.setEnabled(true);
                } else {
                    spDocType.setEnabled(false);
                }
            }
        });

        final List<String> docTypes = new ArrayList<>();
        docTypes.add(0, "Document Type");
        docTypes.addAll(Classification.getDocumentTypes().keySet());
        ArrayAdapter<String> docTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, docTypes);
        docTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDocType.setAdapter(docTypeAdapter);
        docType = "vmr_doc";
        spDocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map docTypeMap = Classification.getDocumentTypes();
                docType = (String) docTypeMap.get(docTypes.get(position));
                initiateSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupRecyclerView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvSearchResults);
        mTextViewNoResults = (TextView) findViewById(R.id.tvNoResults);
        mTextViewResultCount = (TextView) findViewById(R.id.tvResultCount);
        mSwipeRefreshLayout.setDistanceToTriggerSync(50);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateSearch();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(resultAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_actionbar_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);

        searchView.setIconifiedByDefault(true);
        if(queryString != null) {
            searchView.setQuery(queryString, false);
            searchView.setIconified(false);
        }
        searchView.requestFocus();

        return true;
    }

    @Override
    public void onFetchResultsSuccess(List<SearchResult> results) {

        VmrDebug.printLogI(this.getClass(), results.size() + " results received");
        mSwipeRefreshLayout.setRefreshing(false);
//        resultAdapter.setSearchResults(results);
        resultAdapter.updateDataset(results);

        if(results.size() == 0){
            mRecyclerView.setVisibility(View.GONE);
            mTextViewResultCount.setVisibility(View.GONE);
            mTextViewNoResults.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextViewResultCount.setVisibility(View.VISIBLE);
            mTextViewResultCount.setText( results.size()+" results found for '" + queryString + "'" );
            mTextViewNoResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchResultsFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(SearchResult result) {
        VmrDebug.printLogI(this.getClass(), result.getRecordName() + " clicked.");
        getFile(result);
    }

    private void getFile(final SearchResult result){
        final Record record = Record.getRecordObjectForResult(result);

        if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            final DownloadTaskController downloadTaskController;

            final ProgressDialog downloadProgress = new ProgressDialog(this);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setMessage("Downloading " + record.getRecordName());
                        }
                    });
                }

                @Override
                public void onDownloadFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setMessage("Downloading failed");
                        }
                    });
                }

                @Override
                public void onDownloadCanceled() {
                    runOnUiThread(new Runnable() {
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
                        Toast.makeText(SearchResultActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
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
            Snackbar.make(findViewById(android.R.id.content), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(SearchResultActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        queryString = query;
        initiateSearch();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    private void initiateSearch(){
        if(mSwitch.isChecked()) {
            if(docType == null || docType.isEmpty()){
                Toast.makeText(this, "Please select document type", Toast.LENGTH_SHORT).show();
            } else {
                searchController.fetchResults(queryString, PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF), docType, true);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        } else {
            searchController.fetchResults(queryString, PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF), "vmr_doc", true);
            mSwipeRefreshLayout.setRefreshing(true);
        }

    }
}
