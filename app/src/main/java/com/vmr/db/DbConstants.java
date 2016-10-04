package com.vmr.db;

/*
 * Created by abhijit on 9/6/16.
 */
public class DbConstants {

    //Table Name
    public static final String TABLE_USER   = "USER";
    public static final String TABLE_RECORD = "RECORD";
    public static final String TABLE_TRASH  = "TRASH";
    public static final String TABLE_SHARED = "SHARED";
    public static final String TABLE_RECENT = "RECENT";
    public static final String TABLE_INBOX  = "INBOX";
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
    public static final String RECORD_ID                    = "RECORD_ID";
    public static final String RECORD_MASTER_OWNER          = "MASTER_OWNER";
    public static final String RECORD_NODE_REF              = "RECORD_NODE_REF";
    public static final String RECORD_PARENT_NODE_REF       = "PARENT_NODE_REF";
    public static final String RECORD_NAME                  = "RECORD_NAME";
    public static final String RECORD_DOC_TYPE              = "RECORD_DOC_TYPE";
    public static final String RECORD_FOLDER_CATEGORY       = "FOLDER_CATEGORY";
    public static final String RECORD_FILE_CATEGORY         = "FILE_CATEGORY";
    public static final String RECORD_FILE_SIZE             = "FILE_SIZE";
    public static final String RECORD_FILE_MIME_TYPE        = "FILE_MIME_TYPE";
    public static final String RECORD_IS_FOLDER             = "IS_FOLDER";
    public static final String RECORD_IS_SHARED             = "IS_SHARED";
    public static final String RECORD_IS_WRITABLE           = "IS_WRITABLE";
    public static final String RECORD_IS_DELETABLE          = "IS_DELETABLE";
    public static final String RECORD_OWNER                 = "ITEM_OWNER";
    public static final String RECORD_CREATED_BY            = "CREATED_BY";
    public static final String RECORD_CREATION_DATE         = "CREATED_DATE";
    public static final String RECORD_UPDATED_BY            = "UPDATED_BY";
    public static final String RECORD_UPDATE_DATE           = "UPDATED_DATE";
    public static final String RECORD_LAST_UPDATE_TIMESTAMP = "LAST_UPDATE_TIMESTAMP";
    public static final String RECORD_IS_AVAILABLE_OFFLINE  = "IS_AVAILABLE_OFFLINE";
    //All Columns
    public static final String[] RECORD_COLUMNS
            = {
                RECORD_ID,
                RECORD_MASTER_OWNER,
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
                RECORD_UPDATE_DATE,
                RECORD_LAST_UPDATE_TIMESTAMP,
                RECORD_IS_AVAILABLE_OFFLINE
    };
    public static final String SHARED_RECORD_ID          = "RECORD_ID";
    public static final String SHARED_MASTER_OWNER       = "MASTER_OWNER";
    public static final String SHARED_NODE_REF           = "NODE_REF";
    public static final String SHARED_PARENT_NODE_REF    = "PARENT_NODE_REF";
    public static final String SHARED_OWNER_NAME         = "OWNER_NAME";
    public static final String SHARED_IS_FOLDER          = "IS_FOLDER";
    public static final String SHARED_RECORD_LIFE        = "RECORD_LIFE";
    public static final String SHARED_TO_EMAIL_ID        = "SHARED_TO_EMAIL_ID";
    public static final String SHARED_USER_ID            = "USER_ID";
    public static final String SHARED_FILE_NAME          = "FILE_NAME";
    public static final String SHARED_PERMISSIONS        = "PERMISSIONS";
    //All Columns
    public static final String[] SHARED_COLUMNS
            = {
            SHARED_RECORD_ID,
            SHARED_MASTER_OWNER,
            SHARED_NODE_REF,
            SHARED_PARENT_NODE_REF,
            SHARED_OWNER_NAME,
            SHARED_IS_FOLDER,
            SHARED_RECORD_LIFE,
            SHARED_TO_EMAIL_ID,
            SHARED_USER_ID,
            SHARED_FILE_NAME,
            SHARED_PERMISSIONS
    };
    public static final String TRASH_RECORD_ID       = "RECORD_ID";
    public static final String TRASH_MASTER_OWNER    = "MASTER_OWNER";
    public static final String TRASH_IS_FOLDER       = "IS_FOLDER";
    public static final String TRASH_CREATED_BY      = "CREATED_BY";
    public static final String TRASH_NAME            = "NAME";
    public static final String TRASH_OWNER           = "OWNER";
    public static final String TRASH_NODE_REF        = "NODE_REF";
    public static final String TRASH_PARENT_NODE_REF = "PARENT_NODE_REF";
    //All Columns
    public static final String[] TRASH_COLUMNS
            = {
            TRASH_RECORD_ID,
            TRASH_MASTER_OWNER,
            TRASH_IS_FOLDER,
            TRASH_CREATED_BY,
            TRASH_NAME,
            TRASH_OWNER,
            TRASH_NODE_REF,
            TRASH_PARENT_NODE_REF
    };
    public static final String RECENT_RECORD_ID       = "RECORD_ID";
    public static final String RECENT_MASTER_OWNER    = "MASTER_OWNER";
    public static final String RECENT_NODE_REF        = "NODE_REF";
    public static final String RECENT_NAME            = "NAME";
    public static final String RECENT_LOCATION        = "LOCATION";
    public static final String RECENT_LAST_ACCESSED   = "LAST_ACCESSED";
    //All Columns
    public static final String[] RECENT_COLUMNS
            = {
            RECENT_RECORD_ID,
            RECENT_MASTER_OWNER,
            RECENT_NODE_REF,
            RECENT_NAME,
            RECENT_LOCATION,
            RECENT_LAST_ACCESSED
    };
    public static final String INBOX_ID           = "INBOX_ID";
    public static final String INBOX_MASTER_OWNER = "MASTER_OWNER";
    public static final String INBOX_TYPE         = "TYPE";
    public static final String INBOX_SUBJECT      = "SUBJECT";
    public static final String INBOX_HAS_BODY     = "HAS_BODY";
    public static final String INBOX_BODY         = "BODY";
    public static final String INBOX_SENDER_FIRST_NAME = "senderFirstName";
    public static final String INBOX_SENDER_LAST_NAME = "senderLastName";
    public static final String INBOX_CREATION_DATE = "CREATED_ON";
    public static final String INBOX_UPDATE_DATE   = "updatedOn";
    //All Columns
    public static final String[] INBOX_COLUMNS
            = {
            INBOX_ID,
            INBOX_MASTER_OWNER,
            INBOX_TYPE,
            INBOX_SUBJECT,
            INBOX_HAS_BODY,
            INBOX_BODY,
            INBOX_CREATION_DATE,
            INBOX_UPDATE_DATE,
            INBOX_SENDER_FIRST_NAME,
            INBOX_SENDER_LAST_NAME
    };
    //DB Constants
    static final String DATABASE_NAME = "vmr.db";
    static final int VERSION = 13;
}
