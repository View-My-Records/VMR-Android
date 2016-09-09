package com.vmr.db.trash;

/*
 * Created by abhijit on 9/8/16.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.VMR;
import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;

import java.util.ArrayList;
import java.util.List;

public class TrashRecordDAO {

    private SQLiteDatabase db;

    public TrashRecordDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<TrashRecord> getAllRecords(String parentNode){
        List<TrashRecord> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_TRASH, // Table Name
                DbConstants.TRASH_COLUMNS, // Select columns
                DbConstants.TRASH_OWNER + "=? AND " + DbConstants.RECORD_PARENT_NODE_REF + "=?" , // where
                new String[]{VMR.getLoggedInUserInfo().getSerialNo(), parentNode }, // conditions
                null, // group by
                null, // having
                DbConstants.TRASH_NAME, // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Records retrieved");
            do {
                TrashRecord record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No records found");
        }

        return records;
    }


    // update all records in db for current user
    public void updateAllRecords(List<TrashRecord> records){
        for (TrashRecord record : records) {
            if(!checkRecord(record.getNodeRef())){
                addRecord(record);
            } else {
                updateRecord(record);
            }
        }
    }

    // Returns record from db
    private boolean checkRecord(String nodeRef) {
        Cursor c = db.query(true,
                DbConstants.TABLE_TRASH,
                new String[]{DbConstants.TRASH_NODE_REF},
                DbConstants.TRASH_NODE_REF + "=?",
                new String[]{nodeRef + ""},
                null, null, null, null, null);
        boolean ret = c.moveToFirst();
        c.close();
        return ret;
    }

    public boolean updateRecord(TrashRecord record){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TRASH_PARENT_NODE_REF, record.getParentNodeRef());
        contentValues.put(DbConstants.TRASH_IS_FOLDER, record.isFolder());
        contentValues.put(DbConstants.TRASH_CREATED_BY, record.getCreatedBy());
        contentValues.put(DbConstants.TRASH_OWNER      , record.getOwner());
        contentValues.put(DbConstants.TRASH_NAME, record.getName());

        VmrDebug.printLogI(this.getClass(), "Records updated");
        return db.update(
                DbConstants.TABLE_TRASH,
                contentValues,
                DbConstants.TRASH_NODE_REF + "=?",
                new String[]{record.getNodeRef() + ""}) > 0;
    }

    public Long addRecord(TrashRecord record){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TRASH_NODE_REF, record.getNodeRef());
        contentValues.put(DbConstants.TRASH_PARENT_NODE_REF, record.getParentNodeRef());
        contentValues.put(DbConstants.TRASH_IS_FOLDER, record.isFolder());
        contentValues.put(DbConstants.TRASH_CREATED_BY, record.getCreatedBy());
        contentValues.put(DbConstants.TRASH_OWNER      , record.getOwner());
        contentValues.put(DbConstants.TRASH_NAME, record.getName());
        VmrDebug.printLogI(this.getClass(), "Record added");
        return db.insert(DbConstants.TABLE_TRASH, null, contentValues);
    }

    // Delete record
    public boolean deleteRecord(TrashRecord record){
        VmrDebug.printLogI(this.getClass(), "Records deleted");
        return db.delete(
                DbConstants.TABLE_TRASH,
                DbConstants.TRASH_RECORD_ID + "=?",
                new String[]{record.getId() + ""}) > 0;
    }

    private TrashRecord buildFromCursor(Cursor c) {
        TrashRecord record = null;
        if (c != null) {
            record = new TrashRecord();
            record.setId(            c.getInt(    c.getColumnIndex(DbConstants.TRASH_RECORD_ID)));
            record.setNodeRef(       c.getString( c.getColumnIndex(DbConstants.TRASH_NODE_REF)));
            record.setParentNodeRef( c.getString( c.getColumnIndex(DbConstants.TRASH_PARENT_NODE_REF)));
            record.setIsFolder(      c.getInt(    c.getColumnIndex(DbConstants.SHARED_IS_FOLDER)) > 0);
            record.setCreatedBy(     c.getString( c.getColumnIndex(DbConstants.TRASH_CREATED_BY)));
            record.setName(          c.getString( c.getColumnIndex(DbConstants.TRASH_NAME)));
            record.setOwner(         c.getString( c.getColumnIndex(DbConstants.TRASH_OWNER)));
        }
        return record;
    }
}
