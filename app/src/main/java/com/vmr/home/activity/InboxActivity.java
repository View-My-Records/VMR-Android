package com.vmr.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.vmr.R;

public class InboxActivity extends AppCompatActivity {

    private RecyclerView rvInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        rvInbox = (RecyclerView) findViewById(R.id.rvInbox);
    }
}
