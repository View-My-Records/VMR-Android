package com.vmr.utils;

/*
 * Created by abhijit on 8/17/16.
 */

public class PrefConstants {

    public static final String VMR_PREFERENCES   = "VMR_PREFERENCES";

    public static final String APPLICATION_MODE    = "APPLICATION_MODE";
    public static final String CUSTOM_URL    = "CUSTOM_URL";
    public static final String URL_TYPE             = "URL_TYPE";
    public static final String BASE_URL          = "DEFAULT_BASE_URL";
    public static final String VMR_USER_USERNAME = "VMR_USER_USERNAME";
    public static final String VMR_USER_PASSWORD = "VMR_USER_PASSWORD";
    public static final String VMR_USER_ACCOUNT_TYPE = "VMR_USER_ACCOUNT_TYPE";
    public static final String VMR_USER_ACCOUNT_ID = "VMR_USER_ACCOUNT_ID";
    public static final String VMR_ALFRESCO_TICKET = "VMR_ALFRESCO_TICKET";

    public static final String VMR_LOGGED_USER_ID = "VMR_LOGGED_USER_ID";
    public static final String VMR_LOGGED_USER_ROOT_NODE_REF = "VMR_LOGGED_USER_ROOT_NODE_REF";
    public static final String VMR_LOGGED_USER_SHARED_NODE_REF = "VMR_LOGGED_USER_SHARED_NODE_REF";

    public static final String VMR_LOGGED_USER_MEMBERSHIP_TYPE = "VMR_LOGGED_USER_MEMBERSHIP_TYPE";
    public static final String VMR_LOGGED_USER_FIRST_NAME = "VMR_LOGGED_USER_FIRST_NAME";
    public static final String VMR_LOGGED_USER_LAST_NAME = "VMR_LOGGED_USER_LAST_NAME";
    public static final String VMR_LOGGED_USER_CORP_NAME = "VMR_LOGGED_USER_CORP_NAME";
    public static final String VMR_LOGGED_USER_EMAIL = "VMR_LOGGED_USER_EMAIL";
    public static final String VMR_LOGGED_USER_LAST_LOGIN = "VMR_LOGGED_USER_LAST_LOGIN";
    public static final String VMR_LOGGED_USER_SESSION_ID = "VMR_LOGGED_USER_SESSION_ID";

    public static class ApplicationMode {
        public final static String ONLINE = "ONLINE";
        public final static String OFFLINE = "OFFLINE";
    }

    public static class CustomUrl {
        public final static String STANDARD = "STANDARD";
        public final static String CUSTOM = "CUSTOM";
    }

    public static class URLType {
        public final static String STANDARD         = "STANDARD";
        public final static String CUSTOM           = "CUSTOM";
        public final static String VIEW_MY_RECORDS  = "VMRPORTAL";
    }
}
