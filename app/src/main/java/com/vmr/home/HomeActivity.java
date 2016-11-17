package com.vmr.home;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
import com.vmr.db.DbManager;
import com.vmr.db.notification.Notification;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.activity.SearchResultActivity;
import com.vmr.home.controller.DownloadController;
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
import com.vmr.home.request.DownloadRequest;
import com.vmr.inbox.InboxActivity;
import com.vmr.inbox.controller.InboxController;
import com.vmr.model.NotificationItem;
import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.settings.SettingsActivity;
import com.vmr.utils.Constants;
import com.vmr.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        InboxController.OnFetchInboxListener
//        SearchView.OnQueryTextListener,
//        SearchView.OnCloseListener,
//        SearchView.OnSuggestionListener
{
    MenuItem searchItem;
    String location = null;
    String nodeRef = null;
    String recordName = null;
    String isFolder = null;
    // Views
    private MenuItem toBeIndexed;
    private ImageButton notificationButton;
    private SearchView searchView;
    // Variables
    private boolean backPressedOnce = false;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private InboxController inboxController;
    private Interaction.HomeToMyRecordsInterface sendToMyRecords;
    private Interaction.OnHomeClickListener homeClickListener;
    private Interaction.OnPasteClickListener pasteClickListener;
    // Models
    private UserInfo userInfo;
    private DbManager dbManager;
//    private SearchSuggestionAdapter mSearchViewAdapter;

    public static Intent getLaunchIntent(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(Constants.Key.USER_DETAILS, userInfo);
        return intent;
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

            String queryString = intent.getExtras().getString(SearchManager.QUERY);

            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                VmrDebug.printLogI(this.getClass(), "-------------------Action Search");
                VmrDebug.printLogI(this.getClass(), "-----Query->" + queryString);
                intent.setClass(this, SearchResultActivity.class);
                startActivity(intent);
            } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                VmrDebug.printLogI(this.getClass(), "-------------------Action View");
                VmrDebug.printLogI(this.getClass(), "-----Location->" + location);
                VmrDebug.printLogI(this.getClass(), "-----NodeRef->" + nodeRef);
                VmrDebug.printLogI(this.getClass(), "-----RecordName->" + recordName);
                VmrDebug.printLogI(this.getClass(), "-----IsFolder->" + isFolder);
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

            userInfo = getIntent().getParcelableExtra(Constants.Key.USER_DETAILS);

            dbManager = Vmr.getDbManager();
            if(userInfo!=null) {
                Vmr.setLoggedInUserInfo(userInfo);
            }
        }

        setupNavigationDrawer(toolbar);

        if(dbManager.getRecord(Vmr.getLoggedInUserInfo().getRootNodref()).getRecordId() == null){
            Record newRecord = new Record();
            newRecord.setRecordName("Root");
            newRecord.setRecordNodeRef(Vmr.getLoggedInUserInfo().getRootNodref());
            dbManager.addRecord(newRecord);
        }

        HomeController homeController = new HomeController(this);
        homeController.fetchAllFilesAndFolders(Vmr.getLoggedInUserInfo().getRootNodref());

        inboxController = new InboxController(this);
        updateNotifications();
    }

    public void updateNotifications(){
        inboxController.fetchNotifications();
    }

    private void setupNavigationDrawer(Toolbar toolbar) {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_home);

        drawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
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

        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeClickListener.onHomeClick();
            }
        });

        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.my_records);
        toBeIndexed = navigationView.getMenu().findItem(R.id.to_be_indexed);
        View headerView = navigationView.getHeaderView(0);

        TextView accountName = (TextView) headerView.findViewById(R.id.accountName);
        TextView accountEmail = (TextView) headerView.findViewById(R.id.accountEmail);
        TextView lastLogin = (TextView) headerView.findViewById(R.id.accountLastAccessed);
        ImageButton settingButton = (ImageButton) headerView.findViewById(R.id.action_settings);
        notificationButton = (ImageButton) headerView.findViewById(R.id.action_notifications);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                settingsIntent.setAction(Intent.ACTION_VIEW);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(settingsIntent);
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(HomeActivity.this, InboxActivity.class);
                settingsIntent.setAction(Intent.ACTION_VIEW);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(settingsIntent);
            }
        });

        if (Vmr.getLoggedInUserInfo().getMembershipType().equals(Constants.Request.Login.Domain.INDIVIDUAL)) {
            accountName.setText(Vmr.getLoggedInUserInfo().getFirstName() + " " + Vmr.getLoggedInUserInfo().getLastName());
        } else {
            accountName.setText(Vmr.getLoggedInUserInfo().getCorpName());
        }

        accountEmail.setText(Vmr.getLoggedInUserInfo().getEmailId());

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.ENGLISH);
        lastLogin.setText("Last Login: " + df.format(Vmr.getLoggedInUserInfo().getLastLoginTime()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);

        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else if(!searchView.isIconified()){
            searchView.setIconified(true);
        } else if (backPressedOnce) {
            super.onBackPressed();
            Vmr.resetApp();
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
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(
                            new ComponentName(this, HomeActivity.class))
                    );
        }
//        searchView.setOnQueryTextListener(this);
//        searchView.setOnCloseListener(this);
        searchView.setIconifiedByDefault(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            Vmr.resetApp();
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
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerToggle.syncState();
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.syncState();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchView!= null) {
            if(searchView.isIconified())
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);
            searchItem.collapseActionView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        if (Vmr.getVmrRootFolder() == null) {
            vmrFolder.setNodeRef(userInfo.getRootNodref());
            Vmr.setVmrRootFolder(vmrFolder);
        }
        toBeIndexed.setTitle(toBeIndexed.getTitle() + "(" + (vmrFolder.getTotalUnIndexed()) + ")");
        dbManager.updateAllRecords(Record.getRecordList(Vmr.getVmrRootFolder().getAll(), Vmr.getLoggedInUserInfo().getRootNodref()));
        List<Record> records = dbManager.getAllRecords(Vmr.getLoggedInUserInfo().getRootNodref());
        sendToMyRecords.onReceiveFromActivitySuccess(records);
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
//        Toast.makeText(Vmr.getVMRContext(), R.string.toast_error_something_went_wrong, Toast.LENGTH_SHORT).show();
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
            notificationButton.setImageResource(R.drawable.ic_notifications_with_badge_black_24dp);
        } else {
            notificationButton.setImageResource(R.drawable.ic_notifications_black_24dp);
        }
    }

    @Override
    public void onFetchNotificationsFailure(VolleyError error) {
//        Toast.makeText(Vmr.getVMRContext(), R.string.toast_error_something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    public void getRecord(String location) {
        switch (location){
            case "records":
                final Record record = Vmr.getDbManager().getRecord(nodeRef);
                getFile(record);
                break;
            case "trash":
                TrashRecord trashRecord = Vmr.getDbManager().getTrashRecord(nodeRef);
                getFile(trashRecord);
                break;
            case "shared":
                SharedRecord sharedRecord = Vmr.getDbManager().getSharedRecord(nodeRef);
                getFile(sharedRecord);
                break;
            default:
                break;
        }
    }

    private void getFile(final Record record){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving file..." + record.getRecordName());
        progressDialog.show();
        DownloadController controller = new DownloadController(new DownloadController.OnFileDownload() {
            @Override
            public void onFileDownloadSuccess(File file) {
                VmrDebug.printLogI(HomeActivity.this.getClass(), "File download complete");
                progressDialog.dismiss();
                try {
                    if (file != null) {
                        final File tempFile = new File(getExternalCacheDir(), record.getRecordName());
                        if (tempFile.exists())
                            tempFile.delete();
                        FileUtils.copyFile(file, tempFile);

                        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                        openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri fileUri = Uri.fromFile(tempFile);
                        openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(tempFile.getAbsolutePath()));
                        startActivity(openFileIntent);
                    } else {
                        VmrDebug.printLogI(HomeActivity.this.getClass(), "null file");
                    }
                } catch (IOException e) {
                    VmrDebug.printLogI(this.getClass(), "File download failed");
                    e.printStackTrace();
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(HomeActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFileDownloadFailure(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Couldn't download the file.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        DownloadRequest.DownloadProgressListener progressListener = new DownloadRequest.DownloadProgressListener() {
            @Override
            public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                progressDialog.setMessage("Receiving file..." + record.getRecordName());
                progressDialog.setProgress(progressPercent);
            }
        };
        controller.downloadFile(record, progressListener);
    }

    private void getFile(final TrashRecord record){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving file...\n" + record.getRecordName());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        DownloadController controller = new DownloadController(new DownloadController.OnFileDownload() {
            @Override
            public void onFileDownloadSuccess(File file) {
                VmrDebug.printLogI(HomeActivity.this.getClass(), "File download complete");
                progressDialog.dismiss();
                try {
                    if (file != null) {
                        final File tempFile = new File(getExternalCacheDir(), record.getRecordName());
                        if (tempFile.exists())
                            tempFile.delete();
                        FileUtils.copyFile(file, tempFile);

                        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                        openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri fileUri = Uri.fromFile(tempFile);
                        openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(tempFile.getAbsolutePath()));
                        startActivity(openFileIntent);
                    } else {
                        VmrDebug.printLogI(HomeActivity.this.getClass(), "null file");
                    }
                } catch (IOException e) {
                    VmrDebug.printLogI(this.getClass(), "File download failed");
                    e.printStackTrace();
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(HomeActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFileDownloadFailure(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Couldn't download the file.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        DownloadRequest.DownloadProgressListener progressListener = new DownloadRequest.DownloadProgressListener() {
            @Override
            public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                progressDialog.setMessage("Receiving file..." + record.getRecordName());
                progressDialog.setProgress(progressPercent);
            }
        };
        controller.downloadFile(record, progressListener);
    }

    private void getFile(final SharedRecord record){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving file..." + record.getRecordName());
        progressDialog.show();
        DownloadController controller = new DownloadController(new DownloadController.OnFileDownload() {
            @Override
            public void onFileDownloadSuccess(File file) {
                VmrDebug.printLogI(HomeActivity.this.getClass(), "File download complete");
                progressDialog.dismiss();
                try {
                    if (file != null) {
                        final File tempFile = new File(getExternalCacheDir(), record.getRecordName());
                        if (tempFile.exists())
                            tempFile.delete();
                        FileUtils.copyFile(file, tempFile);

                        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                        openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri fileUri = Uri.fromFile(tempFile);
                        openFileIntent.setDataAndType(fileUri, FileUtils.getMimeType(tempFile.getAbsolutePath()));
                        startActivity(openFileIntent);
                    } else {
                        VmrDebug.printLogI(HomeActivity.this.getClass(), "null file");
                    }
                } catch (IOException e) {
                    VmrDebug.printLogI(this.getClass(), "File download failed");
                    e.printStackTrace();
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(HomeActivity.this, "No application to view this file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFileDownloadFailure(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Couldn't download the file.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        DownloadRequest.DownloadProgressListener progressListener = new DownloadRequest.DownloadProgressListener() {
            @Override
            public void onDownloadProgress(long fileLength, long transferred, int progressPercent) {
                progressDialog.setMessage("Receiving file..." + record.getRecordName());
                progressDialog.setProgress(progressPercent);
            }
        };
        controller.downloadFile(record, progressListener);
    }
}
