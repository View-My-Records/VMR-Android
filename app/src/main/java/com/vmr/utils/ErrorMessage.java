package com.vmr.utils;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.vmr.network.error.AuthenticationError;
import com.vmr.network.error.TicketError;

/*
 * Created by abhijit on 8/21/16.
 */

public class ErrorMessage {
     public static String show(VolleyError error){
        if(error instanceof AuthenticationError){
            return "Authentication failure";
        } else if(error instanceof TimeoutError){
            return "Server timeout";
        } else if(error instanceof TicketError){
            return "Failed to retrieve ticket";
        } else {
            return "Something went wrong.";
        }
    }
}
