package com.vmr.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.vmr.R;
import com.vmr.db.record.Record;
import com.vmr.utils.Constants;

public class ViewActivity extends AppCompatActivity {


    public static Intent getLauncherIntent(Context context, Record record){
        Intent intent = new Intent(context, ViewActivity.class);
        intent.putExtra(Constants.Key.RECORD, record);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Record record = intent.getParcelableExtra(Constants.Key.RECORD);

        getSupportActionBar().setTitle(record.getRecordName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
