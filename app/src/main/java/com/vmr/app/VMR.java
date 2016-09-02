package com.vmr.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class VMR extends Application {

    private static VMR appInstance;
    private static Context appContext;
    private static VmrFolder vmrRootFolder;
    private static VmrFolder vmrSharedWithMeRootFolder;
    private static VmrFolder vmrSharedByMeRootFolder;
    private static UserInfo loggedInUserInfo;
    private static Map<String, String > userMap;

    public static UserInfo getLoggedInUserInfo() {
        return loggedInUserInfo;
    }

    public static void setLoggedInUserInfo(UserInfo loggedInUserInfo) {
        VMR.loggedInUserInfo = loggedInUserInfo;
    }

    public static VmrFolder getVmrRootFolder() {
        return vmrRootFolder;
    }

    public static void setVmrRootFolder(VmrFolder vmrRootFolder) {
        VMR.vmrRootFolder = vmrRootFolder;
    }

    public static VmrFolder getVmrSharedWithMeRootFolder() {
        return vmrSharedWithMeRootFolder;
    }

    public static void setVmrSharedWithMeRootFolder(VmrFolder vmrSharedWithMeRootFolder) {
        VMR.vmrSharedWithMeRootFolder = vmrSharedWithMeRootFolder;
    }

    public static VmrFolder getVmrSharedByMeRootFolder() {
        return vmrSharedByMeRootFolder;
    }

    public static void setVmrSharedByMeRootFolder(VmrFolder vmrSharedByMeRootFolder) {
        VMR.vmrSharedByMeRootFolder = vmrSharedByMeRootFolder;
    }

    public static Map<String, String> getUserMap() {
        return userMap;
    }

    public static void setUserMap( Map<String , String > map) {
        userMap = map;
    }

    public static VMR getInstance() {
        return appInstance;
    }

    public static Context getVMRContext(){
        return VMR.appContext;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VMR.appContext = getApplicationContext();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
