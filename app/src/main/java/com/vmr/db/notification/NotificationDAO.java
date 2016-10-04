package com.vmr.db.notification;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.Vmr;
import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 10/4/16.
 */

public class NotificationDAO {

    private boolean DEBUG = true;

    private SQLiteDatabase db;

    public NotificationDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<Notification> getAllNotifications(){
        List<Notification> notifications = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_INBOX, // Table Name
                DbConstants.INBOX_COLUMNS, // Select columns
                DbConstants.INBOX_MASTER_OWNER + "=?", // where
                new String[]{ Vmr.getLoggedInUserInfo().getLoggedinUserId() }, // user id
                null, // group by
                null, // having
                DbConstants.INBOX_CREATION_DATE + " DESC ", // order by
                null );

        if (c != null && c.moveToFirst()) {
            do {
                Notification notification = buildFromCursor(c);
                if (notification != null) {
                    notifications.add(notification);
                }
            } while (c.moveToNext());
        }

        VmrDebug.printLogI(this.getClass(), notifications.size() + " Notifications retrieved");
        return notifications;
    }

    private Notification buildFromCursor(Cursor c) {
        Notification notification = null;
        if (c != null) {
            notification = new Notification();
            notification.setId(             c.getString(    c.getColumnIndex(DbConstants.INBOX_ID)));
            notification.setMasterOwner(    c.getString(    c.getColumnIndex(DbConstants.INBOX_MASTER_OWNER)));
            notification.setType(        c.getInt(    c.getColumnIndex(DbConstants.INBOX_TYPE)));
            notification.setSubject(  c.getString(    c.getColumnIndex(DbConstants.INBOX_SUBJECT)));
            notification.setHasBody(           c.getInt(    c.getColumnIndex(DbConstants.INBOX_HAS_BODY)) > 0);
            notification.setBody(        c.getString(    c.getColumnIndex(DbConstants.INBOX_BODY)));
            notification.setSenderFirstName(       c.getString(    c.getColumnIndex(DbConstants.INBOX_SENDER_FIRST_NAME)));
            notification.setSenderLastName(         c.getString(    c.getColumnIndex(DbConstants.INBOX_SENDER_LAST_NAME)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                if(!c.getString(    c.getColumnIndex(DbConstants.INBOX_CREATION_DATE)).equals(""))
                    date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.INBOX_CREATION_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            notification.setCreatedOn(date);

            try {
                if(!c.getString(    c.getColumnIndex(DbConstants.INBOX_UPDATE_DATE)).equals(""))
                    date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.INBOX_UPDATE_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            notification.setUpdatedOn(date);
        }
        return notification;
    }
}
