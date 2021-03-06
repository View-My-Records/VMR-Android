package com.vmr.network.controller.request;

/*
 * Created by abhijit on 9/23/16.
 */

import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

public class VmrURL {

    public static String getLoginUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.LOGIN;
    }

    public static String getFolderNavigationUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.FOLDER_NAVIGATION;
    }

    public static String getShareRecordsUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.SHARE_RECORDS;
    }

    public static String getFileUploadUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.FILE_UPLOAD;
    }

    public static String getInboxUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.INBOX;
    }

    public static String getAccountSetupUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.ACCOUNT_SETUP;
    }

    public static String getImageUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.IMAGE;
    }

    public static String getForgotPasswordUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.FORGOT_PASSWORD;
    }

    public static String getForgotUsernameUrl(){
        return PrefUtils.getSharedPreference(PrefConstants.BASE_URL) + Constants.Url.FORGOT_USERNAME;
    }
}
