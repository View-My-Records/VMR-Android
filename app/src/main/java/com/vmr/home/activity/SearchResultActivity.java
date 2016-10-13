package com.vmr.home.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vmr.R;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent  = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            String uri = intent.getDataString();
            String location = intent.getStringExtra("intent_extra_data_key");
            Toast.makeText(this, "Action Search: "+ uri + "Location: " + location, Toast.LENGTH_LONG).show();
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            String location = intent.getStringExtra("intent_extra_data_key");
            Toast.makeText(this, "Action View: "+ uri + "Location: " + location, Toast.LENGTH_LONG).show();
        }
    }
}
