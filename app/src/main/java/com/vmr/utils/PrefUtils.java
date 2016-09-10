package com.vmr.utils;

/*
 * Created by abhijit on 8/17/16.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

//    private static SharedPreferences getSharedPreferences(){
//        SharedPreferences sharedpreferences;
//        sharedpreferences = VMR.getVMRContext()
//                .getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
//        return sharedpreferences;
//    }
//
//    private static SharedPreferences.Editor getSharedPreferencesEditor(){
//        return PrefUtils.getSharedPreferences().edit();
//    }
//
//    public static void setUsername(String username){
//        getSharedPreferencesEditor().putString(PrefConstants.VMR_USER_USERNAME, username);
//        getSharedPreferencesEditor().commit();
//    }
//
//    public static void setPassword(String password){
//        getSharedPreferencesEditor().putString(PrefConstants.VMR_USER_PASSWORD, password);
//        getSharedPreferencesEditor().commit();
//    }
//
//    public static void setType(String type){
//        getSharedPreferencesEditor().putString(PrefConstants.VMR_USER_ACCOUNT_TYPE, type);
//        getSharedPreferencesEditor().commit();
//    }
//
//    public static void setId(String id){
//        getSharedPreferencesEditor().putString(PrefConstants.VMR_USER_ACCOUNT_ID, id);
//        getSharedPreferencesEditor().commit();
//    }
//
//    public static void setAlfrescoTicket(String ticket){
//        getSharedPreferencesEditor().putString(PrefConstants.VMR_ALFRESCO_TICKET, ticket);
//        getSharedPreferencesEditor().commit();
//    }
//    public static String getAlfrescoTicket(){
//        return getSharedPreferences().getString(PrefConstants.VMR_ALFRESCO_TICKET, null);
//
//    }
//
//    public static void deleteAlfrescoTicket() {
//        getSharedPreferencesEditor().remove(PrefConstants.VMR_ALFRESCO_TICKET);
//    }

    public static void setSharedPreference(Context context, String key, String value){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString(key,value);
        prefEditor.apply();
    }

    public static String getSharedPreference(Context context, String key){
        String value;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
        value = sharedPreferences.getString(key, "NA");
        return value;
    }

    public static void clearSharedPreference(Context context, String key){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(PrefConstants.VMR_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.remove(key);
    }



}
