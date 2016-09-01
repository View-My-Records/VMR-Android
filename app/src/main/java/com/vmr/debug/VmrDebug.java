package com.vmr.debug;

import android.content.Context;
import android.util.Log;

import com.vmr.app.VMR;

/**
 * Created by abhijit on 8/25/16.
 */
public class VmrDebug {

    public static void printLogI(Context context, String message){
        Log.i(context.getClass().getSimpleName(), message);
    }

    public static void printLogI( String message){
        Log.i("Info:",message);
    }

    public static void printLogD(Context context, String message){
        Log.i(context.getClass().getSimpleName(), message);
    }

    public static void printLogD( String message){
        Log.i("Debug:", message);
    }

    public static void printLogW(Context context, String message){
        Log.i(context.getClass().getSimpleName(), message);
    }

    public static void printLogW( String message){
        Log.i("Warning:", message);
    }

    public static void printLine(String message){
        System.out.println(message);
    }

}
