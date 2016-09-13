package com.vmr.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.vmr.app.Vmr;

/*
 * Created by abhijit on 8/17/16.
 */

public class ConnectionDetector {
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Vmr.getVMRContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
