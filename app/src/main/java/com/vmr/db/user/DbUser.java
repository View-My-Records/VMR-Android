package com.vmr.db.user;

/*
 * Created by abhijit on 9/4/16.
 */

import java.util.Date;

public class DbUser {

    private String serialNo;       // double NOT NULL,
    private String result;        // varchar(50) NOT NULL,
    private String rootNodeRef;// varchar(100) NOT NULL,
    private String urlType;    //varchar(50) NOT NULL,
    private String userType;    //integer NOT NULL,

    private String membershipType;//varchar(50) NOT NULL,
    private String corpName;    // varchar(50) NOT NULL,
    private String emailId;        // varchar(100) NOT NULL,
    private String empType;        // integer NOT NULL,
    private String userId;        // varchar(50) NOT NULL,

    private String sessionId;    // varchar(100) NOT NULL,
    private String userName;    //varchar(50) NOT NULL,
    private String corpId;        //integer NOT NULL,
    private String loggedInUserId;//integer NOT NULL
    private Date   lastLogin;//integer NOT NULL

    private String lastName;
    private String firstName;

    public DbUser() {

    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRootNodeRef() {
        return rootNodeRef;
    }

    public void setRootNodeRef(String rootNodeRef) {
        this.rootNodeRef = rootNodeRef;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
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
}
