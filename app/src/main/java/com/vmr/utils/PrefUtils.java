package com.vmr.utils;

/*
 * Created by abhijit on 8/17/16.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.vmr.app.Vmr;

public class PrefUtils {

    public static void setSharedPreference(String key, String value){
        SharedPreferences sharedPreferences;
        sharedPreferences = Vmr.getContext().getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString(key,value);
        prefEditor.apply();
    }

    public static String getSharedPreference(String key){
        String value;
        SharedPreferences sharedPreferences;
        sharedPreferences =  Vmr.getContext().getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
        value = sharedPreferences.getString(key, "NA");
        return value;
    }

    public static void clearSharedPreference(Context context, String key){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.remove(key);
        prefEditor.apply();
    }

}
