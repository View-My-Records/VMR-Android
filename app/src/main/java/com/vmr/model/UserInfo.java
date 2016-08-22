package com.vmr.model;

/*
 * Created by abhijit on 8/17/16.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.vmr.utils.WebApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Parcelable {

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
    private String lastName;            ;
    private String firstName;


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
    }

    public UserInfo(JSONObject jsonObject) throws JSONException{
        super();
        this.setSlNo(jsonObject.getString(WebApiConstants.JSON_USER_INFO_SLNO          ));
        this.setResult(jsonObject.getString(WebApiConstants.JSON_USER_INFO_RESULT        ));
        this.setRootNodref(jsonObject.getString(WebApiConstants.JSON_USER_INFO_ROOTNODREF    ));
        this.setUrlType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_URLTYPE       ));
        this.setUserType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_USERTYPE      ));
        this.setMembershipType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_MEMBERSHIPTYPE));
        this.setEmailId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_EMAILID       ));
        this.setEmpType(jsonObject.getString(WebApiConstants.JSON_USER_INFO_EMPTYPE       ));
        this.setUserId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_USERID        ));
        this.setHttpSessionId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_HTTPSESSIONID ));
        this.setUserName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_USERNAME      ));
        this.setLoggedinUserId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_LOGGEDINUSERID));

        if(this.getMembershipType().equalsIgnoreCase("IND") ){
            this.setLastName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_LASTNAME));
            this.setFirstName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_FIRSTNAME));
        }else {
            this.setCorpName(jsonObject.getString(WebApiConstants.JSON_USER_INFO_CORPNAME));
            this.setCorpId(jsonObject.getString(WebApiConstants.JSON_USER_INFO_CORPID));
        }
    }

    public UserInfo(){

    }

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

    public String getSlNo() {
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
    }
}
