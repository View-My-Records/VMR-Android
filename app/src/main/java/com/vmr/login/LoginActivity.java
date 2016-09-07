package com.vmr.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.db.DbManager;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.login.interfaces.OnLoginClickListener;
import com.vmr.model.UserInfo;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

public class LoginActivity extends AppCompatActivity
        implements
        VmrResponseListener.OnLoginListener,
        VmrResponseListener.OnFetchTicketListener,
        OnLoginClickListener
{
    // Controller for queuing requests
    private LoginController loginController;

    // Variables
    private DbManager dbManager;
    private String username;
    private String password;
    private String accountType;
    private String accountId;
    private boolean remember;
    private UserInfo userDetails;
    private boolean isUserLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginController = new LoginController(this, this);
        dbManager = new DbManager();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapterLogin adapter = new PagerAdapterLogin(getSupportFragmentManager(), this);
        assert viewPager != null;
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!ConnectionDetector.isOnline()){
            PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
            Snackbar.make(findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
        } else {
            loginController.fetchAlfrescoTicket();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ConnectionDetector.isOnline()){
            PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
            Snackbar.make(findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
        } else {
            loginController.fetchAlfrescoTicket();
        }
    }

    @Override
    public void onLoginSuccess(UserInfo userInfo) {
        dbManager.addUser(userInfo);
        onLoginComplete(userInfo);
    }

    @Override
    public void onLoginFailure(VolleyError error) {
        Toast.makeText(this, ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchTicketFailure(VolleyError error) {
        Toast.makeText(this, ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
        PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
    }

    @Override
    public void onFetchTicketSuccess(String ticket) {
        PrefUtils.setSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET, ticket);
        if(isUserLoggedIn) {
            startHomeActivity();
        }
    }

    //flow after login is completed
    private void onLoginComplete(UserInfo userInfo){
        VmrDebug.printLogI(this.getClass(), "Login Success");
        if(remember){
            PrefUtils.setSharedPreference(this, PrefConstants.VMR_USER_USERNAME, this.username);
            PrefUtils.setSharedPreference(this, PrefConstants.VMR_USER_PASSWORD, this.password);
            PrefUtils.setSharedPreference(this, PrefConstants.VMR_USER_ACCOUNT_TYPE, this.accountType);
            PrefUtils.setSharedPreference(this, PrefConstants.VMR_USER_ACCOUNT_ID, this.accountId);
        }

        this.userDetails = userInfo;

        isUserLoggedIn = true;

        if(PrefUtils.getSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET).equals("NA")) {
            loginController.fetchAlfrescoTicket();
        }else {
            startHomeActivity();
        }
    }

    private void startHomeActivity(){
        Intent intent = HomeActivity.getLaunchIntent(this, userDetails);
        startActivity(intent);
        finish();
    }

    // Handle clicks on fragments buttons
    @Override
    public void onIndividualLoginClick(String email, String password, String domain, boolean remember) {
        if(!ConnectionDetector.isOnline()){
            PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
            Snackbar.make(findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
        } else {
            if(PrefUtils.getSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET).equals("NA")) {
                loginController.fetchAlfrescoTicket();
            }
            loginController.fetchIndividualDetail(email, password, domain);
            this.username = email;
            this.password = password;
            this.accountType = "Individual";
            this.accountId = null;
            this.remember = remember;
        }
    }

    @Override
    public void onFamilyLoginClick( String username, String password, String accountId, String domain, boolean remember) {
        if(!ConnectionDetector.isOnline()){
            PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
            Snackbar.make(findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
        } else {
            if (PrefUtils.getSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET).equals("NA")) {
                loginController.fetchAlfrescoTicket();
            }
            loginController.fetchFamilyDetail(username, password, accountId, domain);
            this.username = username;
            this.password = password;
            this.accountType = "Family";
            this.accountId = accountId;
            this.remember = remember;
        }
    }

    @Override
    public void onProfessionalLoginClick(String username, String password, String accountId, String domain, boolean remember) {
        if(!ConnectionDetector.isOnline()){
            PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
            Snackbar.make(findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
        } else {
            if (PrefUtils.getSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET).equals("NA")) {
                loginController.fetchAlfrescoTicket();
            }
            loginController.fetchProfessionalDetail(username, password, accountId, domain);
            this.username = username;
            this.password = password;
            this.accountType = "Professional";
            this.accountId = accountId;
            this.remember = remember;
        }
    }

    @Override
    public void onCorporateLoginClick(String username, String password, String accountId, String domain, boolean remember) {
        if(!ConnectionDetector.isOnline()){
            PrefUtils.clearSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET);
            Snackbar.make(findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
        } else {
            if (PrefUtils.getSharedPreference(this, PrefConstants.VMR_ALFRESCO_TICKET).equals("NA")) {
                loginController.fetchAlfrescoTicket();
            }
            loginController.fetchCorporateDetail(username, password, accountId, domain);
            this.username = username;
            this.password = password;
            this.accountType = "Corporate";
            this.accountId = accountId;
            this.remember = remember;
        }
    }

}
