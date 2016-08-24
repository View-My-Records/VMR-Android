package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.folder_structure.VmrFolder;

/**
 * Created by abhijit on 8/23/16.
 */
public class VmrRequest {

    public interface onFetchRecordsListener {
        void onFetchRecordsSuccess(VmrFolder vmrFolder);
        void onFetchRecordsFailure(VolleyError error);
    }


}
