package com.vmr.home.controller;

import android.net.Uri;
import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.request.ClassificationRequest;
import com.vmr.home.request.CreateFolderRequest;
import com.vmr.home.request.DownloadRequest;
import com.vmr.home.request.MoveCopyLinkRequest;
import com.vmr.home.request.MoveToTrashRequest;
import com.vmr.home.request.PropertiesRequest;
import com.vmr.home.request.RecordsRequest;
import com.vmr.home.request.RemoveExpiredRecordsRequest;
import com.vmr.home.request.RenameItemRequest;
import com.vmr.home.request.SaveIndexRequest;
import com.vmr.home.request.SharedByMeRequest;
import com.vmr.home.request.TrashRequest;
import com.vmr.home.request.UploadRequest;
import com.vmr.model.DeleteMessage;
import com.vmr.model.SearchResult;
import com.vmr.model.UploadPacket;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrSharedItem;
import com.vmr.model.VmrTrashItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private VmrResponseListener.OnMoveItemListener onMoveItemListener;
    private VmrResponseListener.OnLinkItemListener onLinkItemListener;
    private VmrResponseListener.OnCopyItemListener onCopyItemListener;
    private VmrResponseListener.OnMoveToTrashListener onMoveToTrashListener;
    private VmrResponseListener.OnDeleteFromTrashListener onDeleteFromTrashListener;
    private VmrResponseListener.OnFetchClassifications onFetchClassifications;
    private VmrResponseListener.OnFetchProperties onFetchProperties;
    private VmrResponseListener.OnFileDownload onFileDownload;
    private VmrResponseListener.OnFileUpload onFileUpload;
    private VmrResponseListener.OnSaveIndex onSaveIndex;

    public HomeController(VmrResponseListener.OnFetchSharedByMeListener onFetchSharedByMe) {
        this.onFetchSharedByMe = onFetchSharedByMe;
    }

    public HomeController(VmrResponseListener.OnFetchRecordsListener onFetchRecordsListener) {
        this.onFetchRecordsListener = onFetchRecordsListener;
    }

    public HomeController(VmrResponseListener.OnFetchTrashListener onFetchTrashListener) {
        this.onFetchTrashListener = onFetchTrashListener;
    }

    public HomeController(VmrResponseListener.OnCreateFolderListener onCreateFolderListener) {
        this.onCreateFolderListener = onCreateFolderListener;
    }

    public HomeController(VmrResponseListener.OnRenameItemListener onRenameItemListener) {
        this.onRenameItemListener = onRenameItemListener;
    }

    public HomeController(VmrResponseListener.OnMoveItemListener onMoveItemListener) {
        this.onMoveItemListener = onMoveItemListener;
    }

    public HomeController(VmrResponseListener.OnLinkItemListener onLinkItemListener) {
        this.onLinkItemListener = onLinkItemListener;
    }

    public HomeController(VmrResponseListener.OnCopyItemListener onCopyItemListener) {
        this.onCopyItemListener = onCopyItemListener;
    }

    public HomeController(VmrResponseListener.OnMoveToTrashListener onMoveToTrashListener) {
        this.onMoveToTrashListener = onMoveToTrashListener;
    }

    public HomeController(VmrResponseListener.OnDeleteFromTrashListener onDeleteFromTrashListener) {
        this.onDeleteFromTrashListener = onDeleteFromTrashListener;
    }

    public HomeController(VmrResponseListener.OnFetchClassifications onFetchClassifications) {
        this.onFetchClassifications = onFetchClassifications;
    }

    public HomeController(VmrResponseListener.OnFetchProperties onFetchProperties) {
        this.onFetchProperties = onFetchProperties;
    }

    public HomeController(VmrResponseListener.OnFileDownload onFileDownload) {
        this.onFileDownload = onFileDownload;
    }

    public HomeController(VmrResponseListener.OnFileUpload onFileUpload) {
        this.onFileUpload = onFileUpload;
    }

    public HomeController(VmrResponseListener.OnSaveIndex onSaveIndex) {
        this.onSaveIndex = onSaveIndex;
    }

    public static void removeUnwantedParams(){
        List<String> list = new ArrayList<>();
        list.add(Constants.Request.Login.DOMAIN);
        list.add(Constants.Request.Login.Individual.EMAIL_ID);
        list.add(Constants.Request.Login.Individual.PASSWORD);
        list.add(Constants.Request.Login.Family.EMAIL_ID);
        list.add(Constants.Request.Login.Family.PASSWORD);
        list.add(Constants.Request.Login.Family.NAME);
        list.add(Constants.Request.Login.Professional.EMAIL_ID);
        list.add(Constants.Request.Login.Professional.PASSWORD);
        list.add(Constants.Request.Login.Professional.NAME);
        list.add(Constants.Request.Login.Corporate.EMAIL_ID);
        list.add(Constants.Request.Login.Corporate.PASSWORD);
        list.add(Constants.Request.Login.Corporate.NAME);

        list.add(Constants.Request.Login.Custom.Individual.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Individual.PASSWORD);
        list.add(Constants.Request.Login.Custom.Family.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Family.PASSWORD);
        list.add(Constants.Request.Login.Custom.Family.NAME);
        list.add(Constants.Request.Login.Custom.Professional.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Professional.PASSWORD);
        list.add(Constants.Request.Login.Custom.Professional.NAME);
        list.add(Constants.Request.Login.Custom.Corporate.EMAIL_ID);
        list.add(Constants.Request.Login.Custom.Corporate.PASSWORD);
        list.add(Constants.Request.Login.Custom.Corporate.NAME);

        Vmr.getUserMap().keySet().retainAll(list);
    }

    public void fetchAllFilesAndFolders(String nodeRef){
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.FolderNavigation.ListAllFileFolder.ALFRESCO_NODE_REFERENCE, nodeRef);
        formData.put(Constants.Request.FolderNavigation.ListAllFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_ALL_FILE_FOLDER);
//        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, Vmr.getAlfrescoTicket());

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
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.FolderNavigation.ListUnIndexed.ALFRESCO_NODE_REFERENCE, nodeRef);
        formData.put(Constants.Request.FolderNavigation.ListUnIndexed.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_ALL_FILE_FOLDER);
//        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, Vmr.getAlfrescoTicket());

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

    public void fetchTrash(){
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
//        formData.put(Constants.Request.FolderNavigation.ListTrashBin.ALFRESCO_NODE_REFERENCE,nodeRef);
        formData.put(Constants.Request.FolderNavigation.ListTrashBin.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_TRASH_BIN);
//        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, Vmr.getAlfrescoTicket());

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
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
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

    public void renameItem(Record record, String newName){
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.RENAME_FILE_OR_FOLDER);
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.OLD_NAME, record.getRecordName());
        formData.put(Constants.Request.FolderNavigation.RenameFileFolder.NEW_NAME, Uri.encode(newName, "UTF-8"));

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

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                VmrDebug.printLogI(this.getClass(), renameItemRequest.getHeaders().toString());
//                VmrDebug.printLogI(this.getClass(), new String(renameItemRequest.getBody(), StandardCharsets.UTF_8));
            }
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        VmrRequestQueue.getInstance().addToRequestQueue(renameItemRequest, Constants.Request.FolderNavigation.RenameFileFolder.TAG);
    }

    public void moveItem(Record srcRecord, Record dstRecord){

        try {
            Map<String, String> formData = Vmr.getUserMap();
            formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.MOVE_COPY_LINK_FILE);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("fileNodRef", srcRecord.getNodeRef().replaceAll("\\\\", ""));
            jsonObject.put("fileName", srcRecord.getRecordName());

            jsonArray.put(jsonObject);

            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.NODE_REF, jsonArray.toString());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.DEST_FOLDER_NODE_REF, dstRecord.getNodeRef());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.DEST_FOLDER_NAME, dstRecord.getRecordName());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.OPERATION, "2"); // for move

            MoveCopyLinkRequest moveRequest =
                    new MoveCopyLinkRequest(
                            formData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    onMoveItemListener.onMoveItemSuccess(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    onMoveItemListener.onMoveItemFailure(error);
                                }
                            }
                    );

            VmrRequestQueue.getInstance().addToRequestQueue(moveRequest, Constants.Request.FolderNavigation.MoveCopyLink.TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void linkItem(Record srcRecord, Record dstRecord){

        try {
            Map<String, String> formData = Vmr.getUserMap();
            formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.MOVE_COPY_LINK_FILE);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("fileNodRef", srcRecord.getNodeRef().replaceAll("\\\\", ""));
            jsonObject.put("fileName", srcRecord.getRecordName());

            jsonArray.put(jsonObject);

            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.NODE_REF, jsonArray.toString());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.DEST_FOLDER_NODE_REF, dstRecord.getNodeRef());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.DEST_FOLDER_NAME, dstRecord.getRecordName());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.OPERATION, "1"); // for link

            MoveCopyLinkRequest linkRequest =
                    new MoveCopyLinkRequest(
                            formData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    onLinkItemListener.onLinkItemSuccess(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    onLinkItemListener.onLinkItemFailure(error);
                                }
                            }
                    );

            VmrRequestQueue.getInstance().addToRequestQueue(linkRequest, Constants.Request.FolderNavigation.MoveCopyLink.TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void copyItem(Record srcRecord, Record dstRecord){

        try {
            Map<String, String> formData = Vmr.getUserMap();
            formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.MOVE_COPY_LINK_FILE);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("fileNodRef", srcRecord.getNodeRef().replaceAll("\\\\", ""));
            jsonObject.put("fileName", srcRecord.getRecordName());

            jsonArray.put(jsonObject);

            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.NODE_REF, jsonArray.toString());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.DEST_FOLDER_NODE_REF, dstRecord.getNodeRef());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.DEST_FOLDER_NAME, dstRecord.getRecordName());
            formData.put(Constants.Request.FolderNavigation.MoveCopyLink.OPERATION, "3"); // for copy

            MoveCopyLinkRequest copyRequest =
                    new MoveCopyLinkRequest(
                            formData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    onCopyItemListener.onCopyItemSuccess(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    onCopyItemListener.onCopyItemFailure(error);
                                }
                            }
                    );

            VmrRequestQueue.getInstance().addToRequestQueue(copyRequest, Constants.Request.FolderNavigation.MoveCopyLink.TAG);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void moveToTrash(Record record){

        Map<String, String> formData = Vmr.getUserMap();
//        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DeleteFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DELETE_FILE_OR_FOLDER);

        JSONObject deleteObjectValues = new JSONObject();
        JSONArray deleteObjects = new JSONArray();

        JSONObject trashObjectValues = new JSONObject();
        JSONArray trashObjects = new JSONArray();

        JSONObject objectToDelete = new JSONObject();

        try {
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_NAME, record.getRecordName());
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_NODE_REF, record.getNodeRef());
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

    public void deleteFromTrash(TrashRecord record){

        Map<String, String> formData = Vmr.getUserMap();
//        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DeleteFileFolder.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DELETE_FILE_OR_FOLDER);

        JSONObject deleteObjectValues = new JSONObject();
        JSONArray deleteObjects = new JSONArray();

        JSONObject trashObjectValues = new JSONObject();
        JSONArray trashObjects = new JSONArray();

        JSONObject objectToDelete = new JSONObject();

        try {
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_NAME, record.getRecordName());
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_NODE_REF, record.getNodeRef());
            objectToDelete.put(Constants.Request.FolderNavigation.DeleteFileFolder.OBJECT_TYPE, true);
            trashObjects.put(objectToDelete);
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
                                onDeleteFromTrashListener.onDeleteFromTrashSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onDeleteFromTrashListener.onDeleteFromTrashFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(moveToTrashRequest, Constants.Request.FolderNavigation.DeleteFileFolder.TAG);
    }

    public void fetchSharedByMe(){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.ListSharedByMe.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.LIST_SHARED_BY_ME);
        formData.put(Constants.Request.FolderNavigation.ListSharedByMe.LOGGED_IN_USER_ID, Vmr.getLoggedInUserInfo().getLoggedinUserId());
//        formData.put(Constants.Request.Alfresco.ALFRESCO_TICKET, Vmr.getAlfrescoTicket());

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

        Map<String, String> formData = Vmr.getUserMap();
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

    public void fetchClassifications(){
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.FolderNavigation.Classification.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOCUMENT_CONTENT_TYPES);

        ClassificationRequest classificationRequest =
                new ClassificationRequest(
                        formData,
                        new Response.Listener<Map<String , String >>() {
                            @Override
                            public void onResponse(Map<String , String > response) {
                                onFetchClassifications.onFetchClassificationsSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchClassifications.onFetchClassificationsFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(classificationRequest, Constants.Request.FolderNavigation.Classification.TAG);
    }

    public void fetchProperties( String docType, String nodeRef,  String programName){
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.FolderNavigation.Properties.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOCUMENT_DETAILS);
        formData.put(Constants.Request.FolderNavigation.Properties.DOC_TYPE, docType);
        formData.put(Constants.Request.FolderNavigation.Properties.FILE_NODE_REF, nodeRef);
//        if (!(programName.isEmpty())) {
//            formData.put(Constants.Request.FolderNavigation.Properties.PROGRAM_NAME, programName);
//        }

        PropertiesRequest propertiesRequest =
                new PropertiesRequest(
                        formData,
                        new Response.Listener<Map<String , JSONObject >>() {
                            @Override
                            public void onResponse(Map<String , JSONObject > response) {
                                onFetchProperties.onFetchPropertiesSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchProperties.onFetchPropertiesFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(propertiesRequest, Constants.Request.FolderNavigation.Properties.TAG);
    }

    public void downloadFile(Record record){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void downloadFile(TrashRecord record){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void downloadFile(SharedRecord record){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void downloadFile(SearchResult record){

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.DOWNLOAD_FILE_STREAM);
        formData.put(Constants.Request.FolderNavigation.DownloadFile.NODE_REF, record.getNodeRef());
        formData.put(Constants.Request.FolderNavigation.DownloadFile.FILE_NAME, Uri.encode(record.getRecordName()));
        formData.put(Constants.Request.FolderNavigation.DownloadFile.MIME_TYPE, "application/octet-stream");

        DownloadRequest downloadRequest =
                new DownloadRequest(
                        formData,
                        new Response.Listener<File>() {
                            @Override
                            public void onResponse(File file) {
                                onFileDownload.onFileDownloadSuccess(file);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileDownload.onFileDownloadFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance()
                .addToRequestQueue(downloadRequest, Constants.Request.FolderNavigation.DownloadFile.TAG);
    }

    public void uploadFile(UploadPacket uploadPacket)  {

        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.UploadFile.FILE_NAMES, Uri.encode(uploadPacket.getFileName()));
        formData.put(Constants.Request.FolderNavigation.UploadFile.CONTENT_TYPE, uploadPacket.getContentType());
        formData.put(Constants.Request.FolderNavigation.UploadFile.PARENT_NODE_REF, uploadPacket.getParentNodeRef());

        UploadRequest uploadRequest =
                new UploadRequest(
                        formData,
                        uploadPacket,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                onFileUpload.onFileUploadSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFileUpload.onFileUploadFailure(error);
                            }
                        }
                );

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                VmrDebug.printLogI(this.getClass(), uploadRequest.getHeaders().toString());
                VmrDebug.printLogI(this.getClass(), new String(uploadRequest.getBody(), StandardCharsets.UTF_8));
            }
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        VmrRequestQueue.getInstance()
                .addToRequestQueue(uploadRequest, Constants.Request.FolderNavigation.UploadFile.TAG);
    }

    public void saveIndex(String filePropertyJsonString,
                          String fileSelectedNodeRef,
                          String fileSelectedName,
                          boolean fileIndexStatus,
                          String docCategoryVal,
                          String docType,
                          String programName){
        removeUnwantedParams();
        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.remove(Constants.Request.FolderNavigation.Properties.DOC_TYPE);

        formData.put(Constants.Request.FolderNavigation.SaveIndex.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.SAVE_FILE_PROPERTY);
        formData.put(Constants.Request.FolderNavigation.SaveIndex.FILE_PROPERTY_JSON_STRING, filePropertyJsonString);
        formData.put(Constants.Request.FolderNavigation.SaveIndex.FILE_SELECTED_NODE_REF, fileSelectedNodeRef);
        formData.put(Constants.Request.FolderNavigation.SaveIndex.FILE_NAME, fileSelectedName);
        formData.put(Constants.Request.FolderNavigation.SaveIndex.FILE_INDEX_STATUS, String.valueOf(fileIndexStatus));
        formData.put(Constants.Request.FolderNavigation.SaveIndex.DOCUMENT_CATEGORY_VALUE, docCategoryVal);
        formData.put(Constants.Request.FolderNavigation.SaveIndex.DOCUMENT_TYPE, docType);
//        if (!(programName == null || programName.equals(""))) {
//            formData.put(Constants.Request.FolderNavigation.SaveIndex.PROGRAM_NAME, programName);
//        }

        VmrDebug.printLogI(this.getClass(), formData.toString());

        SaveIndexRequest saveIndexRequest =
                new SaveIndexRequest(
                        formData,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                onSaveIndex.onSaveIndexSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onSaveIndex.onSaveIndexFailure(error);
                            }
                        }
                );

        VmrRequestQueue.getInstance().addToRequestQueue(saveIndexRequest, Constants.Request.FolderNavigation.SaveIndex.TAG);
    }

}
