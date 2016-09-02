package com.vmr.utils;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.vmr.network.error.AuthenticationError;
import com.vmr.network.error.FetchError;
import com.vmr.network.error.ParseError;
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
        } else if(error instanceof FetchError){
            return "Failed to process request";
        } else if(error instanceof ParseError){
            return "Failed to parse response";
        } else {
            return "Something went wrong.";
        }
    }
}
