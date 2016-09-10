package com.vmr.response_listener;

import com.android.volley.VolleyError;
import com.vmr.model.DeleteMessage;
import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrSharedItem;
import com.vmr.model.VmrTrashItem;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/23/16.
 */
public abstract class VmrResponseListener {

    public interface OnFetchRecordsListener {
        void onFetchRecordsSuccess(VmrFolder vmrFolder);
        void onFetchRecordsFailure(VolleyError error);
    }

    public interface OnFetchTrashListener {
        void onFetchTrashSuccess(List<VmrTrashItem> vmrTrashItems);
        void onFetchTrashFailure(VolleyError error);
    }

    public interface OnCreateFolderListener {
        void onCreateFolderSuccess(JSONObject jsonObject);
        void onCreateFolderFailure(VolleyError error);
    }

    public interface OnFetchSharedByMeListener{
        void onFetchSharedByMeSuccess(List<VmrSharedItem> vmrSharedItems);
        void onFetchSharedByMeFailure(VolleyError error);
    }

    public interface OnRenameItemListener {
        void onRenameItemSuccess(JSONObject jsonObject);
        void onRenameItemFailure(VolleyError error);
    }

    public interface OnMoveToTrashListener {
        void onMoveToTrashSuccess(List<DeleteMessage> jsonObject);
        void onMoveToTrashFailure(VolleyError error);
    }

    public interface OnLoginListener {
        void onLoginSuccess(UserInfo userInfo);
        void onLoginFailure(VolleyError error);
    }

    public interface OnFetchTicketListener{
        void onFetchTicketSuccess(String ticket);
        void onFetchTicketFailure(VolleyError error);
    }

    public interface OnFetchClassifications{
        void onFetchClassificationsSuccess(Map<String , String > classifications);
        void onFetchClassificationsFailure(VolleyError error);
    }

    public interface OnFetchProperties{
        void onFetchPropertiesSuccess(Map<String , JSONObject> properties);
        void onFetchPropertiesFailure(VolleyError error);
    }
}
