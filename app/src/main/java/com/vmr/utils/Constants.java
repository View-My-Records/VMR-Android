package com.vmr.utils;

/*
 * Created by abhijit on 8/16/16.
 */

public abstract class Constants {

    public static final String VMR_LOGIN_REQUEST_TAG = "VMR_LOGIN";

    public static class Url {
        public final static String DEFAULT_BASE_URL = "http://vmrdev.cloudapp.net:8080";

        public final static String ALFRESCO_TICKET     =  DEFAULT_BASE_URL + "/alfresco/service/api/login?u=admin&pw=alfresc0";
        public final static String LOGIN               = "/vmr/mlogin.do";
        public final static String FOLDER_NAVIGATION   = "/vmr/folderNavigation.do";
        public final static String FILE_UPLOAD         = "/vmr/fileUpload.do";
        public final static String RECENT              = "/vmr/recent.do";
        public final static String SHARE_RECORDS       = "/vmr/shareRecords.do";
        public final static String REPORT_DATA         = "/vmr/reportData.do";
        public final static String ACCOUNT_SETUP       = "/vmr/accountSetup.do";
        public final static String NOTIFICATION        = "/vmr/indoxData.do";
    }

    public final static class Request {

        public final static class Login {

            // constant for Domain
            public static final String DOMAIN       ="domain";

            // constants for Individual Form Body
            public final static class Individual{
                public static final String EMAIL_ID = "emailID";
                public static final String PASSWORD = "password";
            }

            // constants for Professional Form Body
            public final static class Professional{
                public static final String EMAIL_ID = "profEmailid";
                public static final String PASSWORD = "profpswd";
                public static final String NAME     = "profName";
            }

            // constants for Family Form Body
            public final static class Family{
                public static final String EMAIL_ID = "familyEmailId";
                public static final String PASSWORD = "familyPassword";
                public static final String NAME     = "familyName";
            }

            // constants for Corporate Form Body
            public final static class Corporate{
                public static final String EMAIL_ID = "corpEmailID";
                public static final String PASSWORD = "corpPassword";
                public static final String NAME     = "corpName";
            }

            public final static class Custom {

                // constants for Individual Form Body
                public final static class Individual{
                    public static final String EMAIL_ID = "emailIDCustom";
                    public static final String PASSWORD = "passwordCustom";
                }

                // constants for Professional Form Body
                public final static class Professional{
                    public static final String EMAIL_ID = "profEmailIdcustom";
                    public static final String PASSWORD = "profPwdcustom";
                    public static final String NAME     = "profNamecustom";
                }

                // constants for Family Form Body
                public final static class Family{
                    public static final String EMAIL_ID = "familyEmailIdCustom";
                    public static final String PASSWORD = "familyPasswordCustom";
                    public static final String NAME     = "familyNameCustom";
                }

                // constants for Corporate Form Body
                public final static class Corporate{
                    public static final String EMAIL_ID = "corpEmailIDCustom";
                    public static final String PASSWORD = "corpPasswordCustom";
                    public static final String NAME     = "corpNameCustom";
                }
            }

            public final static class Domain {
                public static final String INDIVIDUAL   = "IND";
                public static final String FAMILY       = "FAM";
                public static final String PROFESSIONAL = "PROF";
                public static final String CORPORATE    = "CORP";
            }
        }

        public final static class FolderNavigation {

            public final static class PageMode {
                public final static String LIST_ALL_FILE_FOLDER      = "LIST_ALL_FILE_FOLDER";
                public final static String DOCUMENT_CONTENT_TYPES    = "DOCUMENT_CONTENT_TYPES";
                public final static String DOCUMENT_DETAILS          = "DOCUMENT_DETAILS";
                public final static String SET_SAVED_FILE_PROPERTIES = "SET_SAVED_FILE_PROPERTIES";
                public final static String SAVE_FILE_PROPERTY        = "FILE_PROPERTY_SAVE";
                public final static String RENAME_FILE_OR_FOLDER     = "RENAME_FILE_FOLDER";
                public final static String DELETE_FILE_OR_FOLDER     = "DELETE_FILE_FOLDER";
                public final static String SEARCH_FILE               = "SEARCH_FILE";
                public final static String LIST_TRASH_BIN            = "LIST_TRASH_BIN";
                public final static String DOWNLOAD_ACTIVITY         = "DOWNLOAD_ACTIVITY";
                public final static String DOWNLOAD_FILE_STREAM      = "DOWNLOAD_FILE_STREAM";
                public final static String LIST_UN_INDEXED_FILE      = "LIST_UN_INDEXED_FILE";
                public final static String MOVE_COPY_LINK_FILE       = "MOVE_OR_LINK_FILE";
                public final static String LIST_SHARED_WITH_ME       = "LIST_ALL_FILE_FOLDER_FOR_SHARED_WITH_ME";
                public final static String LIST_SHARED_BY_ME         = "GET_RECORDS_SHARED_BY_OWNER";
                public final static String CREATE_FOLDER             = "FOLDER_ADD";
                public final static String REMOVE_EXPIRED_RECORDS    = "REMOVE_EXPIRED_RECORDS";
                public final static String INBOX_MESSAGE_DISPLAY     = "INBOX_MSG_DISPLY";
            }

            public final static class ListAllFileFolder {
                public static final String PAGE_MODE = "pageMode";
                public static final String ALFRESCO_NODE_REFERENCE = "alfNoderef";
                public static final String TAG = "VMR_LIST_ALL_FILE_FOLDER";
            }

            public final static class ListUnIndexed {
                public static final String PAGE_MODE = "pageMode";
                public static final String ALFRESCO_NODE_REFERENCE = "alfNoderef";
                public static final String TAG = "VMR_LIST_UN_INDEXED";
            }

            public final static class ListTrashBin {
                public static final String PAGE_MODE = "pageMode";
                public static final String ALFRESCO_NODE_REFERENCE = "alfNoderef";
                public static final String TAG = "VMR_LIST_TRASH_BIN";
            }

            public final static class ListSharedByMe {
                public static final String PAGE_MODE = "pageMode";
                public static final String LOGGED_IN_USER_ID = "loggedInUserId";
                public static final String TAG = "VMR_SHARED_BY_ME";
            }

            public final static class ListSharedWithMe {
                public static final String PAGE_MODE = "pageMode";
                public static final String TAG = "VMR_SHARED_WITH_ME";
            }

            public final static class RemoveExpiredRecords {
                public static final String PAGE_MODE = "pageMode";
                public static final String TAG = "VMR_REMOVE_EXPIRED_RECORDS";
            }

            public final static class Classification {
                public static final String PAGE_MODE = "pageMode";
                public static final String TAG = "VMR_GET_CLASSIFICATIONS";
            }

            public final static class Properties {
                public static final String PAGE_MODE = "pageMode";
                public static final String DOC_TYPE = "docSelected";
                public static final String FILE_NODE_REF = "fileSelectedNodeRef";
                public static final String PROGRAM_NAME = "programName";
                public static final String TAG = "VMR_GET_PROPERTIES";
            }

            public final static class SaveIndex {
                public static final String PAGE_MODE = "pageMode";
                public static final String FILE_PROPERTY_JSON_STRING = "filePropertyJsonString";
                public static final String FILE_SELECTED_NODE_REF = "fileSelectedNodeRef";
                public static final String FILE_NAME = "fileSelectedName";
                public static final String FILE_INDEX_STATUS = "fileIndexstatus";
                public static final String DOCUMENT_CATEGORY_VALUE = "docCategoryVal";
                public static final String DOCUMENT_TYPE = "docType";
                public static final String PROGRAM_NAME = "programName";
                public static final String TAG = "VMR_GET_PROPERTIES";
            }

            public final static class DownloadFile {
                public static final String PAGE_MODE = "pageMode";
                public static final String NODE_REF = "fileSelectedNodeRef";
                public static final String FILE_NAME = "fileName";
                public static final String MIME_TYPE = "mimeType";
                public static final String TAG = "VMR_DOWNLOAD_FILE";
            }

            public final static class UploadFile {
                public static final String FILE = "upload";
                public static final String FILE_NAMES = "uploadFileName";
                public static final String CONTENT_TYPE = "uploadContentType";
                public static final String PARENT_NODE_REF = "selectedFolderNodeRef";
                public static final String TAG = "VMR_UPLOAD_FILE";
            }

            public final static class DocumentDetails {
                public static final String PAGE_MODE = "pageMode";
                public static final String NODE_REF = "fileSelectedNodeRef";
                public static final String DOC_SELECTED = "docSelected";
                public static final String TAG = "VMR_DOCUMENT_DETAILS";
            }

            public final static class CreateFolder {
                public static final String PAGE_MODE = "pageMode";
                public static final String FOLDER_JSON_OBJECT = "folderObjJson";
                public static final String FOLDER_NAME = "folderName";
                public static final String FOLDER_TYPE = "type";
                public static final String FOLDER_PARENT = "parent";
                public static final String TAG = "VMR_CREATE_FOLDER";
            }

            public final static class RenameFileFolder {
                public static final String PAGE_MODE = "pageMode";
                public static final String NODE_REF = "fileSelectedNodeRef";
                public static final String NEW_NAME = "newName";
                public static final String OLD_NAME = "renameOldname";
                public static final String TAG = "VMR_RENAME_FILE_OR_FOLDER";
            }

            public final static class MoveCopyLink {
                public static final String PAGE_MODE = "pageMode";
                public static final String NODE_REF = "fileNodRef";
                public static final String DEST_FOLDER_NODE_REF = "folderNodRef";
                public static final String DEST_FOLDER_NAME = "folderName";
                public static final String OPERATION = "fileLinkMoveCopyOperation";
                public static final String TAG = "VMR_MOVE_OR_LINK_FILE";
            }

            public final static class DeleteFileFolder {
                public static final String PAGE_MODE            = "pageMode";
                public static final String DELETE_OBJECTS       = "Deleteobjects";
                public static final String TRASH_OBJECTS        = "Trashobjects";
                public static final String DELETE_OBJECT_VALUES = "deleteobjectvalues";
                public static final String TRASH_OBJECT_VALUES  = "trashobjectvalues";
                public static final String OBJECT_NAME          = "objectName";
                public static final String OBJECT_NODE_REF      = "objectNoderef";
                public static final String OBJECT_TYPE          = "objectType";
                public static final String TAG                  = "VMR_DELETE_FILE_OR_FOLDER";
            }

            public final static class Message {
                public static final String PAGE_MODE    = "pageMode";
                public static final String INBOX_ID     = "InboxrowId";
                public static final String TAG          = "VMR_MESSAGE";
            }

        }

        public final static class Notification {
                public static final String DATA_TYPE = "dataType";
                public static final String TAG       = "VMR_NOTIFICATION";
        }

        public final static class Alfresco {
            public static final String ALFRESCO_NODE_REFERENCE = "alfNoderef";
            public static final String ALFRESCO_TICKET = "alf_ticket";
        }

    }

    public final static class Response {
        public final static class Login {
            public static final String SERIAL_LNO       = "slNo";
            public static final String RESULT           = "result";
            public static final String ROOT_NODE_REF    = "rootNodref";
            public static final String URL_TYPE         = "urlType";
            public static final String USER_TYPE        = "userType";
            public static final String MEMBERSHIP_TYPE  = "membershipType";
            public static final String EMAIL_ID         = "emailId";
            public static final String EMPLOYEE_TYPE    = "empType";
            public static final String USER_ID          = "userId";
            public static final String SESSION_ID       = "httpSessionId";
            public static final String USER_NAME        = "userName";
            public static final String CURRENT_USER_ID  = "loggedinUserId";
            public static final String LAST_LOGIN_TIME  = "lastLoginTime";
            public static final String LAST_NAME        = "lastName";
            public static final String FIRST_NAME       = "firstName";
            public static final String CORP_NAME        = "corpName";
            public static final String CORP_ID          = "corpId";
        }
    }

    public final static class Indexing {
        public static final String[] CLASSIFICATION = {

        };
    }

    public final static class Key {
        public static final String USER_DETAILS = "USER_DETAILS";
        public static final String RECORD = "RECORD";
    }

    public final static class Fragment {
        public static final String MY_RECORDS = "My Records";
        public static final String RECENTLY_ACCESSED = "Recently Accessed";
        public static final String TO_BE_INDEXED = "To Be Indexed";
        public static final String OFFLINE = "Offline";
        public static final String REPORTS = "Reports";
        public static final String TRASH = "Trash";
        public static final String SHARED_WITH_ME = "Shared With Me";
        public static final String SHARED_BY_ME = "Shared By Me";
        public static final String ABOUT = "About";
        public static final String HELP = "Help";
    }

    public final static class MembershipType{
        public static final String INDIVIDUAL = "IND";
        public static final String FAMILY = "FAM";
        public static final String PROFESSIONAL = "PROF";
        public static final String CORPORATE = "CORP";
    }
}
