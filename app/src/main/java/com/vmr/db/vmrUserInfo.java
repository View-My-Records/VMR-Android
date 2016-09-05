package com.vmr.db;

/*
 * Created by abhijit on 9/4/16.
 */

public class vmrUserInfo {

    private String ID;              // double NOT NULL CONSTRAINT USER_INFO_pk PRIMARY KEY,
    private String SERIAL_NO;       // double NOT NULL,
    private String RESULT  ;        // varchar(50) NOT NULL,
    private String ROOT_NODE_REF   ;// varchar(100) NOT NULL,
    private String URL_TYPE    ;    //varchar(50) NOT NULL,
    private String USER_TYPE   ;    //integer NOT NULL,
    private String MEMBERSHIP_TYPE ;//varchar(50) NOT NULL,
    private String CORP_NAME   ;    // varchar(50) NOT NULL,
    private String EMAIL_ID;        // varchar(100) NOT NULL,
    private String EMP_TYPE;        // integer NOT NULL,
    private String USER_ID ;        // varchar(50) NOT NULL,
    private String SESSION_ID  ;    // varchar(100) NOT NULL,
    private String USER_NAME   ;    //varchar(50) NOT NULL,
    private String CORP_ID ;        //integer NOT NULL,
    private String LOGGEDIN_USER_ID ;//integer NOT NULL

    public vmrUserInfo() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSERIAL_NO() {
        return SERIAL_NO;
    }

    public void setSERIAL_NO(String SERIAL_NO) {
        this.SERIAL_NO = SERIAL_NO;
    }

    public String getRESULT() {
        return RESULT;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }

    public String getROOT_NODE_REF() {
        return ROOT_NODE_REF;
    }

    public void setROOT_NODE_REF(String ROOT_NODE_REF) {
        this.ROOT_NODE_REF = ROOT_NODE_REF;
    }

    public String getURL_TYPE() {
        return URL_TYPE;
    }

    public void setURL_TYPE(String URL_TYPE) {
        this.URL_TYPE = URL_TYPE;
    }

    public String getUSER_TYPE() {
        return USER_TYPE;
    }

    public void setUSER_TYPE(String USER_TYPE) {
        this.USER_TYPE = USER_TYPE;
    }

    public String getMEMBERSHIP_TYPE() {
        return MEMBERSHIP_TYPE;
    }

    public void setMEMBERSHIP_TYPE(String MEMBERSHIP_TYPE) {
        this.MEMBERSHIP_TYPE = MEMBERSHIP_TYPE;
    }

    public String getCORP_NAME() {
        return CORP_NAME;
    }

    public void setCORP_NAME(String CORP_NAME) {
        this.CORP_NAME = CORP_NAME;
    }

    public String getEMAIL_ID() {
        return EMAIL_ID;
    }

    public void setEMAIL_ID(String EMAIL_ID) {
        this.EMAIL_ID = EMAIL_ID;
    }

    public String getEMP_TYPE() {
        return EMP_TYPE;
    }

    public void setEMP_TYPE(String EMP_TYPE) {
        this.EMP_TYPE = EMP_TYPE;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSESSION_ID() {
        return SESSION_ID;
    }

    public void setSESSION_ID(String SESSION_ID) {
        this.SESSION_ID = SESSION_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getCORP_ID() {
        return CORP_ID;
    }

    public void setCORP_ID(String CORP_ID) {
        this.CORP_ID = CORP_ID;
    }

    public String getLOGGEDIN_USER_ID() {
        return LOGGEDIN_USER_ID;
    }

    public void setLOGGEDIN_USER_ID(String LOGGEDIN_USER_ID) {
        this.LOGGEDIN_USER_ID = LOGGEDIN_USER_ID;
    }
}
