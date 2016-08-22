package com.vmr.home;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.home.interfaces.MyRecordsRequestInterface;
import com.vmr.home.request.MyRecordsRequest;
import com.vmr.model.MyRecords;
import com.vmr.network.VolleySingleton;
import com.vmr.utils.Constants;

import org.json.JSONObject;

import java.util.Map;

/*
 * Created by abhijit on 8/17/16.
 */

public class HomeController {

    private MyRecordsRequestInterface myRecordsRequestInterface;

    public HomeController(MyRecordsRequestInterface myRecordsRequestInterface) {
        this.myRecordsRequestInterface = myRecordsRequestInterface;
    }
    // TODO: 8/19/16 Create different requests with parsers implemented in it.

    public void fetchAllFilesAndFolders(Map<String, String> formData){
        MyRecordsRequest recordsRequest =
                new MyRecordsRequest(
                        formData,
                        new Response.Listener<MyRecords>() {
                            @Override
                            public void onResponse(MyRecords myRecords) {
                                myRecordsRequestInterface.fetchFilesAndFoldersSuccess(myRecords);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                myRecordsRequestInterface.fetchFilesAndFoldersFailure(error);
                            }
                        } );
        VolleySingleton.getInstance().addToRequestQueue(recordsRequest, Constants.VMR_FOLDER_NAVIGATION_TAG);
    }
}
