package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrTrashItem;

import java.util.List;

/*
 * Created by abhijit on 8/23/16.
 */
public class VmrRequest {

    public interface onFetchRecordsListener {
        void onFetchRecordsSuccess(VmrFolder vmrFolder);
        void onFetchRecordsFailure(VolleyError error);
    }

    public interface onFetchTrashListener {
        void onFetchTrashSuccess(List<VmrTrashItem> vmrTrashItems);
        void onFetchTrashFailure(VolleyError error);
    }

    public interface onFetchSharedFilesTrashListener {
        void onFetchSharedFilesSuccess(List<VmrTrashItem> vmrTrashItems);
        void onFetchSharedFilesFailure(VolleyError error);
    }
}
