package com.vmr.db;

/*
 * Created by abhijit on 9/6/16.
 */
public class DbConstants {
    //DB Constants
    public static final String DATABASE_NAME = "vmr.db";
    public static final int VERSION = 4;

    //Table Name
    public static final String TABLE_USER = "USER";
    public static final String TABLE_RECORD = "RECORD";

    //Column Names
    public static final String USER_SERIAL_NO         = "SERIAL_NO";
    public static final String USER_RESULT            = "RESULT";
    public static final String USER_ROOT_NODE_REF     = "ROOT_NODE_REF";
    public static final String USER_URL_TYPE          = "URL_TYPE";
    public static final String USER_TYPE              = "USER_TYPE";
    public static final String USER_MEMBERSHIP_TYPE   = "MEMBERSHIP_TYPE";
    public static final String USER_CORP_NAME         = "CORP_NAME";
    public static final String USER_EMAIL_ID          = "EMAIL_ID";
    public static final String USER_EMP_TYPE          = "EMP_TYPE";
    public static final String USER_ID                = "USER_ID";
    public static final String USER_SESSION_ID        = "SESSION_ID";
    public static final String USER_USER_NAME         = "USERNAME";
    public static final String USER_CORP_ID           = "CORP_ID";
    public static final String USER_LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";
    public static final String USER_LAST_LOGIN        = "LAST_LOGIN_TIME";
    public static final String USER_FIRST_NAME        = "FIRST_NAME";
    public static final String USER_LAST_NAME         = "LAST_NAME";

    //All Columns
    public static final String[] USER_COLUMNS
            = {
                USER_SERIAL_NO,
                USER_RESULT,
                USER_ROOT_NODE_REF,
                USER_URL_TYPE,
                USER_TYPE,
                USER_MEMBERSHIP_TYPE,
                USER_CORP_NAME,
                USER_EMAIL_ID,
                USER_EMP_TYPE,
                USER_ID,
                USER_SESSION_ID,
                USER_USER_NAME,
                USER_CORP_ID,
                USER_LOGGED_IN_USER_ID,
                USER_LAST_NAME,
                USER_FIRST_NAME,
                USER_LAST_LOGIN };

    //Column Names
    public static final String RECORD_NODE_REF        = "RECORD_NODE_REF";
    public static final String RECORD_PARENT_NODE_REF = "PARENT_NODE_REF";
    public static final String RECORD_NAME            = "RECORD_NAME";
    public static final String RECORD_DOC_TYPE        = "RECORD_DOC_TYPE ";
    public static final String RECORD_FOLDER_CATEGORY = "FOLDER_CATEGORY";
    public static final String RECORD_FILE_CATEGORY   = "FILE_CATEGORY ";
    public static final String RECORD_FILE_SIZE       = "FILE_SIZE";
    public static final String RECORD_FILE_MIME_TYPE  = "FILE_MIME_";
    public static final String RECORD_IS_FOLDER       = "IS_FOLDER";
    public static final String RECORD_IS_SHARED       = "IS_SHARED ";
    public static final String RECORD_IS_WRITABLE     = "IS_WRITABLE";
    public static final String RECORD_IS_DELETABLE    = "IS_DELETABLE";
    public static final String RECORD_OWNER           = "ITEM_OWNER";
    public static final String RECORD_CREATED_BY      = "CREATED_BY";
    public static final String RECORD_CREATION_DATE   = "CREATED_DATE";
    public static final String RECORD_UPDATED_BY      = "UPDATED_BY";
    public static final String RECORD_UPDATE_DATE     = "UPDATED_DATE";

    //All Columns
    public static final String[] RECORD_COLUMNS
            = {
                RECORD_NODE_REF ,
                RECORD_PARENT_NODE_REF ,
                RECORD_NAME ,
                RECORD_DOC_TYPE ,
                RECORD_FOLDER_CATEGORY ,
                RECORD_FILE_CATEGORY ,
                RECORD_FILE_SIZE ,
                RECORD_FILE_MIME_TYPE ,
                RECORD_IS_FOLDER ,
                RECORD_IS_SHARED ,
                RECORD_IS_WRITABLE ,
                RECORD_IS_DELETABLE ,
                RECORD_OWNER ,
                RECORD_CREATED_BY ,
                RECORD_CREATION_DATE ,
                RECORD_UPDATED_BY ,
                RECORD_UPDATE_DATE };

}
