package com.vmr.utils;

/*
 * Created by abhijit on 8/16/16.
 */

public class Constants {

    public static final String VMR_LOGIN_REQUEST_TAG = "VMR_LOGIN";
    public static final String VMR_FOLDER_NAVIGATION_TAG = "VMR_FOLDER_NAVIGATION";

    public final static class Url {
        public  final static String BASE                = "http://vmrdev.cloudapp.net:8080";
        public  final static String ALFRESCO_TICKET     = BASE + "/alfresco/service/api/login?u=admin&pw=alfresc0";
        public  final static String LOGIN               = BASE + "/vmr/mlogin.do";
        public  final static String FOLDER_NAVIGATION   = BASE + "/vmr/folderNavigation.do";
        public  final static String FILE_UPLOAD         = BASE + "/vmr/fileUpload.do";
        public  final static String RECENT              = BASE + "/vmr/recent.do";
        public  final static String SHARE_RECORDS       = BASE + "/vmr/shareRecords.do";
        public  final static String REPORT_DATA         = BASE + "/vmr/reportData.do";
        public  final static String ACCOUNT_SETUP       = BASE + "/vmr/accountSetup.do";
    }

    public final static class PageMode {
        public final static String LIST_ALL_FILE_FOLDER      = "LIST_ALL_FILE_FOLDER";
        public final static String DOCUMENT_DETAILS          = "DOCUMENT_DETAILS";
        public final static String SET_SAVED_FILE_PROPERTIES = "SET_SAVED_FILE_PROPERTIES";
        public final static String SAVE_FILE_PROPERTY        = "FILE_PROPERTY_SAVE";
        public final static String RENAME_FILE_OR_FOLDER     = "RENAME_FILE_FOLDER";
        public final static String DELETE_FILE_OR_FOLDER     = "DELETE_FILE_FOLDER";
        public final static String SEARCH_FILE               = "SEARCH_FILE";
        public final static String LIST_TRASH_BIN            = "LIST_TRASH_BIN";
        public final static String DOWNLOAD_ACTIVITY         = "DOWNLOAD_ACTIVITY";
        public final static String LIST_UN_INDEXED_FILE      = "LIST_UN_INDEXED_FILE";
        public final static String MOVE_OR_LINK_FILE         = "MOVE_OR_LINK_FILE";
        public final static String LIST_SHARED_WITH_ME       = "LIST_ALL_FILE_FOLDER_FOR_SHARED_WITH_ME";
        public final static String LIST_SHARED_BY_ME         = "GET_RECORDS_SHARED_BY_OWNER";
        public final static String CREATE_FOLDER             = "FOLDER_ADD";
        public final static String REMOVE_EXPIRED_RECORDS    = "REMOVE_EXPIRED_RECORDS";
    }

    public final static class Request {

        public final static class Login {

            // constants for Individual Form Body
            public static final String INDIVIDUAL_EMAIL_ID      = "emailID";
            public static final String INDIVIDUAL_PASSWORD      = "password";

            // constants for Professional Form Body
            public static final String PROFESSIONAL_EMAIL_ID    = "profEmailid";
            public static final String PROFESSIONAL_PASSWORD    = "profpswd";
            public static final String PROFESSIONAL_ID          = "profName";

            // constants for Family Form Body
            public static final String FAMILY_EMAIL_ID          = "familyEmailId";
            public static final String FAMILY_PASSWORD          = "familyPassword";
            public static final String FAMILY_ID                = "familyName";

            // constants for Corporate Form Body
            public static final String CORPORATE_EMAIL_ID       = "corpEmailID";
            public static final String CORPORATE_PASSWORD       = "corpPassword";
            public static final String CORPORATE_ID             = "corpName";

            // constant for Domain
            public static final String LOGIN_DOMAIN             ="domain";

            public final static class Custom {
                // constants for Individual Form Body
                public static final String INDIVIDUAL_EMAIL_ID      = "emailIDCustom";
                public static final String INDIVIDUAL_PASSWORD      = "passwordCustom";

                // constants for Professional Form Body
                public static final String PROFESSIONAL_EMAIL_ID    = "profEmailIdcustom";
                public static final String PROFESSIONAL_PASSWORD    = "profPwdcustom";
                public static final String PROFESSIONAL_ID          = "profNamecustom";

                // constants for Family Form Body
                public static final String FAMILY_EMAIL_ID          = "familyEmailIdCustom";
                public static final String FAMILY_PASSWORD          = "familyPasswordCustom";
                public static final String FAMILY_ID                = "familyNameCustom";

                // constants for Corporate Form Body
                public static final String CORPORATE_EMAIL_ID       = "corpEmailIDCustom";
                public static final String CORPORATE_PASSWORD       = "corpPasswordCustom";
                public static final String CORPORATE_ID             = "corpNameCustom";
            }

        }

        public final static class Domain {
            public static final String INDIVIDUAL   = "IND";
            public static final String FAMILY       = "FAM";
            public static final String PROFESSIONAL = "PROF";
            public static final String CORPORATE    = "CORP";
        }

        public final static class FormFields {
            public static final String ALFRESCO_NODE_REFERENCE = "alfNoderef";
            public static final String PAGE_MODE = "pageMode";
            public static final String ALFRESCO_TICKET = "alf_ticket";
            public static final String FOLDER_NAME = "folderName";
            public static final String TYPE = "type";
            public static final String PARENT_FOLDER = "parent";
            public static final String FOLDER_JSON_OBJECT = "folderObjJson";
            public static final String LOGGEDIN_USER_ID = "loggedInUserId";

        }

    }

    public final static class Key {
        public static final String USER_DETAILS = "USER_DETAILS";
    }

    public final static class Fragment {
        public static final String MY_RECORDS = "My Records";
    }


}
