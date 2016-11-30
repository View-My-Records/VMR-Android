package com.vmr.db.recently_accessed;

/*
 * Created by abhijit on 9/8/16.
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

public class RecentDAO {

    private SQLiteDatabase db;

    public RecentDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<Recent> getAllRecents(){
        List<Recent> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECENT, // Table Name
                DbConstants.RECENT_COLUMNS, // Select columns
                DbConstants.RECENT_MASTER_OWNER + "=?", // where
                new String[]{PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ID)}, // conditions
                null, // group by
                null, // having
                DbConstants.RECENT_LAST_ACCESSED + " DESC", // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Records retrieved");
            do {
                Recent record = buildFromCursor(c);
                if (record != null) {
                    records.add(record);
                }
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No records found");
        }

        return records;
    }

    // Returns record from db
    public boolean checkRecord(String nodeRef) {
        Cursor c = db.query(true,
                DbConstants.TABLE_RECENT,
                new String[]{DbConstants.RECENT_NODE_REF},
                DbConstants.RECENT_NODE_REF + "=?",
                new String[]{nodeRef + ""},
                null, null, null, null, null);
        boolean ret = c.moveToFirst();
//        c.close();
        return ret;
    }

    public boolean updateRecord(Recent record){

        ContentValues contentValues = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(record.getLastAccess());
        contentValues.put(DbConstants.RECENT_LAST_ACCESSED, date);
        contentValues.put(DbConstants.RECENT_IS_INDEXED, record.isIndexed());

        VmrDebug.printLogI(this.getClass(), record.getName() + " updated");

        return db.update(
                DbConstants.TABLE_RECENT,
                contentValues,
                DbConstants.RECENT_NODE_REF + "=?",
                new String[]{record.getNodeRef() + ""}) > 0;
    }

    public Long addRecent(Recent record){

        ContentValues contentValues = new ContentValues();

        contentValues.put(DbConstants.RECENT_NODE_REF, record.getNodeRef());
        contentValues.put(DbConstants.RECENT_MASTER_OWNER, record.getMasterRecordOwner());
        contentValues.put(DbConstants.RECENT_NAME, record.getName());
        contentValues.put(DbConstants.RECENT_IS_INDEXED, record.isIndexed());
        contentValues.put(DbConstants.RECENT_LOCATION, record.getLocation());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(record.getLastAccess());
        contentValues.put(DbConstants.RECENT_LAST_ACCESSED, date);

        VmrDebug.printLogI(this.getClass(), record.getName() + " added");
        return db.insert(DbConstants.TABLE_RECENT, null, contentValues);
    }

    // Delete record
    public boolean deleteRecord(Recent record){
        VmrDebug.printLogI(this.getClass(), "Records deleted");
        return db.delete(
                DbConstants.TABLE_RECENT,
                DbConstants.RECENT_NODE_REF + "=?",
                new String[]{record.getNodeRef() + ""}) > 0;
    }

    private Recent buildFromCursor(Cursor c) {
        Recent record = null;
        if (c != null) {
            record = new Recent();
            record.setId(               c.getInt(    c.getColumnIndex(DbConstants.RECENT_RECORD_ID)));
            record.setMasterRecordOwner(c.getString( c.getColumnIndex(DbConstants.RECENT_MASTER_OWNER)));
            record.setNodeRef(          c.getString( c.getColumnIndex(DbConstants.RECENT_NODE_REF)));
            record.setName(             c.getString( c.getColumnIndex(DbConstants.RECENT_NAME)));
            record.setIndexed(          c.getInt(    c.getColumnIndex(DbConstants.RECENT_IS_INDEXED)) > 0);
            record.setLocation(         c.getString( c.getColumnIndex(DbConstants.RECENT_LOCATION)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                if(                    !c.getString( c.getColumnIndex(DbConstants.RECENT_LAST_ACCESSED)).equals(""))
                    date = sdf.parse(   c.getString( c.getColumnIndex(DbConstants.RECENT_LAST_ACCESSED)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.setLastAccess(date);
        }
        return record;
    }
}
