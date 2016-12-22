package com.vmr.debug;

import android.util.Log;

/*
 * Created by abhijit on 8/25/16.
 */
public class VmrDebug {

    public static void printLogI(Class c, String message){
        Log.i(c.getSimpleName(), message);
    }

    public static void printLogI( String message){
        Log.i("Info:",message);
    }

    public static void printLogD(Class c, String message){
        Log.d(c.getSimpleName(), message);
    }

    public static void printLogD( String message){
        Log.d("Debug:", message);
    }

    public static void printLogE(Class c, String message){
        Log.e(c.getSimpleName(), message);
    }

    public static void printLogE( String message){
        Log.e("Debug:", message);
    }

    public static void printLogW(Class c, String message){
        Log.w(c.getSimpleName(), message);
    }

    public static void printLogW(String message){
        Log.w("Warning:", message);
    }

    public static void printLine(String message){
        System.out.println(message);
    }

}
