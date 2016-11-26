package com.vmr.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.custom_view.CustomSearchView;
import com.vmr.db.DbManager;
import com.vmr.db.notification.Notification;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.activity.SearchResultActivity;
import com.vmr.home.controller.DownloadTaskController;
import com.vmr.home.controller.HomeController;
import com.vmr.home.fragments.FragmentAbout;
import com.vmr.home.fragments.FragmentHelp;
import com.vmr.home.fragments.FragmentMyRecords;
import com.vmr.home.fragments.FragmentOffline;
import com.vmr.home.fragments.FragmentRecentlyAccessed;
import com.vmr.home.fragments.FragmentReports;
import com.vmr.home.fragments.FragmentSharedByMe;
import com.vmr.home.fragments.FragmentSharedWithMe;
import com.vmr.home.fragments.FragmentToBeIndexed;
import com.vmr.home.fragments.FragmentTrash;
import com.vmr.home.interfaces.Interaction;
import com.vmr.home.request.DownloadTask;
import com.vmr.inbox.InboxActivity;
import com.vmr.inbox.controller.InboxController;
import com.vmr.model.NotificationItem;
import com.vmr.model.VmrFolder;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.settings.SettingsActivity;
import com.vmr.utils.Constants;
import com.vmr.utils.FileUtils;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.io.File;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        FragmentMyRecords.OnFragmentInteractionListener,
        FragmentOffline.OnFragmentInteractionListener,
        FragmentRecentlyAccessed.OnFragmentInteractionListener,
        FragmentReports.OnFragmentInteractionListener,
        FragmentSharedWithMe.OnFragmentInteractionListener,
        FragmentSharedByMe.OnFragmentInteractionListener,
        FragmentToBeIndexed.OnFragmentInteractionListener,
        FragmentTrash.OnFragmentInteractionListener,
        FragmentAbout.OnFragmentInteractionListener,
        FragmentHelp.OnFragmentInteractionListener,
        VmrResponseListener.OnFetchRecordsListener,
        InboxController.OnFetchInboxListener {

    // Variables
    private String location = null;
    private String nodeRef = null;
    private String recordName = null;
    private String isFolder = null;
    private boolean backPressedOnce = false;
    private InboxController inboxController;

    // Views
    private MenuItem mMenuItemToBeIndexed;
    private ImageButton mButtonNotification;
    private CustomSearchView mSearchView;
    private MenuItem mSearchItem;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    // Interfaces
    private Interaction.HomeToMyRecordsInterface sendToMyRecords;
    private Interaction.OnHomeClickListener homeClickListener;
    private Interaction.OnPasteClickListener pasteClickListener;

    // Database Manager
    private DbManager dbManager;

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.getAction().equals(Intent.ACTION_SEARCH) || intent.getAction().equals(Intent.ACTION_VIEW)) {

            String intentDataString = intent.getDataString();
            String[] parts;

            if (intentDataString != null) {
                parts = intentDataString.split("#");
                location = parts[0];
                nodeRef = parts[1];
                recordName = parts[2];
                isFolder = parts[3];
            }

//            String queryString = intent.getExtras().getString(SearchManager.QUERY);

            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//                VmrDebug.printLogI(this.getClass(), "-------------------Action Search");
//                VmrDebug.printLogI(this.getClass(), "-----Query->" + queryString);
                intent.setClass(this, SearchResultActivity.class);
                startActivity(intent);
            } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//                VmrDebug.printLogI(this.getClass(), "-------------------Action View");
//                VmrDebug.printLogI(this.getClass(), "-----Location->" + location);
//                VmrDebug.printLogI(this.getClass(), "-----NodeRef->" + nodeRef);
//                VmrDebug.printLogI(this.getClass(), "-----RecordName->" + recordName);
//                VmrDebug.printLogI(this.getClass(), "-----IsFolder->" + isFolder);
                getRecord(location);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = FragmentMyRecords.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.home_fragment_holder, fragment).commit();

            dbManager = Vmr.getDbManager();
        }

        setupNavigationDrawer(toolbar);

        if(dbManager.getRecord(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF)).getRecordId() == null) {
            Record newRecord = new Record();
            newRecord.setRecordName("Root");
            newRecord.setRecordNodeRef(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));
            dbManager.addRecord(newRecord);
        }

        HomeController homeController = new HomeController(this);
        homeController.fetchAllFilesAndFolders(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));

        inboxController = new InboxController(this);
        updateNotifications();
    }

    public void updateNotifications(){
        inboxController.fetchNotifications();
    }

    private void setupNavigationDrawer(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_home);

        mActionBarDrawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        mDrawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close){
                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);
                        // Do whatever you want here
                    }
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        // Do whatever you want here
                    }
                };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeClickListener.onHomeClick();
            }
        });

        mActionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.my_records);
        mMenuItemToBeIndexed = navigationView.getMenu().findItem(R.id.to_be_indexed);
        View headerView = navigationView.getHeaderView(0);

        TextView accountName = (TextView) headerView.findViewById(R.id.accountName);
        TextView accountEmail = (TextView) headerView.findViewById(R.id.accountEmail);
        TextView lastLogin = (TextView) headerView.findViewById(R.id.accountLastAccessed);
        ImageButton settingButton = (ImageButton) headerView.findViewById(R.id.action_settings);
        mButtonNotification = (ImageButton) headerView.findViewById(R.id.action_notifications);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                settingsIntent.setAction(Intent.ACTION_VIEW);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                startActivity(settingsIntent);
            }
        });

        mButtonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(HomeActivity.this, InboxActivity.class);
                settingsIntent.setAction(Intent.ACTION_VIEW);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                startActivity(settingsIntent);
            }
        });

        if (PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_MEMBERSHIP_TYPE).equals(Constants.Request.Login.Domain.INDIVIDUAL)) {
            accountName.setText(
                    PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_FIRST_NAME)
                    + " "
                    + PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_LAST_NAME));
        } else {
            accountName.setText(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_CORP_NAME));
        }

        accountEmail.setText(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_EMAIL));

        lastLogin.setText("Last Login: " + PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_LAST_LOGIN));
    }

    @Override
    public void onBackPressed() {

        VmrDebug.printLogI(this.getClass(), "Back pressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);

        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
                backPressedOnce =false;
            }
        }, 2000);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(Vmr.getClipBoard() == null){
            menu.findItem(R.id.action_paste).setEnabled(false);
        } else {
            menu.findItem(R.id.action_paste).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_actionbar_menu, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (CustomSearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if(null!=searchManager ) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(
                            new ComponentName(this, HomeActivity.class))
                    );
        }
        mSearchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_paste) {
            pasteClickListener.onPasteClick();
            return true;
        } else if (id == R.id.action_layout) {
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.my_records) {
            fragmentClass = FragmentMyRecords.class;
        } else if (id == R.id.recently_accessed) {
            fragmentClass = FragmentRecentlyAccessed.class;
        } else if (id == R.id.to_be_indexed) {
            fragmentClass = FragmentToBeIndexed.class;
        } else if (id == R.id.shared_with_me) {
            fragmentClass = FragmentSharedWithMe.class;
        } else if (id == R.id.shared_by_me) {
            fragmentClass = FragmentSharedByMe.class;
        } else if (id == R.id.offline) {
            fragmentClass = FragmentOffline.class;
        } else if (id == R.id.reports) {
            fragmentClass = FragmentReports.class;
        } else if (id == R.id.trash) {
            fragmentClass = FragmentTrash.class;
        } else if (id == R.id.about) {
            fragmentClass = FragmentAbout.class;
        } else if (id == R.id.help) {
            fragmentClass = FragmentHelp.class;
        } else if (id == R.id.log_out) {
            Intent restartApp
                    = getBaseContext()
                    .getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            restartApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restartApp);
            finish();
            return true;
        }

        try {
            assert fragmentClass != null;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_fragment_holder, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String string) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(string);
        }
    }

    @Override
    public void setBackButton(boolean isEnabled) {
        if(isEnabled){
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            mActionBarDrawerToggle.syncState();
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            mActionBarDrawerToggle.syncState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSearchView != null) {
            if(mSearchView.isIconified())
            mSearchView.setQuery("", false);
            mSearchView.clearFocus();
            mSearchView.setIconified(true);
            mSearchItem.collapseActionView();
        }
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        vmrFolder.setNodeRef(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));
        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_SHARED_NODE_REF, vmrFolder.getSharedFolder());
        mMenuItemToBeIndexed.setTitle(mMenuItemToBeIndexed.getTitle() + "(" + (vmrFolder.getTotalUnIndexed()) + ")");
        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF)));
        List<Record> records = dbManager.getAllRecords(PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));
        sendToMyRecords.onReceiveFromActivitySuccess(records);
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        sendToMyRecords.onReceiveFromActivityFailure(error);
    }

    public void setSendToMyRecords(Interaction.HomeToMyRecordsInterface sendToMyRecords) {
        this.sendToMyRecords = sendToMyRecords;
    }

    public void setHomeClickListener(Interaction.OnHomeClickListener homeClickListener) {
        this.homeClickListener = homeClickListener;
    }

    public void setPasteClickListener(Interaction.OnPasteClickListener pasteClickListener) {
        this.pasteClickListener = pasteClickListener;
    }

    @Override
    public void onFetchNotificationsSuccess(List<NotificationItem> notificationItemList) {
        if( notificationItemList != null && notificationItemList.size() > 0)
            dbManager.updateAllNotifications(Notification.getNotificationList(notificationItemList));

        List<Notification> notifications = dbManager.getAllUnreadNotifications();
        if(notifications.size() > 0) {
            mButtonNotification.setImageResource(R.drawable.ic_notifications_with_badge_black_24dp);
        } else {
            mButtonNotification.setImageResource(R.drawable.ic_notifications_black_24dp);
        }
    }

    @Override
    public void onFetchNotificationsFailure(VolleyError error) {
//        Toast.makeText(Vmr.getContext(), R.string.toast_error_something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    public void getRecord(String location) {
        switch (location){
            case "records":
                final Record record = dbManager.getRecord(nodeRef);
                getFile(record);
                break;
            case "trash":
//                TrashRecord trashRecord = dbManager.getTrashRecord(nodeRef);
//                getFile(trashRecord);
                Toast.makeText(this, "Can't open the files from trash", Toast.LENGTH_LONG).show();
                break;
            case "shared":
                SharedRecord sharedRecord = dbManager.getSharedRecord(nodeRef);
                getFile(sharedRecord);
                break;
            default:
                break;
        }
    }

    private void getFile(final Record record){
        if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            final DownloadTaskController downloadTaskController;

            final ProgressDialog downloadProgress = new ProgressDialog(HomeActivity.this);
            downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadProgress.setMessage("Downloading " + record.getRecordName());
            downloadProgress.setCancelable(true);
            downloadProgress.setCanceledOnTouchOutside(true);
//            downloadProgress.setMax(100);
            downloadProgress.setIndeterminate(true);

            DownloadTask.DownloadProgressListener progressListener
                    = new DownloadTask.DownloadProgressListener() {
                @Override
                public void onDownloadStarted() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setMessage("Downloading " + record.getRecordName());
                        }
                    });
                }

                @Override
                public void onDownloadFailed() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setMessage("Downloading failed");
                        }
                    });
                }

                @Override
                public void onDownloadCanceled() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
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
                        Toast.makeText(HomeActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            downloadTaskController = new DownloadTaskController(record,progressListener);

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
            Snackbar.make(HomeActivity.this.findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(HomeActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                        }
                    })
                    .show();
        }
    }

    private void getFile(final TrashRecord record){
        if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            final DownloadTaskController downloadTaskController;

            final ProgressDialog downloadProgress = new ProgressDialog(HomeActivity.this);
            downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadProgress.setMessage("Downloading " + record.getRecordName());
            downloadProgress.setCancelable(true);
            downloadProgress.setCanceledOnTouchOutside(true);
            downloadProgress.setMax(100);
            downloadProgress.setIndeterminate(true);

            DownloadTask.DownloadProgressListener progressListener
                    = new DownloadTask.DownloadProgressListener() {
                @Override
                public void onDownloadStarted() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setMessage("Downloading " + record.getRecordName());
                        }
                    });
                }

                @Override
                public void onDownloadFailed() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setMessage("Downloading failed");
                        }
                    });
                }

                @Override
                public void onDownloadCanceled() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
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
                        Toast.makeText(HomeActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            downloadTaskController = new DownloadTaskController(record,progressListener);

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
            Snackbar.make(HomeActivity.this.findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(HomeActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                        }
                    })
                    .show();
        }
    }

    private void getFile(final SharedRecord record){
        if(PermissionHandler.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            final DownloadTaskController downloadTaskController;

            final ProgressDialog downloadProgress = new ProgressDialog(HomeActivity.this);
            downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadProgress.setMessage("Downloading " + record.getRecordName());
            downloadProgress.setCancelable(true);
            downloadProgress.setCanceledOnTouchOutside(true);
//            downloadProgress.setMax(100);
            downloadProgress.setIndeterminate(true);

            DownloadTask.DownloadProgressListener progressListener
                    = new DownloadTask.DownloadProgressListener() {
                @Override
                public void onDownloadStarted() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setMessage("Downloading " + record.getRecordName());
                        }
                    });
                }

                @Override
                public void onDownloadFailed() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadProgress.setMessage("Downloading failed");
                            downloadProgress.dismiss();
                        }
                    });

                }

                @Override
                public void onDownloadCanceled() {
                    HomeActivity.this.runOnUiThread(new Runnable() {
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
                        Toast.makeText(HomeActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            downloadTaskController = new DownloadTaskController(record,progressListener);

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
            Snackbar.make(HomeActivity.this.findViewById(R.id.clayout), "Application needs permission to write to SD Card", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(HomeActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                        }
                    })
                    .show();
        }
    }
}
