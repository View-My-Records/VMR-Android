package com.vmr.home;

import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.model.VmrItem;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.home.request.CreateFolderRequest;
import com.vmr.home.request.MoveToTrashRequest;
import com.vmr.home.request.RecordsRequest;
import com.vmr.home.request.RemoveExpiredRecordsRequest;
import com.vmr.home.request.RenameItemRequest;
import com.vmr.home.request.SharedByMeRequest;
import com.vmr.home.request.TrashRequest;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrSharedItem;
import com.vmr.model.VmrTrashItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 8/17/16.
 */

public class HomeController {

    private VmrResponseListener.OnFetchRecordsListener onFetchRecordsListener;
    private VmrResponseListener.OnFetchTrashListener onFetchTrashListener;
    private VmrResponseListener.OnFetchSharedByMeListener onFetchSharedByMe;
    private VmrResponseListener.OnCreateFolderListener onCreateFolderListener;
    private VmrResponseListener.OnRenameItemListener onRenameItemListener;
    private VmrResponseListener.OnMoveToTrashListener onMoveToTrashListener;

    public HomeController(VmrResponseListener.OnFetchSharedByMeListener onFetchSharedByMe) {
        this.onFetchSharedByMe = onFetchSharedByMe;
    }

    public HomeController(VmrResponseListener.OnFetchRecordsListener OnFetchRecordsListener) {
        this.onFetchRecordsListener = OnFetchRecordsListener;
    }

    public HomeController(VmrResponseListener.OnFetchTrashListener OnFetchTrashListener) {
        this.onFetchTrashListener = OnFetchTrashListener;
    }

    public HomeController(VmrResponseListener.OnCreateFolderListener onCreateFolderListener) {
        this.onCreateFolderListener = onCreateFolderListener;
    }

    public HomeController(VmrResponseListener.OnRenameItemListener onRenameItemListener) {
        this.onRenameItemListener = onRenameItemListener;
    }

    public HomeController(VmrResponseListener.OnMoveToTrashListener onMoveToTrashListener) {
        this.onMoveToTrashListener = onMoveToTrashListener;
    }

    public void fetchAllFilesAndFolders(String nodeRef){

        Map<String, String> formData = VMR.getUserMap();
        formData.put(Constants.Request.FolderNavigation.ListAllFileFolder.ALFRESCO_NODE_REFERENCE, nodeRef);
        formData.put(Constants.Request.FolderNavigation.ListAllFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_ALL_FILE_FOLDER);
        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, PrefUtils.getSharedPreference(VMR.getVMRContext(), PrefConstants.VMR_ALFRESCO_TICKET));

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
        VmrRequestQueue.getInstance().addToRequestQueue(recordsRequest, Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
    }

    public void fetchUnIndexed(String nodeRef){

        Map<String, String> formData = VMR.getUserMap();
        formData.put(Constants.Request.FolderNavigation.ListUnIndexed.ALFRESCO_NODE_REFERENCE, nodeRef);
        formData.put(Constants.Request.FolderNavigation.ListUnIndexed.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_ALL_FILE_FOLDER);
        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, PrefUtils.getSharedPreference(VMR.getVMRContext(), PrefConstants.VMR_ALFRESCO_TICKET));

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
        VmrRequestQueue.getInstance().addToRequestQueue(recordsRequest, Constants.Request.FolderNavigation.ListUnIndexed.TAG);
    }


    public void fetchTrash(String nodeRef){

        Map<String, String> formData = VMR.getUserMap();
        formData.put(Constants.Request.FolderNavigation.ListTrashBin.ALFRESCO_NODE_REFERENCE,nodeRef);
        formData.put(Constants.Request.FolderNavigation.ListTrashBin.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_TRASH_BIN);
        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, PrefUtils.getSharedPreference(VMR.getVMRContext(), PrefConstants.VMR_ALFRESCO_TICKET));

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
        VmrRequestQueue.getInstance().addToRequestQueue(trashRequest, Constants.Request.FolderNavigation.ListTrashBin.TAG);
    }

    public void createFolder(String folderName, String parentNodeRef){

        Map<String, String> formData = VMR.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.CreateFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.CREATE_FOLDER);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.Request.FolderNavigation.CreateFolder.FOLDER_NAME, Uri.encode(folderName, "UTF-8"));
            jsonObject.put(Constants.Request.FolderNavigation.CreateFolder.FOLDER_TYPE, 1);
            jsonObject.put(Constants.Request.FolderNavigation.CreateFolder.FOLDER_PARENT, parentNodeRef);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        VmrDebug.printLogI(this.getClass(), jsonObject.toString().replaceAll("\\\\", ""));
        formData.put(Constants.Request.FolderNavigation.CreateFolder.FOLDER_JSON_OBJECT, jsonObject.toString().replaceAll("\\\\", ""));

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
        VmrRequestQueue.getInstance().addToRequestQueue(createFolderRequest, Constants.Request.FolderNavigation.CreateFolder.TAG);
    }

    public void renameItem(VmrItem vmrItem, String newName){

        Map<String, String> formData = VMR.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.RENAME_FILE_OR_FOLDER);
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.NEW_NAME,Uri.encode(newName, "UTF-8"));
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.NODE_REF,vmrItem.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.OLD_NAME, vmrItem.getName());

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
        VmrRequestQueue.getInstance().addToRequestQueue(renameItemRequest, Constants.Request.FolderNavigation.RenameFileFolder.TAG);
    }

    public void moveToTrash(VmrItem vmrItem){

        Map<String, String> formData = VMR.getUserMap();
//        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DeleteFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DELETE_FILE_OR_FOLDER);

        JSONObject deleteObjectValues = new JSONObject();
        JSONArray deleteObjects = new JSONArray();

        JSONObject trashObjectValues = new JSONObject();
        JSONArray trashObjects = new JSONArray();

        JSONObject objectToDelete = new JSONObject();

        try {
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_NAME, vmrItem.getName());
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_NODE_REF, vmrItem.getNodeRef());
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_TYPE, true);
            deleteObjects.put(objectToDelete);
            deleteObjectValues.put(Constants.Request.FolderNavigation.DeleteFileFolder.DELETE_OBJECTS, deleteObjects);
            trashObjectValues.put(Constants.Request.FolderNavigation.DeleteFileFolder.TRASH_OBJECTS, trashObjects);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        formData.put(Constants.Request.FolderNavigation.DeleteFileFolder.DELETE_OBJECT_VALUES, deleteObjectValues.toString().replaceAll("\\\\", ""));
        formData.put(Constants.Request.FolderNavigation.DeleteFileFolder.TRASH_OBJECT_VALUES, trashObjectValues.toString().replaceAll("\\\\", ""));
        VmrDebug.printLogI(this.getClass(), formData.toString().replaceAll("\\\\", ""));

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
        VmrRequestQueue.getInstance().addToRequestQueue(moveToTrashRequest, Constants.Request.FolderNavigation.DeleteFileFolder.TAG);
    }

    public void fetchSharedByMe(){

        Map<String, String> formData = VMR.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.ListSharedByMe.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_SHARED_BY_ME);
        formData.put(Constants.Request.FolderNavigation.ListSharedByMe.LOGGED_IN_USER_ID, VMR.getLoggedInUserInfo().getLoggedinUserId());
        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, PrefUtils.getSharedPreference(VMR.getVMRContext(), PrefConstants.VMR_ALFRESCO_TICKET));

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
        VmrRequestQueue.getInstance().addToRequestQueue(sharedByMeRequest, Constants.Request.FolderNavigation.ListSharedByMe.TAG);
    }

    public void removeExpiredRecords(){

        Map<String, String> formData = VMR.getUserMap();
        formData.put(Constants.Request.FolderNavigation.RemoveExpiredRecords.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.REMOVE_EXPIRED_RECORDS);

        RemoveExpiredRecordsRequest request =
                new RemoveExpiredRecordsRequest(
                        formData,
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
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.FolderNavigation.RemoveExpiredRecords.TAG);
    }

}
