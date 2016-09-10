package com.vmr.model;

/*
 * Created by abhijit on 8/17/16.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.vmr.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserInfo implements Parcelable {

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
    private String slNo;
    private String result;
    private String rootNodref;
    private String urlType;
    private String userType;
    private String membershipType;
    private String corpName;
    private String emailId;
    private String empType;
    private String userId;
    private String httpSessionId;
    private String userName;
    private String corpId;
    private String loggedinUserId;
    private Date   lastLoginTime;
    private String firstName;
    private String lastName;

    protected UserInfo(Parcel in) {
        slNo = in.readString();
        result = in.readString();
        rootNodref = in.readString();
        urlType = in.readString();
        userType = in.readString();
        membershipType = in.readString();
        corpName = in.readString();
        emailId = in.readString();
        empType = in.readString();
        userId = in.readString();
        httpSessionId = in.readString();
        userName = in.readString();
        corpId = in.readString();
        loggedinUserId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        lastLoginTime = (java.util.Date) in.readSerializable();
    }

    public UserInfo(JSONObject jsonObject) throws JSONException{
        super();
        this.setSlNo(jsonObject.getString(Constants.Response.Login.SERIAL_LNO));
        this.setResult(jsonObject.getString(Constants.Response.Login.RESULT));
        this.setRootNodref(jsonObject.getString(Constants.Response.Login.ROOT_NODE_REF));
        this.setUrlType(jsonObject.getString(Constants.Response.Login.URL_TYPE));
        this.setUserType(jsonObject.getString(Constants.Response.Login.USER_TYPE));
        this.setMembershipType(jsonObject.getString(Constants.Response.Login.MEMBERSHIP_TYPE));
        this.setEmailId(jsonObject.getString(Constants.Response.Login.EMAIL_ID));
        this.setEmpType(jsonObject.getString(Constants.Response.Login.EMPLOYEE_TYPE));
        this.setUserId(jsonObject.getString(Constants.Response.Login.USER_ID));
        this.setHttpSessionId(jsonObject.getString(Constants.Response.Login.SESSION_ID));
        this.setUserName(jsonObject.getString(Constants.Response.Login.USER_NAME));
        this.setLoggedinUserId(jsonObject.getString(Constants.Response.Login.CURRENT_USER_ID));
        this.setLastLoginTime(jsonObject.getString(Constants.Response.Login.LAST_LOGIN_TIME));

        if(this.getMembershipType().equalsIgnoreCase(Constants.Request.Login.Domain.INDIVIDUAL) ){
            this.setLastName(jsonObject.getString(Constants.Response.Login.LAST_NAME));
            this.setFirstName(jsonObject.getString(Constants.Response.Login.FIRST_NAME));
        }else {
            this.setCorpName(jsonObject.getString(Constants.Response.Login.CORP_NAME));
            this.setCorpId(jsonObject.getString(Constants.Response.Login.CORP_ID));
        }
    }

    public UserInfo(){

    }

    public String getSerialNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRootNodref() {
        return rootNodref;
    }

    public void setRootNodref(String rootNodref) {
        this.rootNodref = rootNodref;
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmpType() {
        return empType;
    }

    public void setEmpType(String empType) {
        this.empType = empType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHttpSessionId() {
        return httpSessionId;
    }

    public void setHttpSessionId(String httpSessionId) {
        this.httpSessionId = httpSessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getLoggedinUserId() {
        return loggedinUserId;
    }

    public void setLoggedinUserId(String loggedinUserId) {
        this.loggedinUserId = loggedinUserId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH); //"2016-08-29 20:02:37.879"
        Date result = null;
        try {
            result = df.parse(lastLoginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.lastLoginTime = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(slNo);
        parcel.writeString(result);
        parcel.writeString(rootNodref);
        parcel.writeString(urlType);
        parcel.writeString(userType);
        parcel.writeString(membershipType);
        parcel.writeString(corpName);
        parcel.writeString(emailId);
        parcel.writeString(empType);
        parcel.writeString(userId);
        parcel.writeString(httpSessionId);
        parcel.writeString(userName);
        parcel.writeString(corpId);
        parcel.writeString(loggedinUserId);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeSerializable(lastLoginTime);
    }
}
