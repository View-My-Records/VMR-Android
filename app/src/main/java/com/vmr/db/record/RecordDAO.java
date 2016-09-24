package com.vmr.db.record;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.vmr.app.Vmr;
import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrItem;

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

    private boolean DEBUG = true;

    private SQLiteDatabase db;

    public RecordDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<Record> getAllRecords(String parentNode, boolean override){
        List<Record> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_MASTER_OWNER + "=? AND "
                    +DbConstants.RECORD_PARENT_NODE_REF + "=?", // where
                new String[]{ Vmr.getLoggedInUserInfo().getLoggedinUserId(), // user id
                        parentNode }, // conditions
                null, // group by
                null, // having
                DbConstants.RECORD_IS_FOLDER + " DESC, " + "LOWER(" + DbConstants.RECORD_NAME + ") ASC ", // order by
                null );

        if (c != null && c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), records.size() + " Records retrieved");
        return records;
    }

    // Fetch all records in db for current user
    public List<Record> getFolders(String parentNode){
        List<Record> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_MASTER_OWNER + "=? AND "
                        + DbConstants.RECORD_PARENT_NODE_REF + "=? AND "
                        + DbConstants.RECORD_IS_FOLDER + "=?" , // where
                new String[]{ Vmr.getLoggedInUserInfo().getLoggedinUserId(), // user id
                        parentNode, // noderef
                        1+"" }, // conditions
                null, // group by
                null, // having
                DbConstants.RECORD_IS_FOLDER + " DESC, " + "LOWER(" + DbConstants.RECORD_NAME + ") ASC ", // order by
                null );

        if (c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), records.size() + " Records retrieved");
        return records;
    }

    // Fetch all records in db for current user
    public Record getRecord(String nodeRef){
        Record record = new Record();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_MASTER_OWNER + "=? AND "
                        + DbConstants.RECORD_NODE_REF + "=?" , // where
                new String[]{ Vmr.getLoggedInUserInfo().getLoggedinUserId(), // user id
                        nodeRef }, // noderef
                null, // group by
                null, // having
                null, // order by
                null );

        if (c.moveToFirst()) {
           Record newRecord = buildFromCursor(c);
            if (newRecord != null) {
                record = newRecord;
            }
        }

        VmrDebug.printLogI(this.getClass(), record.getRecordName() + " retrieved");
        return record;
    }


    // Fetch all records in db for current user
    public List<Record> getAllUnIndexedRecords(String parentNode){
        List<Record> records = new ArrayList<>();
        String[] inClause = {"folder", "unindexed" };
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_MASTER_OWNER + "=? AND "
                        + DbConstants.RECORD_PARENT_NODE_REF + " =? AND "
                        + DbConstants.RECORD_DOC_TYPE + " IN ( " +
                        TextUtils.join(",", Collections.nCopies(inClause.length, "?")) + ")" , // where
                new String[]{ Vmr.getLoggedInUserInfo().getLoggedinUserId(), // user id
                        parentNode, // noderef
                        inClause[0], inClause[1] }, // conditions
                null, // group by
                null, // having
                DbConstants.RECORD_IS_FOLDER + " DESC, " + "LOWER(" + DbConstants.RECORD_NAME + ") ASC ", // order by
                null );

        if (c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), records.size() + " Records retrieved");
        return records;
    }

    // Fetch all records in db for current user
    public List<Record> getAllSharedWithMeRecords(String parentNode){
        List<Record> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                DbConstants.RECORD_COLUMNS, // Select columns
                DbConstants.RECORD_MASTER_OWNER + "=? AND " + DbConstants.RECORD_PARENT_NODE_REF + "=?", // where
                new String[]{ Vmr.getLoggedInUserInfo().getLoggedinUserId(), parentNode }, // conditions
                null, // group by
                null, // having
                DbConstants.RECORD_UPDATE_DATE, // order by
                null );

        if (c!= null && c.moveToFirst()) {
            do {
                Record record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), records.size() + " Records retrieved");
        return records;
    }

    // update all records in db for current user
    public void updateAllRecords(List<Record> records){
        for (Record record : records) {
            if(!checkRecord(record.getNodeRef())){
                addRecord(record);
            } else {
                updateRecord(record);
            }
        }
    }

    // Adds record to DB
    public Long addRecord(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.RECORD_MASTER_OWNER  , Vmr.getLoggedInUserInfo().getLoggedinUserId());
        contentValues.put(DbConstants.RECORD_NODE_REF  , record.getNodeRef());
        contentValues.put(DbConstants.RECORD_PARENT_NODE_REF, record.getParentNodeRef());
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
        String date = "";
        if(record.getCreatedDate() != null)
            date = sdf.format(record.getCreatedDate());
        contentValues.put(DbConstants.RECORD_CREATION_DATE, date);
        contentValues.put(DbConstants.RECORD_UPDATED_BY, record.getUpdatedBy());
        if(record.getUpdatedDate() != null)
            date = sdf.format(record.getUpdatedDate());
        else date = "";
        contentValues.put(DbConstants.RECORD_UPDATE_DATE, date);
        contentValues.put(DbConstants.RECORD_LAST_UPDATE_TIMESTAMP, "");
//        date = sdf.format(record.getLastUpdateTimestamp());
//        contentValues.put(DbConstants.RECORD_LAST_UPDATE_TIMESTAMP, date);
        if(DEBUG) VmrDebug.printLogI(this.getClass(), record.getRecordName() + " added");
        return db.insert(DbConstants.TABLE_RECORD, null, contentValues);
    }

    // Updates record in db
    private boolean updateRecord(Record record){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.RECORD_MASTER_OWNER, Vmr.getLoggedInUserInfo().getLoggedinUserId());
        contentValues.put(DbConstants.RECORD_PARENT_NODE_REF, record.getParentNodeRef());
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
//        date = sdf.format(record.getLastUpdateTimestamp());
//        contentValues.put(DbConstants.RECORD_LAST_UPDATE_TIMESTAMP, date);
        if(DEBUG) VmrDebug.printLogI(this.getClass(), record.getRecordName() + " updated");
        return db.update(
                DbConstants.TABLE_RECORD,
                contentValues,
                DbConstants.RECORD_MASTER_OWNER + "=? AND " + DbConstants.RECORD_NODE_REF + "=?",
                new String[]{record.getMasterRecordOwner()+"", record.getNodeRef() + "" }) > 0;
    }

    public boolean updateTimestamp(String nodeRef){
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date(System.currentTimeMillis()));
        contentValues.put(DbConstants.RECORD_LAST_UPDATE_TIMESTAMP, date);
        return db.update(
                DbConstants.TABLE_RECORD,
                contentValues,
                DbConstants.RECORD_NODE_REF + "=?",
                new String[]{nodeRef + "" }) > 0;
    }

    // Returns record from db
    private boolean checkRecord(String nodeRef) {
        Cursor c = db.query(true,
                DbConstants.TABLE_RECORD,
                new String[]{DbConstants.RECORD_NODE_REF},
                DbConstants.RECORD_NODE_REF + "=?", new String[]{nodeRef},
                null, null, null, null, null);
        if(c != null && c.moveToFirst()){
            c.close();
            return true;
        }
        return false;
    }

    // Delete record
    public boolean deleteRecord(Record record){
        if(DEBUG) VmrDebug.printLogI(this.getClass(), record.getRecordName() + " deleted");
        return db.delete(DbConstants.TABLE_RECORD, DbConstants.RECORD_NODE_REF + "=?", new String[]{record.getNodeRef() + ""}) > 0;
    }

    public void removeAllRecords(String parentNodeRef){
        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Records deleted");
        db.rawQuery("DELETE FROM " + DbConstants.TABLE_RECORD
                + " WHERE "
                + DbConstants.RECORD_PARENT_NODE_REF + "=?" ,
                new String[]{ parentNodeRef});
    }

    public void removeAllRecords(String parentNodeRef, VmrFolder vmrFolder){
        ArrayList<String > notInClause = new ArrayList<>();

        for (VmrItem vmrItem : vmrFolder.getAll()){
            notInClause.add(vmrItem.getNodeRef());
        }

        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Records deleted");
        String notInClauseString = notInClause.toString().replace("[", "'").replace("]", "'").replace( ",", "\",\"");
        db.rawQuery("DELETE FROM " + DbConstants.TABLE_RECORD
                + " WHERE "
                + DbConstants.RECORD_PARENT_NODE_REF + "=?"
                + " AND "
                + DbConstants.RECORD_NODE_REF + " NOT IN ( ? )" ,
                new String[]{ parentNodeRef , notInClauseString});
    }

    private Record buildFromCursor(Cursor c) {
        Record record = null;
        if (c != null) {
            record = new Record();
            record.setRecordId(             c.getString(    c.getColumnIndex(DbConstants.RECORD_ID)));
            record.setMasterRecordOwner(    c.getString(    c.getColumnIndex(DbConstants.RECORD_MASTER_OWNER)));
            record.setRecordNodeRef(        c.getString(    c.getColumnIndex(DbConstants.RECORD_NODE_REF)));
            record.setRecordParentNodeRef(  c.getString(    c.getColumnIndex(DbConstants.RECORD_PARENT_NODE_REF)));
            record.setRecordName(           c.getString(    c.getColumnIndex(DbConstants.RECORD_NAME)));
            record.setRecordDocType(        c.getString(    c.getColumnIndex(DbConstants.RECORD_DOC_TYPE)));
            record.setFolderCategory(       c.getString(    c.getColumnIndex(DbConstants.RECORD_FOLDER_CATEGORY)));
            record.setFileCategory(         c.getString(    c.getColumnIndex(DbConstants.RECORD_FILE_CATEGORY)));
            record.setFileSize(             c.getInt(       c.getColumnIndex(DbConstants.RECORD_FILE_SIZE)));
            record.setFileMimeType(         c.getString(    c.getColumnIndex(DbConstants.RECORD_FILE_MIME_TYPE)));
            record.setIsFolder(             c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_FOLDER)) > 0);
            record.setIsShared(             c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_SHARED))> 0);
            record.setIsWritable(           c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_WRITABLE))> 0);
            record.setIsDeletable(          c.getInt(       c.getColumnIndex(DbConstants.RECORD_IS_DELETABLE))> 0);
            record.setRecordOwner(          c.getString(    c.getColumnIndex(DbConstants.RECORD_OWNER)));
            record.setCreatedBy(            c.getString(    c.getColumnIndex(DbConstants.RECORD_CREATED_BY)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                if(!c.getString(    c.getColumnIndex(DbConstants.RECORD_CREATION_DATE)).equals(""))
                    date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.RECORD_CREATION_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setCreatedDate(date);
            record.setUpdatedBy(            c.getString(    c.getColumnIndex(DbConstants.RECORD_UPDATED_BY)));
            try {
                if(!c.getString(    c.getColumnIndex(DbConstants.RECORD_UPDATE_DATE)).equals(""))
                    date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.RECORD_UPDATE_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setUpdatedDate(date);
            try {
                if( !(c.getString(c.getColumnIndex(DbConstants.RECORD_LAST_UPDATE_TIMESTAMP)) == null ) )
                    date = sdf.parse(       c.getString(    c.getColumnIndex(DbConstants.RECORD_LAST_UPDATE_TIMESTAMP))+"");
                else
                    date = null;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setLastUpdateTimestamp(date);
        }
        return record;
    }
}
