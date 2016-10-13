package com.vmr.home.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.vmr.R;
import com.vmr.debug.VmrDebug;

public class SearchResultActivity extends AppCompatActivity {


    String location;
    String nodeRef;
    String recordName;
    String isFolder;
    String queryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
//            Toast.makeText(this, "Action Search: "+ uri + "Location: " + location, Toast.LENGTH_LONG).show();
            VmrDebug.printLogI(this.getClass(), "-------------------Action Search");
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//            Toast.makeText(this, "Action View: "+ uri + "Location: " + location, Toast.LENGTH_LONG).show();
            VmrDebug.printLogI(this.getClass(), "-------------------Action View");
        }

        VmrDebug.printLogI(this.getClass(), "Query String->" + queryString);
        VmrDebug.printLogI(this.getClass(), "Location->" + location);
        VmrDebug.printLogI(this.getClass(), "NodeRef->" + nodeRef);
        VmrDebug.printLogI(this.getClass(), "Record Name->" + recordName);
        VmrDebug.printLogI(this.getClass(), "isFolder->" + isFolder);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getExtras().getString(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_actionbar_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(
                    new ComponentName(this, SearchResultActivity.class))
            );
        }
        searchView.setIconifiedByDefault(true);
        if(queryString != null) {
            searchView.setQuery(queryString, false);
            searchView.setIconified(false);
        }
        searchView.requestFocus();

        return true;
    }
}
