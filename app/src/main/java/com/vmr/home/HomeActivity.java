package com.vmr.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.vmr.app.VMR;
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
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.model.UserInfo;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.util.Map;

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
        VmrRequest.onFetchRecordsListener
{
    private Interaction.HomeToMyRecordsInterface sendToMyRecords;

    // Models
    private UserInfo userInfo;

    // Views
    MenuItem toBeIndexed;

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
            VMR.setUserInfo(userInfo);

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
        ImageButton settingButton = (ImageButton) headerView.findViewById(R.id.action_settings);
        ImageButton notificationButton = (ImageButton) headerView.findViewById(R.id.action_notifications);

        if (userInfo.getMembershipType().equals(Constants.Request.Domain.INDIVIDUAL)) {
            accountName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        } else {
            accountName.setText(userInfo.getCorpName());
        }

        accountEmail.setText(userInfo.getEmailId());

        Map<String, String> formData = VMR.getUserMap();
        formData.put(Constants.Request.FolderNavigation.ALFRESCO_NODE_REFERENCE,this.getUserInfo().getRootNodref());
        formData.put(Constants.Request.FolderNavigation.PAGE_MODE,Constants.PageMode.LIST_ALL_FILE_FOLDER);
        formData.put(Constants.Request.FolderNavigation.ALFRESCO_TICKET, PrefUtils.getSharedPreference(this.getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
        HomeController homeController = new HomeController(this);
        homeController.fetchAllFilesAndFolders(formData);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_actionbar_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        if (VMR.getVmrRootFolder() == null) {
            VMR.setVmrRootFolder(vmrFolder);
        }
        toBeIndexed.setTitle(toBeIndexed.getTitle() + "(" + (vmrFolder.getTotalUnIndexed()) + ")");
        sendToMyRecords.onReceiveFromActivitySuccess(VMR.getVmrRootFolder());
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), R.string.toast_error_something_went_wrong, Toast.LENGTH_SHORT).show();
        sendToMyRecords.onReceiveFromActivityFailure(error);
    }

    public void setSendToMyRecords(Interaction.HomeToMyRecordsInterface sendToMyRecords) {
        this.sendToMyRecords = sendToMyRecords;
    }
}
