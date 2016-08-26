package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.model.folder_structure.VmrFolder;

/*
 * Created by abhijit on 8/23/16.
 */
public class Interaction {

    public interface HomeToMyRecordsInterface {
        void onReceiveFromActivitySuccess(VmrFolder vmrFolder);
        void onReceiveFromActivityFailure(VolleyError error);
    }

    public interface HomeToUnIndexedInterface {
        void onReceiveFromActivitySuccess(VmrFolder vmrFolder);
        void onReceiveFromActivityFailure(VolleyError error);
    }
}
