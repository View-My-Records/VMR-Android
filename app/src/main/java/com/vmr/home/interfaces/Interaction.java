package com.vmr.home.interfaces;

import com.android.volley.VolleyError;
import com.vmr.db.record.Record;
import com.vmr.model.VmrFolder;

import java.util.List;

/*
 * Created by abhijit on 8/23/16.
 */
public class Interaction {

    public interface HomeToMyRecordsInterface {
        void onReceiveFromActivitySuccess(List<Record> records);
        void onReceiveFromActivityFailure(VolleyError error);
    }

    public interface HomeToUnIndexedInterface {
        void onReceiveFromActivitySuccess(VmrFolder vmrFolder);
        void onReceiveFromActivityFailure(VolleyError error);
    }
}
