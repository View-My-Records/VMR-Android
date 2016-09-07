package com.vmr.db.record;

import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.VMR;

import java.util.List;

/*
 * Created by abhijit on 9/5/16.
 */
public class RecordManager {

    private RecordDAO recordDAO;

    public RecordManager() {
        RecordHelper recordHelper = new RecordHelper(VMR.getVMRContext());
        SQLiteDatabase database = recordHelper.getWritableDatabase();
        recordDAO = new RecordDAO(database);
    }

    public List<Record> getAllRecords(String parentNode) {
        return this.recordDAO.getAllRecord(parentNode);
    }

    public void updateAllRecords(List<Record> records) {
        this.recordDAO.updateAllRecords(records);
    }

    public boolean deleteRecord(Record record) {
        return this.recordDAO.deleteRecord(record);
    }
}
