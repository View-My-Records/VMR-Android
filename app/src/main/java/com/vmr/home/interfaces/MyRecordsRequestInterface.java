package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.MyRecords;

import org.json.JSONObject;

/**
 * Created by abhijit on 8/17/16.
 */

public interface MyRecordsRequestInterface {
    void fetchFilesAndFoldersSuccess(MyRecords myRecords);
    void fetchFilesAndFoldersFailure(VolleyError error);
}
