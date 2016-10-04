package com.vmr.home.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.notification.Notification;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.NotificationAdapter;
import com.vmr.home.controller.NotificationController;
import com.vmr.model.NotificationItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity
        implements
        NotificationAdapter.OnNotificationClickListener,
        NotificationController.OnFetchNotificationsListener
{

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;

    // Controllers
    private NotificationController notificationControllerController;
    private DbManager dbManager;

    // Variables
    private List<com.vmr.db.notification.Notification> notifications = new ArrayList<>();
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        dbManager = Vmr.getDbManager();
        notificationAdapter = new NotificationAdapter(notifications, this);
        notificationControllerController = new NotificationController(this);

        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        notificationControllerController.fetchNotifications();
        notifications = dbManager.getAllNotifications();
        notificationAdapter.updateDataset(notifications);
    }

    private void setupRecyclerView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) findViewById(R.id.tvEmpty);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvInbox);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);
                notificationControllerController.fetchNotifications();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(notificationAdapter);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onNotificationClick(com.vmr.db.notification.Notification item) {

    }

    @Override
    public void onFetchNotificationsSuccess(List<NotificationItem> notificationItemList) {
        VmrDebug.printLine("Notifications received.");

        if(notificationItemList != null)
            dbManager.updateAllNotifications(Notification.getNotificationList(notificationItemList));

        notifications =dbManager.getAllNotifications();
        notificationAdapter.updateDataset(notifications);

        mSwipeRefreshLayout.setRefreshing(false);

        if(notifications.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchNotificationsFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }
}
