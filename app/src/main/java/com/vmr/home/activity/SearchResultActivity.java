package com.vmr.home.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.ResultAdapter;
import com.vmr.home.controller.HomeController;
import com.vmr.home.controller.SearchController;
import com.vmr.model.Classification;
import com.vmr.model.SearchResult;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.FileUtils;

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
            location     =   parts[0];
            nodeRef      =    parts[1];
            recordName   = parts[2];
            isFolder     =   parts[3];
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

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            queryString = intent.getExtras().getString(SearchManager.QUERY);
//            Toast.makeText(this, "Searching by: "+ queryString, Toast.LENGTH_SHORT).show();
//            initiateSearch();
//        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//            String uri = intent.getDataString();
//            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_actionbar_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        if(null!=searchManager ) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(
//                    new ComponentName(this, SearchResultActivity.class))
//            );
//        }
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving file...");
        progressDialog.show();
        HomeController controller = new HomeController(new VmrResponseListener.OnFileDownload() {
            @Override
            public void onFileDownloadSuccess(File file) {
                progressDialog.dismiss();
                try {
                    if (file != null) {
                        final File tempFile = new File(SearchResultActivity.this.getExternalCacheDir(), result.getRecordName());
                        if (tempFile.exists())
                            tempFile.delete();
                            FileUtils.copyFile(file, tempFile);

                            Intent openFileIntent = new Intent();
                            openFileIntent.setAction(Intent.ACTION_VIEW);
                            openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Uri fileUri = Uri.fromFile(tempFile);
                            openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(tempFile.getAbsolutePath()));
                            VmrDebug.printLogI(SearchResultActivity.this.getClass(), FileUtils.getMimeType(tempFile.getAbsolutePath()));
                            try {
                                startActivity(openFileIntent);
                                VmrDebug.printLogI(SearchResultActivity.this.getClass(), "File opened.");
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(SearchResultActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                            }
                    } else {
                        VmrDebug.printLogI(SearchResultActivity.this.getClass(), "null file");
                    }
                } catch (Exception e) {
                    VmrDebug.printLogI(this.getClass(), "File download failed");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFileDownloadFailure(VolleyError error) {
                Toast.makeText(SearchResultActivity.this, "Couldn't download the file.", Toast.LENGTH_LONG).show();
            }
        });

        controller.downloadFile(result);
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
                searchController.fetchResults(queryString, Vmr.getLoggedInUserInfo().getRootNodref(), docType, true);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        } else {
            searchController.fetchResults(queryString, Vmr.getLoggedInUserInfo().getRootNodref(), "vmr_doc", true);
            mSwipeRefreshLayout.setRefreshing(true);
        }

    }

//    public void getRecord() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Receiving file...");
//        progressDialog.show();
//
//        switch (location){
//            case "records":
//                final Record record = Vmr.getDbManager().getRecord(nodeRef);
//                HomeController controller = new HomeController(new VmrResponseListener.OnFileDownload() {
//                    @Override
//                    public void onFileDownloadSuccess(File file) {
//                        progressDialog.dismiss();
//                        try {
//                            if (file != null) {
//                                final File tempFile = new File(SearchResultActivity.this.getExternalCacheDir(), record.getRecordName());
//                                if (tempFile.exists() && tempFile.delete()) {
//                                    FileUtils.copyFile(file, tempFile);
//
//                                    Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
//                                    openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    Uri fileUri = Uri.fromFile(tempFile);
//                                    openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(tempFile.getAbsolutePath()));
//                                    try {
//                                        startActivity(openFileIntent);
//                                        SearchResultActivity.this.finish();
//                                    } catch (ActivityNotFoundException e) {
//                                        Toast.makeText(SearchResultActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            } else {
//                                VmrDebug.printLogI(SearchResultActivity.this.getClass(), "null file");
//                                SearchResultActivity.this.finish();
//                            }
//                        } catch (Exception e) {
//                            VmrDebug.printLogI(this.getClass(), "File download failed");
//                            e.printStackTrace();
//                            SearchResultActivity.this.finish();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFileDownloadFailure(VolleyError error) {
//                        Toast.makeText(SearchResultActivity.this, "Couldn't download the file.", Toast.LENGTH_LONG).show();
//                        SearchResultActivity.this.finish();
//                    }
//                });
//
//                controller.downloadFile(record);
//                break;
//            case "trash":
//                TrashRecord trashRecord = Vmr.getDbManager().getTrashRecord(nodeRef);
//                break;
//            case "shared":
//                SharedRecord sharedRecord = Vmr.getDbManager().getSharedRecord(nodeRef);
//                break;
//            default:
//        }
//    }
}
