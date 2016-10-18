package com.vmr.utils;

/*
 * Created by abhijit on 9/13/16.
 */

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.vmr.app.Vmr;

public class PermissionHandler {

    public static boolean checkPermission(String permission){
        int result = ContextCompat.checkSelfPermission(Vmr.getVMRContext(), permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permission, int requestCode ){
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
//        }
    }
}
