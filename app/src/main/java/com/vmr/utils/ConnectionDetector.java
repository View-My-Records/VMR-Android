package com.vmr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vmr.app.Vmr;

/*
 * Created by abhijit on 8/17/16.
 */

public class ConnectionDetector {
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Vmr.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
