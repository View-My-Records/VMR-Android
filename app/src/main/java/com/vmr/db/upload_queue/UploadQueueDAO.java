package com.vmr.db.upload_queue;

/*
 * Created by abhijit on 10/11/16.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UploadQueueDAO {

    private boolean DEBUG = true;

    private SQLiteDatabase db;

    public UploadQueueDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public List<UploadQueue> fetchAllUploads(){
        List<UploadQueue> uploads = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_UPLOAD_QUEUE, // Table Name
                DbConstants.UPLOAD_COLUMNS, // Select columns
                DbConstants.UPLOAD_OWNER + "=?", // where
                new String[]{ PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID) }, // conditions
                null, // group by
                null, // having
                DbConstants.UPLOAD_DATE + " DESC ", // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Upload Queue retrieved");
            do {
                UploadQueue upload = buildFromCursor(c);
                if (upload != null) {
                    uploads.add(upload);
                }
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No items in upload queue found");
        }

        return uploads;
    }

    public List<UploadQueue> fetchAllPendingUploads(){
        List<UploadQueue> uploads = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_UPLOAD_QUEUE, // Table Name
                DbConstants.UPLOAD_COLUMNS, // Select columns
                DbConstants.UPLOAD_OWNER + "=? AND " +  DbConstants.UPLOAD_STATUS + "=?", // where
                new String[]{ PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID), String.valueOf(UploadQueue.STATUS_PENDING)}, // conditions
                null, // group by
                null, // having
                DbConstants.UPLOAD_DATE + " DESC ", // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Upload Queue retrieved");
            do {
                UploadQueue upload = buildFromCursor(c);
                if (upload != null) {
                    uploads.add(upload);
                }
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No items in upload queue found");
        }

        return uploads;
    }

    public Long addUpload(UploadQueue uploadQueue){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.UPLOAD_OWNER, uploadQueue.getOwner());
        contentValues.put(DbConstants.UPLOAD_FILE_PATH, uploadQueue.getFileUri());
        contentValues.put(DbConstants.UPLOAD_FILE_NAME, uploadQueue.getFileName());
        contentValues.put(DbConstants.UPLOAD_PARENT_NODE, uploadQueue.getParentNodeRef());
        contentValues.put(DbConstants.UPLOAD_CONTENT_TYPE, uploadQueue.getContentType());
        contentValues.put(DbConstants.UPLOAD_STATUS      , uploadQueue.getStatus());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(uploadQueue.getCreationDate());
        contentValues.put(DbConstants.UPLOAD_DATE, date);
        VmrDebug.printLogI(this.getClass(), "Upload queued");
        return db.insert(DbConstants.TABLE_UPLOAD_QUEUE, null, contentValues);
    }

    // Delete record
    public boolean updateUpload(int uploadId, int status){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.UPLOAD_STATUS, status);
        boolean result = db.update(
                DbConstants.TABLE_UPLOAD_QUEUE,
                contentValues,
                DbConstants.UPLOAD_OWNER + "=? AND "
                        + DbConstants.UPLOAD_ID + "=?",
                new String[]{PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID),
                        String.valueOf(uploadId)}) > 0;

        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Updated upload queue");

        return result;
    }

    // Delete record
    public boolean deleteRecord(UploadQueue uploadQueue){
        boolean result = db.delete(
                DbConstants.TABLE_UPLOAD_QUEUE,
                DbConstants.UPLOAD_ID + "=?",
                new String[]{uploadQueue.getId() + ""}) > 0;
        VmrDebug.printLogI(this.getClass(), "Item deleted");
        return result;
    }

    private UploadQueue buildFromCursor(Cursor c) {
        UploadQueue upload = null;
        if (c != null) {
            upload = new UploadQueue();
            upload.setId(               c.getInt(    c.getColumnIndex(DbConstants.UPLOAD_ID)));
            upload.setOwner(            c.getString( c.getColumnIndex(DbConstants.UPLOAD_OWNER)));
            upload.setFileUri(         c.getString( c.getColumnIndex(DbConstants.UPLOAD_FILE_PATH)));
            upload.setFileName(         c.getString( c.getColumnIndex(DbConstants.UPLOAD_FILE_NAME)));
            upload.setParentNodeRef(    c.getString( c.getColumnIndex(DbConstants.UPLOAD_PARENT_NODE)));
            upload.setContentType(      c.getString(    c.getColumnIndex(DbConstants.UPLOAD_CONTENT_TYPE)));
            upload.setStatus(           c.getInt( c.getColumnIndex(DbConstants.UPLOAD_STATUS)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                if(!c.getString(        c.getColumnIndex(DbConstants.UPLOAD_DATE)).equals(""))
                    date = sdf.parse(   c.getString(    c.getColumnIndex(DbConstants.UPLOAD_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            upload.setCreateDate(date);
        }
        return upload;
    }
}
