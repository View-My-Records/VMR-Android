package com.vmr.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.vmr.db.DbManager;
import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class Vmr extends Application {

    private static Vmr appInstance;
    private static Context appContext;
    private static VmrFolder vmrRootFolder;
    private static VmrFolder vmrSharedWithMeRootFolder;
    private static VmrFolder vmrSharedByMeRootFolder;
    private static UserInfo loggedInUserInfo;
    private static DbManager dbManager;
    private static Map<String, String > userMap;

    public static UserInfo getLoggedInUserInfo() {
        return loggedInUserInfo;
    }

    public static void setLoggedInUserInfo(UserInfo loggedInUserInfo) {
        Vmr.loggedInUserInfo = loggedInUserInfo;
    }

    public static VmrFolder getVmrRootFolder() {
        return vmrRootFolder;
    }

    public static void setVmrRootFolder(VmrFolder vmrRootFolder) {
        Vmr.vmrRootFolder = vmrRootFolder;
    }

    public static VmrFolder getVmrSharedWithMeRootFolder() {
        return vmrSharedWithMeRootFolder;
    }

    public static void setVmrSharedWithMeRootFolder(VmrFolder vmrSharedWithMeRootFolder) {
        Vmr.vmrSharedWithMeRootFolder = vmrSharedWithMeRootFolder;
    }

    public static VmrFolder getVmrSharedByMeRootFolder() {
        return vmrSharedByMeRootFolder;
    }

    public static void setVmrSharedByMeRootFolder(VmrFolder vmrSharedByMeRootFolder) {
        Vmr.vmrSharedByMeRootFolder = vmrSharedByMeRootFolder;
    }

    public static Map<String, String> getUserMap() {
        return userMap;
    }

    public static void setUserMap( Map<String , String > map) {
        userMap = map;
    }

    public static Vmr getInstance() {
        return appInstance;
    }

    public static Context getVMRContext(){
        return Vmr.appContext;
    }

    public static DbManager getDbManager() {
        return dbManager;
    }

    public static void setDbManager(DbManager dbManager) {
        Vmr.dbManager = dbManager;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Vmr.appContext = getApplicationContext();
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
