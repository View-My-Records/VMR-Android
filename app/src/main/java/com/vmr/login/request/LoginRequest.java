package com.vmr.login.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.model.UserInfo;
import com.vmr.network.JSONNetworkRequest;
import com.vmr.network.NetworkRequest;
import com.vmr.utils.Constants;
import com.vmr.utils.WebApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/19/16.
 */

public class LoginRequest extends NetworkRequest<UserInfo> {

    private Map<String, String> formData;

    public LoginRequest(
            Map<String, String> formData,
            Response.Listener<UserInfo> successListener,
            Response.ErrorListener errorListener) {
        super( Method.POST,Constants.Url.LOGIN, successListener, errorListener);
        this.formData = formData;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.formData;
    }

    @Override
    protected Response<UserInfo> parseNetworkResponse(NetworkResponse response) {

        String jsonString = new String(response.data);
        JSONObject jsonObject;
        UserInfo userInfo = new UserInfo();

        try {
            jsonObject = new JSONObject(jsonString);

            userInfo.setSlNo(jsonObject.getString(WebApiConstants.JSON_USER_INFO_SLNO          ));
            userInfo.setResult(jsonObject.getString(WebApiConstants.JSON_USER_INFO_RESULT        ));
            userInfo.setRootNodref(jsonObject.getString(WebApiConstants.JSON_USER_INFO_ROOTNODREF    ));
            userInfo.setUrlType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_URLTYPE       ));
            userInfo.setUserType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_USERTYPE      ));
            userInfo.setMembershipType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_MEMBERSHIPTYPE));
            userInfo.setEmailId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_EMAILID       ));
            userInfo.setEmpType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_EMPTYPE       ));
            userInfo.setUserId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_USERID        ));
            userInfo.setHttpSessionId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_HTTPSESSIONID ));
            userInfo.setUserName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_USERNAME      ));
            userInfo.setLoggedinUserId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_LOGGEDINUSERID));

            if(userInfo.getMembershipType().equalsIgnoreCase(Constants.Request.Domain.INDIVIDUAL) ){
                userInfo.setLastName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_LASTNAME));
                userInfo.setFirstName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_FIRSTNAME));
            }else {
                userInfo.setCorpName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_CORPNAME));
                userInfo.setCorpId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_CORPID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new VolleyError("Invalid Username or Password"));
        }
        return Response.success(userInfo, getCacheEntry());
    }
}
