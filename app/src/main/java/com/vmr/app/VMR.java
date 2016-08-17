package com.vmr.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/*
 * Created by abhijit on 8/16/16.
 */

public class VMR extends Application {

    private static VMR appInstance;

    public static VMR getInstance() {
        return appInstance;
    }

    public static Context getContext(){
        return appInstance;
    }

    public static Context getVMRContext(){
        return appInstance;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        appInstance = this;
        super.onCreate();
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
