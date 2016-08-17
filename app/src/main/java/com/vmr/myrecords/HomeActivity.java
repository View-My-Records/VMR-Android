package com.vmr.myrecords;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.vmr.R;
import com.vmr.settings.SettingsActivity;
import com.vmr.myrecords.fragments.FragmentAbout;
import com.vmr.myrecords.fragments.FragmentHelp;
import com.vmr.myrecords.fragments.FragmentMyRecords;
import com.vmr.myrecords.fragments.FragmentOffline;
import com.vmr.myrecords.fragments.FragmentRecentlyAccessed;
import com.vmr.myrecords.fragments.FragmentReports;
import com.vmr.myrecords.fragments.FragmentSharedWithMe;
import com.vmr.myrecords.fragments.FragmentToBeIndexed;
import com.vmr.myrecords.fragments.FragmentTrash;


public class HomeActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        FragmentMyRecords.OnFragmentInteractionListener,
        FragmentOffline.OnFragmentInteractionListener,
        FragmentRecentlyAccessed.OnFragmentInteractionListener,
        FragmentReports.OnFragmentInteractionListener,
        FragmentSharedWithMe.OnFragmentInteractionListener,
        FragmentToBeIndexed.OnFragmentInteractionListener,
        FragmentTrash.OnFragmentInteractionListener,
        FragmentAbout.OnFragmentInteractionListener,
        FragmentHelp.OnFragmentInteractionListener {

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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.my_records);
        View headerView = navigationView.getHeaderView(0);

        ImageButton settingButton = (ImageButton) headerView.findViewById(R.id.action_settings);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getBaseContext() , SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        ImageButton notificationButton = (ImageButton) headerView.findViewById(R.id.action_notifications);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Notifications clicked.", Toast.LENGTH_SHORT).show();
            }
        });

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
        getSupportActionBar().setTitle(string);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page Url is correct.
                // Otherwise, set the Url to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app Url is correct.
                Uri.parse("android-app://com.vmr.vmrdemo/http/host/path")
        );
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page Url is correct.
                // Otherwise, set the Url to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app Url is correct.
                Uri.parse("android-app://com.vmr.vmrdemo/http/host/path")
        );

    }
}
