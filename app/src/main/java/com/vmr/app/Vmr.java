package com.vmr.app;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import com.vmr.db.DbManager;
import com.vmr.db.record.Record;

import java.util.Map;

/*
 * Created by abhijit on 8/16/16.
 */

public class Vmr extends Application {

    private static Vmr vmrInstance;
    private static Pair<Record, Integer> clipBoard;

    private static DbManager dbManager;
    private static Map<String, String > userMap;


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

    public static Context getContext(){
        return vmrInstance.getApplicationContext();
    }

    public static DbManager getDbManager() {
        return dbManager;
    }

    public static void setDbManager(DbManager dbManager) {
        Vmr.dbManager = dbManager;
    }

    @Override
    public void onCreate() {
        vmrInstance = this;
        Vmr.dbManager = new DbManager();
        super.onCreate();
    }
}
