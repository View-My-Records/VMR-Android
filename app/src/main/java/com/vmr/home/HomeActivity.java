package com.vmr.home;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.vmr.login.LoginActivity;
import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;

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
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        SearchView.OnSuggestionListener
{
    // Views
    MenuItem toBeIndexed;
    // Variables
    boolean doubleBackToExitPressedOnce = false;
    private Interaction.HomeToMyRecordsInterface sendToMyRecords;
    // Models
    private UserInfo userInfo;
    private DbManager dbManager;
//    private SearchSuggestionAdapter mSearchViewAdapter;
    private SearchView searchView;

    public static Intent getLaunchIntent(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(Constants.Key.USER_DETAILS, userInfo);
        return intent;
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.my_records);
        toBeIndexed = navigationView.getMenu().findItem(R.id.to_be_indexed);
        View headerView = navigationView.getHeaderView(0);

        TextView accountName = (TextView) headerView.findViewById(R.id.accountName);
        TextView accountEmail = (TextView) headerView.findViewById(R.id.accountEmail);
        TextView lastLogin = (TextView) headerView.findViewById(R.id.accountLastAccessed);
//        ImageButton settingButton = (ImageButton) headerView.findViewById(R.id.action_settings);
//        ImageButton notificationButton = (ImageButton) headerView.findViewById(R.id.action_notifications);

        if (Vmr.getLoggedInUserInfo().getMembershipType().equals(Constants.Request.Login.Domain.INDIVIDUAL)) {
            accountName.setText(Vmr.getLoggedInUserInfo().getFirstName() + " " + Vmr.getLoggedInUserInfo().getLastName());
        } else {
            accountName.setText(Vmr.getLoggedInUserInfo().getCorpName());
        }

        accountEmail.setText(Vmr.getLoggedInUserInfo().getEmailId());

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.ENGLISH);
        lastLogin.setText("Last Login: " + df.format(Vmr.getLoggedInUserInfo().getLastLoginTime()));

        HomeController homeController = new HomeController(this);
        homeController.fetchAllFilesAndFolders(Vmr.getLoggedInUserInfo().getRootNodref());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_actionbar_menu, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(new ComponentName(this, SearchResultActivity.class)));
        }
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
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
            Intent newIntent = new Intent(this,LoginActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
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
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public DbManager getDbManager() {
        return dbManager;
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
        Toast.makeText(Vmr.getVMRContext(), R.string.toast_error_something_went_wrong, Toast.LENGTH_SHORT).show();
        sendToMyRecords.onReceiveFromActivityFailure(error);
    }

    public void setSendToMyRecords(Interaction.HomeToMyRecordsInterface sendToMyRecords) {
        this.sendToMyRecords = sendToMyRecords;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        VmrDebug.printLogI(this.getClass(), "onQueryTextSubmit->" + query);
        SearchRecentSuggestions suggestions =
                new SearchRecentSuggestions(this,
                        SearchHistoryProvider.AUTHORITY,
                        SearchHistoryProvider.MODE);
        suggestions.saveRecentQuery(query, null);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        VmrDebug.printLogI(this.getClass(), "onQueryTextChange->" + newText);

        return false;
    }

    @Override
    public boolean onClose() {
        VmrDebug.printLogI(this.getClass(), "onQueryClose->");
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        VmrDebug.printLogI(this.getClass(), "onSuggestionSelect->");
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {

        Cursor c = searchView.getSuggestionsAdapter().getCursor();
        VmrDebug.printLogI(this.getClass(), "onSuggestionClick->" +c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)));

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.setAction(Intent.ACTION_VIEW);

//        intent.putExtra("id", id);

        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(getIntent())
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);



        startActivity(intent);

        return true;
    }
}
