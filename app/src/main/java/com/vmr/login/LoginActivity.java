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
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.BuildConfig;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.login.controller.LoginController;
import com.vmr.login.fragment.dialog.SettingsDialog;
import com.vmr.model.UserInfo;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity
        implements
        LoginController.OnLoginListener,
        LoginPagerAdapter.OnLoginClickListener {

    ProgressDialog loginProgress;
    DbManager dbManager;
    // Controller for queuing requests
    private LoginController loginController;
    private boolean remember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginController = new LoginController(this);
        dbManager = new DbManager();
        Vmr.setDbManager(dbManager);

        if(PrefUtils.getSharedPreference(PrefConstants.BASE_URL).equals("NA")) {
            PrefUtils.setSharedPreference(PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final LoginPagerAdapter adapter = new LoginPagerAdapter(getSupportFragmentManager(), this);
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
            SettingsDialog settingsDialog = new SettingsDialog();
            settingsDialog.show(fm, "Settings");
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Version Code: \n\t" + BuildConfig.VERSION_CODE + "\n\n"
                            + "Commit hash: \n\t" + BuildConfig.VERSION_NAME + "\n\n"
                            + "Description:  \n\t")
                    .show();
        } else if (id == R.id.action_forgot_password) {
            forgotPasswordHandler();
        } else if (id == R.id.action_forgot_username) {
            forgotUsernameHandler();
        }

        return super.onOptionsItemSelected(item);
    }

    private void forgotUsernameHandler() {
        AlertDialog.Builder forgotUsername
                = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setView(getLayoutInflater().inflate(R.layout.alert_dialog_forgot_username, null))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        forgotUsername.show();
    }

    private void forgotPasswordHandler() {
        AlertDialog.Builder forgotPassword
                = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setView(getLayoutInflater().inflate(R.layout.alert_dialog_forgot_password, null))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        forgotPassword.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
            if (!ConnectionDetector.isOnline()) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toast_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
            if (!ConnectionDetector.isOnline()) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toast_internet_unavailable), Snackbar.LENGTH_SHORT).show();
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
                .setMessage("The account you are trying to log in is locked. Please contact VMR admin.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    @Override
    public void onLoginFailure(VolleyError error) {
        loginProgress.dismiss();
        Toast.makeText(this, ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    //flow after login is completed
    private void onLoginComplete(UserInfo userInfo){
        VmrDebug.printLogI(this.getClass(), "Login Success");
        if(remember){
            dbManager.addUser(userInfo);
        }

        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_ID, userInfo.getLoggedinUserId());
        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF, userInfo.getRootNodref());
        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_MEMBERSHIP_TYPE, userInfo.getMembershipType());

        if (userInfo.getMembershipType().equals(Constants.Request.Login.Domain.INDIVIDUAL)) {
            PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_FIRST_NAME, userInfo.getFirstName());
            PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_LAST_NAME, userInfo.getLastName());
        } else {
            PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_CORP_NAME, userInfo.getCorpName());
        }

        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_EMAIL, userInfo.getEmailId());
        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_SESSION_ID, userInfo.getHttpSessionId());

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        PrefUtils.setSharedPreference(PrefConstants.VMR_LOGGED_USER_LAST_LOGIN, df.format(userInfo.getLastLoginTime()));
//        dbManager.closeConnection();
        Intent intent = HomeActivity.getLaunchIntent(this);
        finish();
        startActivity(intent);
    }

    // Handle clicks on fragments buttons
    @Override
    public void onIndividualLoginClick(String email, String password, String domain, boolean remember) {
        if(PrefUtils.getSharedPreference(PrefConstants.CUSTOM_URL).equals(PrefConstants.CustomUrl.CUSTOM)) {
            loginController.fetchCustomIndividualDetail(email, password, domain);
        } else {
            loginController.fetchIndividualDetail(email, password, domain);
        }
        this.remember = remember;
        loginProgress.show();
    }

    @Override
    public void onFamilyLoginClick( String username, String password, String accountId, String domain, boolean remember) {
        if(PrefUtils.getSharedPreference(PrefConstants.CUSTOM_URL).equals(PrefConstants.CustomUrl.CUSTOM)) {
            loginController.fetchCustomFamilyDetail(username, password, accountId, domain);
        } else {
            loginController.fetchFamilyDetail(username, password, accountId, domain);
        }
        this.remember = remember;
        loginProgress.show();
    }

    @Override
    public void onProfessionalLoginClick(String username, String password, String accountId, String domain, boolean remember) {
        if(PrefUtils.getSharedPreference(PrefConstants.CUSTOM_URL).equals(PrefConstants.CustomUrl.CUSTOM)) {
            loginController.fetchCustomProfessionalDetail(username, password, accountId, domain);
        } else {
            loginController.fetchProfessionalDetail(username, password, accountId, domain);
        }
        this.remember = remember;
        loginProgress.show();
    }

    @Override
    public void onCorporateLoginClick(String username, String password, String accountId, String domain, boolean remember) {
        if(PrefUtils.getSharedPreference(PrefConstants.CUSTOM_URL).equals(PrefConstants.CustomUrl.CUSTOM)) {
            loginController.fetchCustomCorporateDetail(username, password, accountId, domain);
        } else {
            loginController.fetchCorporateDetail(username, password, accountId, domain);
        }
        this.remember = remember;
        loginProgress.show();
    }
}
