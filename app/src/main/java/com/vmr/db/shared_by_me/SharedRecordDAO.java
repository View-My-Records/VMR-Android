package com.vmr.db.shared_by_me;

/*
 * Created by abhijit on 9/8/16.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.VMR;
import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SharedRecordDAO {

    private SQLiteDatabase db;

    public SharedRecordDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<SharedRecord> getAllRecords(){
        List<SharedRecord> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_SHARED, // Table Name
                DbConstants.SHARED_COLUMNS, // Select columns
                DbConstants.SHARED_OWNER_NAME + "=?" , // where
                new String[]{VMR.getLoggedInUserInfo().getSerialNo()}, // conditions
                null, // group by
                null, // having
                null, // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Records retrieved");
            do {
                SharedRecord record = buildFromCursor(c);
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
    public void updateAllRecords(List<SharedRecord> records){
        for (SharedRecord record : records) {
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
                DbConstants.TABLE_SHARED,
                new String[]{DbConstants.SHARED_NODE_REF},
                DbConstants.SHARED_NODE_REF + "=?",
                new String[]{nodeRef + ""},
                null, null, null, null, null);
        boolean ret = c.moveToFirst();
        c.close();
        return ret;
    }

    public boolean updateRecord(SharedRecord record){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.SHARED_PARENT_NODE_REF, record.getParentNodeRef());
        contentValues.put(DbConstants.SHARED_OWNER_NAME      , record.getOwnerName());
        contentValues.put(DbConstants.SHARED_IS_FOLDER, record.isFolder());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String date = sdf.format(record.getRecordLife());
        contentValues.put(DbConstants.SHARED_RECORD_LIFE, date);
        contentValues.put(DbConstants.SHARED_TO_EMAIL_ID, record.getSharedToEmailId());
        contentValues.put(DbConstants.SHARED_USER_ID, record.getUserId());
        contentValues.put(DbConstants.SHARED_FILE_NAME, record.getRecordName());
        contentValues.put(DbConstants.SHARED_PERMISSIONS, record.getPermissions());

        VmrDebug.printLogI(this.getClass(), "Records updated");
        return db.update(
                DbConstants.TABLE_SHARED,
                contentValues,
                DbConstants.SHARED_NODE_REF + "=?",
                new String[]{record.getNodeRef() + ""}) > 0;
    }

    public Long addRecord(SharedRecord record){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.SHARED_NODE_REF  , record.getNodeRef());
        contentValues.put(DbConstants.SHARED_PARENT_NODE_REF, record.getParentNodeRef());
        contentValues.put(DbConstants.SHARED_OWNER_NAME      , record.getOwnerName());
        contentValues.put(DbConstants.SHARED_IS_FOLDER, record.isFolder());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String date = sdf.format(record.getRecordLife());
        contentValues.put(DbConstants.SHARED_RECORD_LIFE, date);
        contentValues.put(DbConstants.SHARED_TO_EMAIL_ID, record.getSharedToEmailId());
        contentValues.put(DbConstants.SHARED_USER_ID, record.getUserId());
        contentValues.put(DbConstants.SHARED_FILE_NAME, record.getRecordName());
        contentValues.put(DbConstants.SHARED_PERMISSIONS, record.getPermissions());
        VmrDebug.printLogI(this.getClass(), "Record added");
        return db.insert(DbConstants.TABLE_SHARED, null, contentValues);
    }

    // Delete record
    public boolean deleteRecord(SharedRecord record){
        VmrDebug.printLogI(this.getClass(), "Records deleted");
        return db.delete(
                DbConstants.TABLE_SHARED,
                DbConstants.SHARED_NODE_REF + "=?",
                new String[]{record.getNodeRef() + ""}) > 0;

    }

    private SharedRecord buildFromCursor(Cursor c) {
        SharedRecord record = null;
        if (c != null) {
            record = new SharedRecord();
            record.setOwnerName(        c.getString(    c.getColumnIndex(DbConstants.SHARED_OWNER_NAME)));
            record.setIsFolder(         c.getInt(       c.getColumnIndex(DbConstants.SHARED_IS_FOLDER)) > 0);
            record.setSharedToEmailId(  c.getString(    c.getColumnIndex(DbConstants.SHARED_TO_EMAIL_ID)));
            record.setUserId(           c.getString(    c.getColumnIndex(DbConstants.SHARED_USER_ID)));
            record.setFileName(         c.getString(    c.getColumnIndex(DbConstants.SHARED_FILE_NAME)));
            record.setPermissions(      c.getString(    c.getColumnIndex(DbConstants.SHARED_PERMISSIONS)));
            record.setNodeRef(          c.getString(    c.getColumnIndex(DbConstants.SHARED_NODE_REF)));
            record.setParentNodeRef(    c.getString(    c.getColumnIndex(DbConstants.SHARED_PARENT_NODE_REF)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                date = sdf.parse(       c.getString(    c.getColumnIndex(DbConstants.SHARED_RECORD_LIFE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setRecordLife(date);

        }
        return record;
    }
}
