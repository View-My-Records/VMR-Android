package com.vmr.utils;

import com.vmr.app.Vmr;

/*
 * Created by abhijit on 9/23/16.
 */

public class VmrURL {

    public static String getLoginUrl(){
        return PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL) + Constants.Url.LOGIN;
    }

    public static String getFolderNavigationUrl(){
        return PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL) + Constants.Url.FOLDER_NAVIGATION;
    }

    public static String getShareRecordsUrl(){
        return PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL) + Constants.Url.SHARE_RECORDS;
    }

    public static String getFileUploadUrl(){
        return PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL) + Constants.Url.FILE_UPLOAD;
    }

    public static String getInboxUrl(){
        return PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL) + Constants.Url.INBOX;
    }

    public static String getAccountSetupUrl(){
        return PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL) + Constants.Url.ACCOUNT_SETUP;
    }
}
