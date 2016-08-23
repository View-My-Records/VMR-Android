package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.MyRecords;
import com.vmr.model.folder_structure.VmrFolder;

import org.json.JSONObject;

/**
 * Created by abhijit on 8/17/16.
 */

public interface MyRecordsRequestInterface {
    void fetchFilesAndFoldersSuccess(VmrFolder vmrFolder);
    void fetchFilesAndFoldersFailure(VolleyError error);
}
