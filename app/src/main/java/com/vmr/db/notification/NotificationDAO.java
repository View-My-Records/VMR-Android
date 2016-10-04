package com.vmr.db.notification;

import android.content.ContentValues;
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

    // Fetch all notifications in db for current user
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

    public void updateAllNotifications(List<Notification> notifications){
        for (Notification notification : notifications) {
            if(!checkNotification(notification.getId())){
                addNotification(notification);
            } else {
                updateNotifications(notification);
            }
        }
    }

    public boolean updateNotifications(Notification notification){
        ContentValues contentValues = new ContentValues();
//        contentValues.put(DbConstants.INBOX_MASTER_OWNER  , notification.getMasterOwner());
//        contentValues.put(DbConstants.INBOX_ID  , notification.getId());
        contentValues.put(DbConstants.INBOX_TYPE, notification.getType());
        contentValues.put(DbConstants.INBOX_SUBJECT, notification.getSubject());
        contentValues.put(DbConstants.INBOX_HAS_BODY, notification.hasBody());
        contentValues.put(DbConstants.INBOX_BODY, notification.getBody());
        contentValues.put(DbConstants.INBOX_SENDER_FIRST_NAME, notification.getSenderFirstName());
        contentValues.put(DbConstants.INBOX_SENDER_LAST_NAME, notification.getSenderLastName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String date = "";
        if(notification.getCreatedDate() != null)
            date = sdf.format(notification.getCreatedDate());
        contentValues.put(DbConstants.INBOX_CREATION_DATE, date);
        if(notification.getUpdatedDate() != null)
            date = sdf.format(notification.getUpdatedDate());
        contentValues.put(DbConstants.INBOX_UPDATED_DATE, date);

        if(DEBUG) VmrDebug.printLogI(this.getClass(), notification.getId() + " updated");
        return db.update(
                DbConstants.TABLE_INBOX,
                contentValues,
                DbConstants.INBOX_MASTER_OWNER + "=? AND " + DbConstants.INBOX_ID + "=?",
                new String[]{notification.getMasterOwner()+"", notification.getId() + "" }) > 0;
    }

    public long addNotification(Notification notification){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.INBOX_MASTER_OWNER  , notification.getMasterOwner());
        contentValues.put(DbConstants.INBOX_ID  , notification.getId());
        contentValues.put(DbConstants.INBOX_TYPE, notification.getType());
        contentValues.put(DbConstants.INBOX_SUBJECT, notification.getSubject());
        contentValues.put(DbConstants.INBOX_HAS_BODY, notification.hasBody());
        contentValues.put(DbConstants.INBOX_BODY, notification.getBody());
        contentValues.put(DbConstants.INBOX_SENDER_FIRST_NAME, notification.getSenderFirstName());
        contentValues.put(DbConstants.INBOX_SENDER_LAST_NAME, notification.getSenderLastName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String date = "";
        if(notification.getCreatedDate() != null)
            date = sdf.format(notification.getCreatedDate());
        contentValues.put(DbConstants.INBOX_CREATION_DATE, date);
        if(notification.getUpdatedDate() != null)
            date = sdf.format(notification.getUpdatedDate());
        contentValues.put(DbConstants.INBOX_UPDATED_DATE, date);

        if(DEBUG) VmrDebug.printLogI(this.getClass(), notification.getId() + " added");
        return db.insert(DbConstants.TABLE_INBOX, null, contentValues);
    }

    public boolean checkNotification(String inboxId){
        Cursor c = db.query(true,
                DbConstants.TABLE_INBOX,
                new String[]{DbConstants.INBOX_ID},
                DbConstants.INBOX_ID + "=?", new String[]{inboxId},
                null, null, null, null, null);
        if(c != null && c.moveToFirst()){
            c.close();
            return true;
        }
        return false;
    }

    public boolean removeNotification(String inboxId){
        if(DEBUG) VmrDebug.printLogI(this.getClass(), inboxId + " deleted");
        return db.delete(DbConstants.TABLE_INBOX, DbConstants.INBOX_ID + "=?", new String[]{inboxId + ""}) > 0;
    }

    public boolean updateMessageBody(String inboxId, String messageBody){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.INBOX_HAS_BODY, 1);
        contentValues.put(DbConstants.INBOX_BODY, messageBody);
        return db.update(
                DbConstants.TABLE_INBOX,
                contentValues,
                DbConstants.INBOX_ID + "=?",
                new String[]{inboxId + "" }) > 0;
    }

    public void removeAllNotifications(String owner){
        if(DEBUG) VmrDebug.printLogI(this.getClass(), "Notifications deleted");
        db.rawQuery("DELETE FROM " + DbConstants.TABLE_INBOX
                        + " WHERE "
                        + DbConstants.INBOX_MASTER_OWNER + "=?" ,
                new String[]{owner});
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
                if(!c.getString(    c.getColumnIndex(DbConstants.INBOX_UPDATED_DATE)).equals(""))
                    date = sdf.parse(           c.getString(    c.getColumnIndex(DbConstants.INBOX_UPDATED_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            notification.setUpdatedOn(date);
        }
        return notification;
    }
}
