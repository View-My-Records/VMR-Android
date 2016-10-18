package com.vmr.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.BuildConfig;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.login.fragment.dialog.LoginSettingsDialog;
import com.vmr.login.interfaces.OnLoginClickListener;
import com.vmr.model.UserInfo;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

public class LoginActivity extends AppCompatActivity
        implements
        VmrResponseListener.OnLoginListener,
        VmrResponseListener.OnFetchTicketListener,
        OnLoginClickListener {

    ProgressDialog loginProgress;
    // Controller for queuing requests
    private LoginController loginController;
    private boolean remember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        loginController = new LoginController(this, this);
        DbManager dbManager = new DbManager();
        Vmr.setDbManager(dbManager);

        if(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL).equals("NA")) {
            PrefUtils.setSharedPreference( Vmr.getVMRContext(), PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapterLogin adapter = new PagerAdapterLogin(getSupportFragmentManager(), this);
        assert viewPager != null;
        viewPager.setAdapter(adapter);

        loginProgress = new ProgressDialog(this);
        loginProgress.setMessage("Logging in...");
        loginProgress.setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            FragmentManager fm = getFragmentManager();
            LoginSettingsDialog settingsDialog = new  LoginSettingsDialog();
            settingsDialog.show(fm, "Settings");
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Version Code: \n\t" + BuildConfig.VERSION_CODE + "\n\n" + "Commit hash: \n\t" + BuildConfig.VERSION_NAME)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
            if (!ConnectionDetector.isOnline()) {
//                Vmr.setAlfrescoTicket(null);
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toast_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            } else {
//                loginController.fetchAlfrescoTicket();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
            if (!ConnectionDetector.isOnline()) {
//                Vmr.setAlfrescoTicket(null);
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toast_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            } else {
//                loginController.fetchAlfrescoTicket();
            }
        }
    }

    @Override
    public void onLoginSuccess(UserInfo userInfo) {
        loginProgress.dismiss();
        if(userInfo.getResult().equals("success")) {
            onLoginComplete(userInfo);
        } else if(userInfo.getResult().contains("locked")) {
            new AlertDialog.Builder(this)
                .setTitle("Locked")
                .setMessage("The account you are tring to log in is locked. Please contact VMR admin.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
    }

    @Override
    public void onLoginFailure(VolleyError error) {
        loginProgress.dismiss();
        Toast.makeText(this, ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchTicketFailure(VolleyError error) {
        Toast.makeText(this, ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchTicketSuccess(String ticket) {
//        Vmr.setAlfrescoTicket(ticket);
        if( Vmr.getLoggedInUserInfo() !=  null ) {
            startHomeActivity();
        }
    }

    //flow after login is completed
    private void onLoginComplete(UserInfo userInfo){
        VmrDebug.printLogI(this.getClass(), "Login Success");
        if(remember){
            Vmr.getDbManager().addUser(userInfo);
        }

        Vmr.setLoggedInUserInfo(userInfo);

//        if(Vmr.getAlfrescoTicket() == null) {
//            loginController.fetchAlfrescoTicket();
//        } else {
            startHomeActivity();
//        }
    }

    private void startHomeActivity(){
        Intent intent = HomeActivity.getLaunchIntent(this, Vmr.getLoggedInUserInfo());
        startActivity(intent);
        finish();
    }

    // Handle clicks on fragments buttons
    @Override
    public void onIndividualLoginClick(String email, String password, String domain, boolean remember) {
//        if() {
//
//        } else {
            loginController.fetchIndividualDetail(email, password, domain);
//        }
        this.remember = remember;

        loginProgress.show();
    }

    @Override
    public void onFamilyLoginClick( String username, String password, String accountId, String domain, boolean remember) {
        loginController.fetchFamilyDetail(username, password, accountId, domain);
        this.remember = remember;
        loginProgress.show();
    }

    @Override
    public void onProfessionalLoginClick(String username, String password, String accountId, String domain, boolean remember) {
        loginController.fetchProfessionalDetail(username, password, accountId, domain);
        this.remember = remember;
        loginProgress.show();
    }

    @Override
    public void onCorporateLoginClick(String username, String password, String accountId, String domain, boolean remember) {
        loginController.fetchCorporateDetail(username, password, accountId, domain);
        this.remember = remember;
        loginProgress.show();
    }

}
