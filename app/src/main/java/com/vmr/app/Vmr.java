package com.vmr.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Pair;

import com.vmr.db.DbManager;
import com.vmr.db.record.Record;
import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class Vmr extends Application {

    private static Vmr appInstance;
    private static Context appContext;
    private static String AlfrescoTicket;
    private static VmrFolder vmrRootFolder;
    private static VmrFolder vmrSharedWithMeRootFolder;
    private static VmrFolder vmrSharedByMeRootFolder;
    private static Pair<Record, Integer> clipBoard;
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

    public static Pair<Record, Integer>  getClipBoard() {
        return clipBoard;
    }

    public static void setClipBoard(Pair<Record, Integer> clipBoard) {
        Vmr.clipBoard = clipBoard;
    }

    public static void clearClipBoard() {
        Vmr.clipBoard = null;
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

    public static String getAlfrescoTicket() {
        return AlfrescoTicket;
    }

    public static void setAlfrescoTicket(String alfrescoTicket) {
        AlfrescoTicket = alfrescoTicket;
    }

    public static void resetApp(){
        Vmr.setAlfrescoTicket(null);
        Vmr.setLoggedInUserInfo(null);
        Vmr.setDbManager(null);
        Vmr.setUserMap(null);
        Vmr.setVmrRootFolder(null);
        Vmr.setVmrSharedByMeRootFolder(null);
        Vmr.setVmrSharedWithMeRootFolder(null);
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
