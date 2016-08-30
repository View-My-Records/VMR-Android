package com.vmr.home;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.home.request.CreateFolderRequest;
import com.vmr.home.request.RecordsRequest;
import com.vmr.home.request.TrashRequest;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrTrashItem;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/17/16.
 */

public class HomeController {

    private VmrRequest.OnFetchRecordsListener onFetchRecordsListener;
    private VmrRequest.OnFetchTrashListener onFetchTrashListener;
    private VmrRequest.OnCreateFolderListener onCreateFolderListener;

    public HomeController(VmrRequest.OnFetchRecordsListener OnFetchRecordsListener) {
        this.onFetchRecordsListener = OnFetchRecordsListener;
    }

    public HomeController(VmrRequest.OnFetchTrashListener OnFetchTrashListener) {
        this.onFetchTrashListener = OnFetchTrashListener;
    }

    public HomeController(VmrRequest.OnCreateFolderListener onCreateFolderListener) {
        this.onCreateFolderListener = onCreateFolderListener;
    }

    public void fetchAllFilesAndFolders(Map<String, String> formData){
        RecordsRequest recordsRequest =
                new RecordsRequest(
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


    public void fetchTrash(Map<String, String> formData){
        TrashRequest trashRequest =
                new TrashRequest(
                        formData,
                        new Response.Listener<List<VmrTrashItem>>() {
                            @Override
                            public void onResponse(List<VmrTrashItem> vmrTrashItems) {
                                onFetchTrashListener.onFetchTrashSuccess(vmrTrashItems);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchTrashListener.onFetchTrashFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(trashRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }

    public void createFolder(Map<String, String> formData){
        CreateFolderRequest createFolderRequest =
                new CreateFolderRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                onCreateFolderListener.onCreateFolderSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onCreateFolderListener.onCreateFolderFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(createFolderRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }


}
