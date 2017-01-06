package com.vmr.screen.share.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.vmr.BuildConfig;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.debug.VmrDebug;
import com.vmr.model.UserInfo;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.ForgotPasswordController;
import com.vmr.network.controller.ForgotUsernameController;
import com.vmr.network.controller.LoginController;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.VmrURL;
import com.vmr.screen.login.fragment.dialog.SettingsDialog;
import com.vmr.screen.share.select.SelectActivity;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PermissionHandler;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShareLoginActivity extends AppCompatActivity
        implements
        LoginController.OnLoginListener,
        LoginPagerAdapter.OnLoginClickListener {

    ViewPager viewPager;
    String intentAction;
    String mimeType;
    ArrayList<Uri> uriList;
    Uri uri;
    private NetworkImageView ivLogo;
    private ProgressDialog loginProgress;
    private DbManager dbManager;
    private LoginController loginController;
    private boolean remember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Vmr.getDbManager() != null) {
            dbManager = Vmr.getDbManager();
        } else {
            dbManager = new DbManager();
        }

        Intent intent = getIntent();
        intentAction = intent.getAction();
        mimeType = intent.getType();

        if(intentAction.equals(Intent.ACTION_SEND) && mimeType != null){
            VmrDebug.printLogI(this.getClass(), "Single file uri received");
            handleSend(intent);
        } else if (intentAction.equals(Intent.ACTION_SEND_MULTIPLE) && mimeType != null) {
            VmrDebug.printLogI(this.getClass(), "Multiple file uri received");
            handleSendMultiple(intent);
        } else {
            Toast.makeText(this, "Invalid content. Exiting...", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loginController = new LoginController(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        ivLogo = (NetworkImageView) toolbar.findViewById(R.id.VMRLogo);
        updateLogo();
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        LoginPagerAdapter adapter = new LoginPagerAdapter(getSupportFragmentManager());
        adapter.setOnLoginClickListener(this);
        assert viewPager != null;
        viewPager.setAdapter(adapter);

        loginProgress = new ProgressDialog(this);
        loginProgress.setMessage("Logging in...");
        loginProgress.setCanceledOnTouchOutside(false);
    }

    private void handleSendMultiple(Intent intent) {
        uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        VmrDebug.printLogE(this.getClass(), "Number of files : " + uriList.size());
        for(Uri u:uriList){
            if (u != null) {
                File file = new File(u.getPath());
                VmrDebug.printLogE(this.getClass(), "Path of file : " + file.getAbsolutePath());
            } else {
                VmrDebug.printLogE(this.getClass(), "No URIs extracted");
            }
        }
    }

    private void handleSend(Intent intent) {
        uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        mimeType = getContentResolver().getType(uri);
        if (uri != null) {
            VmrDebug.printLogE(this.getClass(), "Uri of file : " + uri);
            File file = new File(uri.getPath());
            VmrDebug.printLogE(this.getClass(), "Name of file : " + file.getName());
            VmrDebug.printLogE(this.getClass(), "Path of file : " + file.getAbsolutePath());
            VmrDebug.printLogE(this.getClass(), "Size of file : " + file.length());
        } else {
            VmrDebug.printLogE(this.getClass(), "No URIs extracted");
        }
    }

    private void updateLogo() {
        if(PrefUtils.getSharedPreference(PrefConstants.URL_TYPE).equals(PrefConstants.URLType.CUSTOM)) {
            String newUrl = VmrURL.getImageUrl();
            ivLogo.setImageUrl(newUrl, VolleySingleton.getInstance().getImageLoader());
            ivLogo.setErrorImageResId(R.drawable.default_logo);
        } else {
            ivLogo.setDefaultImageResId(R.drawable.default_logo);
        }
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
            settingsDialog.setOnDismissListener(new SettingsDialog.OnDismissListener() {
                @Override
                public void onDismiss() {
                    updateLogo();
                }
            });
            settingsDialog.show(fm, "Settings");
            return true;
        } else if (id == R.id.action_version) {
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

        VmrDebug.printLogI(this.getClass(), "Domain: " + this.viewPager.getCurrentItem());

        View dialogView = View.inflate(ShareLoginActivity.this, R.layout.alert_dialog_forgot_username, null);

        final EditText etEmail = (EditText) dialogView.findViewById(R.id.etEmail);
        final Spinner spAccountType = (Spinner) dialogView.findViewById(R.id.spDomain);

        final Map<String, String > domainMap = new HashMap<>();
        domainMap.put("Individual", "IND");
        domainMap.put("Family", "FAM");
        domainMap.put("Professional", "PROF");
        domainMap.put("Corporate", "CORP");

        final List<String> domainList = new ArrayList<>();
        domainList.add(0, "Select Domain");
        domainList.add(1, "Individual");
        domainList.add(2, "Family");
        domainList.add(3, "Professional");
        domainList.add(4, "Corporate");

        ArrayAdapter<String> domainAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, domainList);
        domainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccountType.setAdapter(domainAdapter);

        final int selection = viewPager.getCurrentItem()+1;
        spAccountType.setSelection(selection);

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ForgotUsernameController controller
                            = new ForgotUsernameController(
                                new ForgotUsernameController.OnForgotUsernameListener() {
                                    @Override
                                    public void onOnForgotUsernameSuccess(JSONObject response) {
                                        VmrDebug.printLogI(ShareLoginActivity.this.getClass(), response.toString());
                                    }

                                    @Override
                                    public void onOnForgotUsernameFailure(VolleyError error) {
                                        VmrDebug.printLogI(ShareLoginActivity.this.getClass(), "Request failed");
                                    }
                                });

                        String email = null;

                        String accountType = domainList.get(spAccountType.getSelectedItemPosition());
                        String accountDomain = domainMap.get(accountType);

                        if(etEmail.getText().toString().equals("")){
                            etEmail.setError("This field is mandatory");
                        } else {
                            email = etEmail.getText().toString();
                        }

//                        VmrDebug.printLogI(LoginActivity.this.getClass(), "Account type: " + accountType);

                        if(email !=  null) {
                            controller.sendRequest(accountDomain, accountType, email);
                        }
                    }
                });

    }

    private void forgotPasswordHandler() {

        VmrDebug.printLogI(this.getClass(), "Domain: " + this.viewPager.getCurrentItem());

        View dialogView = View.inflate(ShareLoginActivity.this, R.layout.alert_dialog_forgot_password, null);

        final EditText etUsername = (EditText) dialogView.findViewById(R.id.etUsername);
        final EditText etEmail = (EditText) dialogView.findViewById(R.id.etEmail);
        final EditText etAccountId = (EditText) dialogView.findViewById(R.id.etAccountId);
        final Spinner spAccountType = (Spinner) dialogView.findViewById(R.id.spDomain);

        final Map<String, String > domainMap = new HashMap<>();
        domainMap.put("Individual", "IND");
        domainMap.put("Family", "FAM");
        domainMap.put("Professional", "PROF");
        domainMap.put("Corporate", "CORP");
        final List<String> domainList = new ArrayList<>();
        domainList.add(0, "Select Domain");
        domainList.add(1, "Individual");
        domainList.add(2, "Family");
        domainList.add(3, "Professional");
        domainList.add(4, "Corporate");

        ArrayAdapter<String> domainAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, domainList);
        domainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccountType.setAdapter(domainAdapter);

        final int selection = viewPager.getCurrentItem()+1;
        spAccountType.setSelection(selection);

        if(selection == 1){
            etAccountId.setEnabled(false);
            etAccountId.setText("(Disabled)");
        } else {
            etAccountId.setEnabled(true);
        }

        spAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position != 1 && position != 0){
                    etAccountId.setEnabled(true);
                    etAccountId.setText("");
                } else {
                    etAccountId.setEnabled(false);
                    etAccountId.setText("(Disabled)");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordController controller
                        = new ForgotPasswordController(new ForgotPasswordController.OnForgotPasswordListener() {
                    @Override
                    public void onOnForgotPasswordSuccess(JSONObject response) {
                        VmrDebug.printLogI(ShareLoginActivity.this.getClass(), response.toString());
                    }

                    @Override
                    public void onOnForgotPasswordFailure(VolleyError error) {
                        VmrDebug.printLogI(ShareLoginActivity.this.getClass(), "Request failed");
                    }
                });

                String accountType = domainList.get(spAccountType.getSelectedItemPosition());
                String accountDomain = domainMap.get(accountType);

//                VmrDebug.printLogI(LoginActivity.this.getClass(), "Account type: " + accountDomain);

                String username = null, email = null, accountId = null;

                if(etUsername.getText().toString().equals("")){
                    etUsername.setError("This field is mandatory");
                } else {
                    username = etUsername.getText().toString();
                }

                if(etEmail.getText().toString().equals("")){
                    etEmail.setError("This field is mandatory");
                } else {
                    email = etEmail.getText().toString();
                }

                if(etAccountId.isEnabled()) {
                    if (etAccountId.getText().toString().equals("")) {
                        etAccountId.setError("This field is mandatory");
                    } else {
                        accountId = etAccountId.getText().toString() + "";
                    }
                }

                if(username != null && email !=  null && accountId != null) {
                    controller.sendRequest(
                            accountDomain,
                            accountType,
                            username,
                            email,
                            accountId);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
            if (!ConnectionDetector.isOnline()) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.toast_internet_unavailable), Snackbar.LENGTH_SHORT).show();
            }
        }
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
        VmrDebug.printLogI(this.getClass(), "Login success");
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
        } else if(userInfo.getResult().equals("login")){
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        } else if(userInfo.getResult().equals("IND_Invalid Login ID / Password. Please try again.")){
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    @Override
    public void onLoginFailure(VolleyError error) {
        VmrDebug.printLogI(this.getClass(), "Login Failed");
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

        Intent intent = null;
        if(intentAction.equals(Intent.ACTION_SEND)) {
            intent = SelectActivity.getLauncherIntent(this, userInfo.getRootNodref(), uri);
            startActivity(intent);
        } else if (intentAction.equals(Intent.ACTION_SEND_MULTIPLE)) {
            intent = SelectActivity.getLauncherIntent(this, userInfo.getRootNodref(), uriList, uriList.size());
            startActivity(intent);
        }
        startActivity(intent);
        finish();
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

    @Override
    public void onFragmentChanged(String domain) {

    }
}
