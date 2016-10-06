package com.vmr.home.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.home.request.FetchIndexRequest;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class FetchIndexController {

    private OnFetchIndicesListener onFetchIndicesListener;

    public FetchIndexController(OnFetchIndicesListener onFetchIndicesListener) {
        this.onFetchIndicesListener = onFetchIndicesListener;
    }

    public void fetchIndices(String nodeRef, String programName){
        Map<String, String> formData = Vmr.getUserMap();
        formData.remove(Constants.Request.Alfresco.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FolderNavigation.GetIndex.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.SET_SAVED_FILE_PROPERTIES);
        formData.put(Constants.Request.FolderNavigation.GetIndex.FILE_SELECTED_NODE_REF, nodeRef);
        if(!(programName == null || programName.equals("")))
            formData.put(Constants.Request.FolderNavigation.GetIndex.PROGRAM_NAME, programName);

        FetchIndexRequest request =
                new FetchIndexRequest(
                        formData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                onFetchIndicesListener.onFetchIndexSuccess(jsonObject);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFetchIndicesListener.onFetchIndexFailure(error);
                            }
                        }
                );
        VmrRequestQueue.getInstance().addToRequestQueue(request, Constants.Request.FolderNavigation.GetIndex.TAG);
    }

    public interface OnFetchIndicesListener {
        void onFetchIndexSuccess(JSONObject jsonObject);
        void onFetchIndexFailure(VolleyError error);
    }
}
