package com.vmr.db.record;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.vmr.app.VMR;
import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 9/5/16.
 */
public class RecordDAO {
    private SQLiteDatabase db;

    public RecordDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<Record> getAllRecords(String parentNode){
        List<Record> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_OWNER + "=? AND " + DbConstants.RECORD_PARENT_NODE_REF + "=?" , // where
                new String[]{VMR.getLoggedInUserInfo().getSerialNo(), parentNode }, // conditions
                null, // group by
                null, // having
                null, // order by
                null );

        if (c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), "Records retrieved");
        return records;
    }


    // Fetch all records in db for current user
    public List<Record> getAllUnIndexedRecords(String parentNode){
        List<Record> records = new ArrayList<>();
        String[] inClause = {"folder", "vmr:unindexed" };
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_OWNER + " =? AND " + DbConstants.RECORD_PARENT_NODE_REF + " =? AND " + DbConstants.RECORD_DOC_TYPE + " IN ( " +
                        TextUtils.join(",", Collections.nCopies(inClause.length, "?")) + ")" , // where
                new String[]{VMR.getLoggedInUserInfo().getSerialNo(), parentNode, inClause[0], inClause[1] }, // conditions
                null, // group by
                null, // having
                null, // order by
                null );

        if (c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), "Records retrieved");
        return records;
    }

    // Fetch all records in db for current user
    public List<Record> getAllSharedWithMeRecords(String parentNode){
        List<Record> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_PARENT_NODE_REF + " =? " , // where
                new String[]{parentNode }, // conditions
                null, // group by
                null, // having
                DbConstants.RECORD_UPDATE_DATE, // order by
                null );

        if (c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), "Records retrieved");
        return records;
    }

    // update all records in db for current user
    public void updateAllRecords(List<Record> records){
        for (Record record : records) {
            if(!checkRecord(record.getRecordNodeRef())){
                addRecord(record);
            } else {
                updateRecord(record);
            }
        }
        VmrDebug.printLogI(this.getClass(), "Records updated");
    }

    // Adds record to DB
    public Long addRecord(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.RECORD_NODE_REF  , record.getRecordNodeRef());
        contentValues.put(DbConstants.RECORD_PARENT_NODE_REF, record.getRecordParentNodeRef());
        contentValues.put(DbConstants.RECORD_NAME      , record.getRecordName());
        contentValues.put(DbConstants.RECORD_DOC_TYPE, record.getRecordDocType());
        contentValues.put(DbConstants.RECORD_FOLDER_CATEGORY, record.getFolderCategory());
        contentValues.put(DbConstants.RECORD_FILE_CATEGORY, record.getFileCategory());
        contentValues.put(DbConstants.RECORD_FILE_SIZE, record.getFileSize());
        contentValues.put(DbConstants.RECORD_FILE_MIME_TYPE, record.getFileMimeType());
        contentValues.put(DbConstants.RECORD_IS_FOLDER, record.isFolder());
        contentValues.put(DbConstants.RECORD_IS_SHARED, record.isShared());
        contentValues.put(DbConstants.RECORD_IS_WRITABLE, record.isWritable());
        contentValues.put(DbConstants.RECORD_IS_DELETABLE, record.isDeletable());
        contentValues.put(DbConstants.RECORD_OWNER, record.getRecordOwner());
        contentValues.put(DbConstants.RECORD_CREATED_BY, record.getCreatedBy());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String date = sdf.format(record.getCreatedDate());
        contentValues.put(DbConstants.RECORD_CREATION_DATE, date);
        contentValues.put(DbConstants.RECORD_UPDATED_BY, record.getUpdatedBy());
        date = sdf.format(record.getUpdatedDate());
        contentValues.put(DbConstants.RECORD_UPDATE_DATE, date);
        VmrDebug.printLogI(this.getClass(), "Record added");
        return db.insert(DbConstants.TABLE_RECORD, null, contentValues);
    }

    // Updates record in db
    private boolean updateRecord(Record record){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.RECORD_PARENT_NODE_REF, record.getRecordParentNodeRef());
        contentValues.put(DbConstants.RECORD_NAME, record.getRecordName());
        contentValues.put(DbConstants.RECORD_DOC_TYPE, record.getRecordDocType());
        contentValues.put(DbConstants.RECORD_FOLDER_CATEGORY, record.getFolderCategory());
        contentValues.put(DbConstants.RECORD_FILE_CATEGORY, record.getFileCategory());
        contentValues.put(DbConstants.RECORD_FILE_SIZE, record.getFileSize());
        contentValues.put(DbConstants.RECORD_FILE_MIME_TYPE, record.getFileMimeType());
        contentValues.put(DbConstants.RECORD_IS_FOLDER, record.isFolder());
        contentValues.put(DbConstants.RECORD_IS_SHARED, record.isShared());
        contentValues.put(DbConstants.RECORD_IS_WRITABLE, record.isWritable());
        contentValues.put(DbConstants.RECORD_IS_DELETABLE, record.isDeletable());
        contentValues.put(DbConstants.RECORD_OWNER, record.getRecordOwner());
        contentValues.put(DbConstants.RECORD_CREATED_BY, record.getCreatedBy());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(record.getCreatedDate());
        contentValues.put(DbConstants.RECORD_CREATION_DATE, date);
        contentValues.put(DbConstants.RECORD_UPDATED_BY,record.getUpdatedBy());
        date = sdf.format(record.getUpdatedDate());
        contentValues.put(DbConstants.RECORD_UPDATE_DATE, date);
        VmrDebug.printLogI(this.getClass(), "Records updated");
        return db.update(
                DbConstants.TABLE_RECORD,
                contentValues,
                DbConstants.RECORD_NODE_REF + "=?",
                new String[]{record.getRecordNodeRef() + ""}) > 0;
    }

    // Returns record from db
    private boolean checkRecord(String nodeRef) {
        Cursor c = db.query(true, DbConstants.TABLE_RECORD, new String[]{DbConstants.RECORD_NODE_REF}, DbConstants.RECORD_NODE_REF + "=?", new String[]{nodeRef + ""}, null, null, null, null, null);
        boolean ret = c.moveToFirst();
        c.close();
        return ret;
    }

    // Delete record
    public boolean deleteRecord(Record record){
        VmrDebug.printLogI(this.getClass(), "Records deleted");
        return db.delete(DbConstants.TABLE_RECORD, DbConstants.RECORD_NODE_REF + "=?", new String[]{record.getRecordNodeRef() + ""}) > 0;
    }

    private Record buildFromCursor(Cursor c) {
        Record record = null;
        if (c != null) {
            record = new Record();
            record.setRecordNodeRef(        c.getString(    c.getColumnIndex(DbConstants.RECORD_NODE_REF)));
            record.setRecordParentNodeRef(  c.getString(    c.getColumnIndex(DbConstants.RECORD_PARENT_NODE_REF)));
            record.setRecordName(           c.getString(    c.getColumnIndex(DbConstants.RECORD_NAME)));
            record.setRecordDocType(        c.getString(    c.getColumnIndex(DbConstants.RECORD_DOC_TYPE)));
            record.setFolderCategory(       c.getString(    c.getColumnIndex(DbConstants.RECORD_FOLDER_CATEGORY)));
            record.setFileCategory(         c.getString(    c.getColumnIndex(DbConstants.RECORD_FILE_CATEGORY)));
            record.setFileSize(             c.getInt(       c.getColumnIndex(DbConstants.RECORD_FILE_SIZE)));
            record.setFileMimeType(         c.getString(    c.getColumnIndex(DbConstants.RECORD_FILE_MIME_TYPE)));
            record.setIsFolder(             c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_FOLDER)) > 0);
            record.setIsShared(             c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_SHARED))>0);
            record.setIsWritable(           c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_WRITABLE))>0);
            record.setIsDeletable(          c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_DELETABLE))>0);
            record.setCreatedBy(            c.getString(    c.getColumnIndex(DbConstants.RECORD_CREATED_BY)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.RECORD_CREATION_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setCreatedDate(date);
            record.setUpdatedBy(            c.getString(    c.getColumnIndex(DbConstants.RECORD_UPDATED_BY)));
            try {
                date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.RECORD_UPDATE_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setUpdatedDate(date);
        }
        return record;
    }
}
