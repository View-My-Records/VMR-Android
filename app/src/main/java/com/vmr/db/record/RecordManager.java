package com.vmr.db.record;

import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.VMR;

import java.util.List;

/*
 * Created by abhijit on 9/5/16.
 */
public class RecordManager {

    private SQLiteDatabase database;
    private RecordHelper recordHelper;
    private RecordDAO recordDAO;

    public RecordManager() {
        recordHelper = new RecordHelper(VMR.getVMRContext());
        database = recordHelper.getWritableDatabase();
        recordDAO = new RecordDAO(database);
    }

    public List<Record> getAllRecords(String parentNode) {
        return this.recordDAO.getAllRecord(parentNode);
    }

    public List<Record> updateAllRecords(String parentNode ,List<Record> records) {
        return this.recordDAO.updateAllRecord(parentNode, records);
    }

    public int deleteRecord(String parentNode) {
        return this.recordDAO.deleteRecord(parentNode);
    }
}
