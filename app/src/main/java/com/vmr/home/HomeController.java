package com.vmr.home;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.home.request.MyRecordsRequest;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import java.util.Map;

/*
 * Created by abhijit on 8/17/16.
 */

public class HomeController {

    private VmrRequest.onFetchRecordsListener onFetchRecordsListener;

    public HomeController(VmrRequest.onFetchRecordsListener onFetchRecordsListener) {
        this.onFetchRecordsListener = onFetchRecordsListener;
    }

    public void fetchAllFilesAndFolders(Map<String, String> formData){
        MyRecordsRequest recordsRequest =
                new MyRecordsRequest(
                        formData,
                        new Response.Listener<VmrFolder>() {
                            @Override
                            public void onResponse(VmrFolder vmrFolder) {
                                onFetchRecordsListener.onFetchRecordsSuccess(vmrFolder);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchRecordsListener.onFetchRecordsFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(recordsRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }
}
