package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrTrashItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/*
 * Created by abhijit on 8/23/16.
 */
public class VmrRequest {

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

    public interface OnFetchSharedByMe{

    }
}
