package com.vmr.login.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.UserInfo;

/*
 * Created by abhijit on 8/16/16.
 */

public interface LoginRequestInterface {

    void onLoginSuccess(UserInfo userInfo);
    void onLoginFailure(VolleyError error);

    void onFetchTicketSuccess(String ticket);
    void onFetchTicketFailure(VolleyError error);

}
