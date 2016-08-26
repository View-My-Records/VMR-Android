package com.vmr.debug;

import android.content.Context;
import android.util.Log;

/**
 * Created by abhijit on 8/25/16.
 */
public class VmrDebug {

    public static void printLogI(Context context, String message){
        Log.i(context.getClass().getSimpleName(), message);
    }

    public static void printLogD(Context context, String message){
        Log.i(context.getClass().getSimpleName(), message);
    }

    public static void printLogW(Context context, String message){
        Log.i(context.getClass().getSimpleName(), message);
    }

    public static void printLine(String message){
        System.out.println(message);
    }

}
