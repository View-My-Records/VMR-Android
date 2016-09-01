package com.vmr.home;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.home.interfaces.VmrResponse;
import com.vmr.home.request.CreateFolderRequest;
import com.vmr.home.request.MoveToTrashRequest;
import com.vmr.home.request.RecordsRequest;
import com.vmr.home.request.RemoveExpiredRecordsRequest;
import com.vmr.home.request.RenameItemRequest;
import com.vmr.home.request.SharedByMeRequest;
import com.vmr.home.request.TrashRequest;
import com.vmr.model.DeleteMessage;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrSharedItem;
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

    private VmrResponse.OnFetchRecordsListener onFetchRecordsListener;
    private VmrResponse.OnFetchTrashListener onFetchTrashListener;
    private VmrResponse.OnFetchSharedByMeListener onFetchSharedByMe;
    private VmrResponse.OnCreateFolderListener onCreateFolderListener;
    private VmrResponse.OnRenameItemListener onRenameItemListener;
    private VmrResponse.OnMoveToTrashListener onMoveToTrashListener;

    public HomeController(VmrResponse.OnFetchSharedByMeListener onFetchSharedByMe) {
        this.onFetchSharedByMe = onFetchSharedByMe;
    }

    public HomeController(VmrResponse.OnFetchRecordsListener OnFetchRecordsListener) {
        this.onFetchRecordsListener = OnFetchRecordsListener;
    }

    public HomeController(VmrResponse.OnFetchTrashListener OnFetchTrashListener) {
        this.onFetchTrashListener = OnFetchTrashListener;
    }

    public HomeController(VmrResponse.OnCreateFolderListener onCreateFolderListener) {
        this.onCreateFolderListener = onCreateFolderListener;
    }

    public HomeController(VmrResponse.OnRenameItemListener onRenameItemListener) {
        this.onRenameItemListener = onRenameItemListener;
    }

    public HomeController(VmrResponse.OnMoveToTrashListener onMoveToTrashListener) {
        this.onMoveToTrashListener = onMoveToTrashListener;
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

    public void renameItem(Map<String, String> formData){
        RenameItemRequest renameItemRequest =
                new RenameItemRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                onRenameItemListener.onRenameItemSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onRenameItemListener.onRenameItemFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(renameItemRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }

    public void moveToTrash(Map<String, String> formData){
        MoveToTrashRequest moveToTrashRequest =
                new MoveToTrashRequest(
                        formData,
                        new Response.Listener<List<DeleteMessage>>() {
                            @Override
                            public void onResponse(List<DeleteMessage> response) {
                                onMoveToTrashListener.onMoveToTrashSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onMoveToTrashListener.onMoveToTrashFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(moveToTrashRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }

    public void fetchSharedByMe(Map<String, String> formData){
        SharedByMeRequest sharedByMeRequest =
                new SharedByMeRequest(
                        formData,
                        new Response.Listener<List<VmrSharedItem>>() {
                            @Override
                            public void onResponse(List<VmrSharedItem> vmrSharedItems) {
                                onFetchSharedByMe.onFetchSharedByMeSuccess(vmrSharedItems);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchSharedByMe.onFetchSharedByMeFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(sharedByMeRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }

    public void removeExpiredRecords(){
        RemoveExpiredRecordsRequest request =
                new RemoveExpiredRecordsRequest(
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String vmrSharedItems) {

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(request, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }

}
