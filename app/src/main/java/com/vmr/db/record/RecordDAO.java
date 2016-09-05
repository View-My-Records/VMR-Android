package com.vmr.db.record;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 9/5/16.
 */
public class RecordDAO {
    private SQLiteDatabase db;

    public RecordDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Adds record to DB
    public Long addRecord(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordHelper.COL_ITEM_NODE_REF, record.getRecordNodeRef());
        return db.insert(RecordHelper.TABLE_NAME, null, contentValues);
    }

    // Updates record in db
    private void updateRecord(Record record){

    }

    // Returns record from db
    private int checkRecord(String noderef){

        return 0;
    }

    // Fetch all records in db for current user
    public List<Record> getAllRecord(String parentNode){
        List<Record> records = new ArrayList<>();

        return records;
    }

    // Updates record in db
    public List<Record> updateAllRecord(String parentNode, List<Record> records){

        return records;
    }

    // Delete record
    public int deleteRecord(String noderef){

        return 0;
    }


    /*

    public boolean updateTrip(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordHelper.COL_ID, trip.getTripId());
        contentValues.put(RecordHelper.COL_TITLE, trip.getTitle());
        contentValues.put(RecordHelper.COL_UPDATE_DATE, trip.getUpdateDate());
        return db.update(RecordHelper.TABLE_NAME, contentValues, TripsDBHelper.COL_ID + "=?", new String[]{trip.getTripId() + ""}) > 0;
    }

    public boolean deleteTrip(Trip trip) {
        return db.delete(TripsDBHelper.TABLE_NAME, TripsDBHelper.COL_ID + "=?", new String[]{trip.getTripId() + ""}) > 0;
    }

    public Trip Record(long id) {
        Trip trip = null;
        Cursor c = db.query(true, TripsDBHelper.TABLE_NAME, TripsDBHelper.ALL_COLUMNS, TripsDBHelper.COL_ID + "=?", new String[]{id + ""}, null, null, null, null, null);

        if (c != null && c.moveToFirst()) {
            trip = buildFromCursor(c);
            if (!c.isClosed()) {
                c.close();
            }
        }
        return trip;
    }

    public List<Trip> getAll() {
        List<Trip> tripList = new ArrayList<Trip>();

        String dbQuery = "SELECT * FROM " + TripsDBHelper.TABLE_NAME + " ORDER BY " + TripsDBHelper.COL_UPDATE_DATE + " DESC";
        Cursor c = db.rawQuery(dbQuery, null);

        if (c != null && c.moveToFirst()) {
            do {
                Trip trip = buildFromCursor(c);
                if (trip != null) {
                    tripList.add(trip);
                }
            } while (c.moveToNext());
        }
        return tripList;
    }


    private Trip buildFromCursor(Cursor c) {
        Trip trip = null;
        if (c != null) {
            trip = new Trip();
            trip.setTripId(c.getInt(0));
            trip.setTitle(c.getString(1));
            trip.setCreateDate(c.getString(2));
            trip.setUpdateDate(c.getString(3));
        }
        return trip;
    }
    */
}
