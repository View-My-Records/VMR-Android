package com.vmr.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.login.interfaces.LoginFragmentInterface;
import com.vmr.login.interfaces.LoginRequestInterface;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements LoginRequestInterface, LoginFragmentInterface {

    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginController = new LoginController(this);

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

        if(isOnline()){
            Snackbar.make(findViewById(android.R.id.content), "Internet available.", Snackbar.LENGTH_SHORT ).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Internet not available.", Snackbar.LENGTH_SHORT ).show();
        }

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void fetchIndividualDetailFailure(VolleyError error) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchIndividualDetailSuccess(JSONObject jsonObject) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchFamilyDetailFailure(VolleyError error) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchFamilyDetailSuccess(JSONObject jsonObject) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchProfessionalDetailFailure(VolleyError error) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchProfessionalDetailSuccess(JSONObject jsonObject) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchCorporateDetailFailure(VolleyError error) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchCorporateDetailSuccess(JSONObject jsonObject) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }


    // Handle clicks on fragments buttons
    @Override
    public void onIndividualLoginClick(String email, String password, String domain) {
        loginController.fetchIndividualDetail(email, password, domain);
    }

    @Override
    public void onFamilyLoginClick(String email, String password, String name, String domain) {
        loginController.fetchFamilyDetail(email, password, name, domain);
    }

    @Override
    public void onProfessionalLoginClick(String email, String password, String name, String domain) {
        loginController.fetchProfessionalDetail(email, password, name, domain);
    }

    @Override
    public void onCorporateLoginClick(String email, String password, String name, String domain) {
        loginController.fetchCorporateDetail(email, password, name, domain);
    }
}
