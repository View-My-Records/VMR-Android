package com.vmr.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.vmr.model.folder_structure.VmrFolder;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class VMR extends Application {

    private static VMR appInstance;
    private static Context appContext;
    private static VmrFolder rootVmrFolder;

    public static VmrFolder getRootVmrFolder() {
        return rootVmrFolder;
    }

    public static void setRootVmrFolder(VmrFolder rootVmrFolder) {
        VMR.rootVmrFolder = rootVmrFolder;
    }

    private static Map<String, String > userMap;

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
//        appInstance = this;
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
