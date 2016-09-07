package com.vmr.db.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;
import com.vmr.model.UserInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Created by abhijit on 9/6/16.
 */
public class UserDAO {

    private SQLiteDatabase db;

    public UserDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Adds record to DB
    public Long addUser(UserInfo userInfo) {
        VmrDebug.printLogI(this.getClass(), "User Info adding");
        if(!checkUser(userInfo.getSerialNo())) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConstants.USER_SERIAL_NO, userInfo.getSerialNo());
            contentValues.put(DbConstants.USER_RESULT, userInfo.getResult());
            contentValues.put(DbConstants.USER_ROOT_NODE_REF, userInfo.getRootNodref());
            contentValues.put(DbConstants.USER_URL_TYPE, userInfo.getUrlType());
            contentValues.put(DbConstants.USER_TYPE, userInfo.getUserType());
            contentValues.put(DbConstants.USER_MEMBERSHIP_TYPE, userInfo.getMembershipType());
            contentValues.put(DbConstants.USER_CORP_NAME, userInfo.getCorpName());
            contentValues.put(DbConstants.USER_EMAIL_ID, userInfo.getEmailId());
            contentValues.put(DbConstants.USER_EMP_TYPE, userInfo.getEmpType());
            contentValues.put(DbConstants.USER_ID, userInfo.getUserId());
            contentValues.put(DbConstants.USER_SESSION_ID, userInfo.getHttpSessionId());
            contentValues.put(DbConstants.USER_USER_NAME, userInfo.getUserName());
            contentValues.put(DbConstants.USER_CORP_ID, userInfo.getCorpId());
            contentValues.put(DbConstants.USER_FIRST_NAME, userInfo.getFirstName());
            contentValues.put(DbConstants.USER_LAST_NAME, userInfo.getLastName());
            contentValues.put(DbConstants.USER_LOGGED_IN_USER_ID, userInfo.getLoggedinUserId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String date = sdf.format(userInfo.getLastLoginTime());
            contentValues.put(DbConstants.USER_LAST_LOGIN, date);
            return db.insert(DbConstants.TABLE_USER, null, contentValues);
        } else {
            updateUser(userInfo);
            return 1L;
        }
    }

    // Updates record in db
    public User updateUser(UserInfo userInfo){
        VmrDebug.printLogI(this.getClass(), "User Info Updating");
        if(!checkUser(userInfo.getSerialNo())) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConstants.USER_RESULT, userInfo.getResult());
            contentValues.put(DbConstants.USER_ROOT_NODE_REF, userInfo.getRootNodref());
            contentValues.put(DbConstants.USER_URL_TYPE, userInfo.getUrlType());
            contentValues.put(DbConstants.USER_TYPE, userInfo.getUserType());
            contentValues.put(DbConstants.USER_MEMBERSHIP_TYPE, userInfo.getMembershipType());
            contentValues.put(DbConstants.USER_CORP_NAME, userInfo.getCorpName());
            contentValues.put(DbConstants.USER_EMAIL_ID, userInfo.getEmailId());
            contentValues.put(DbConstants.USER_EMP_TYPE, userInfo.getEmpType());
            contentValues.put(DbConstants.USER_ID, userInfo.getUserId());
            contentValues.put(DbConstants.USER_SESSION_ID, userInfo.getHttpSessionId());
            contentValues.put(DbConstants.USER_USER_NAME, userInfo.getUserName());
            contentValues.put(DbConstants.USER_CORP_ID, userInfo.getCorpId());
            contentValues.put(DbConstants.USER_FIRST_NAME, userInfo.getFirstName());
            contentValues.put(DbConstants.USER_LAST_NAME, userInfo.getLastName());
            contentValues.put(DbConstants.USER_LOGGED_IN_USER_ID, userInfo.getLoggedinUserId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String date = sdf.format(userInfo.getLastLoginTime());
            contentValues.put(DbConstants.USER_LAST_LOGIN, date);
            db.update(
                    DbConstants.TABLE_USER,
                    contentValues,
                    DbConstants.USER_SERIAL_NO + "=?",
                    new String[]{userInfo.getSerialNo() + ""});
            return getUser(userInfo.getSerialNo());
        } else {
            return null;
        }
    }

    // Returns record from db
    private boolean checkUser(String serialNo){
        Cursor c = db.query(true, DbConstants.TABLE_USER, DbConstants.USER_COLUMNS, DbConstants.USER_SERIAL_NO + "=?", new String[]{ serialNo + ""}, null, null, null, null, null);
        boolean ret = c.moveToFirst();
        c.close();
        VmrDebug.printLogI(this.getClass(), "User Info retrieved");
        return ret;
    }

    public User getUser(String  serialNo) {
        User user = null;
        Cursor c = db.query(true,
                DbConstants.TABLE_USER,
                DbConstants.USER_COLUMNS,
                DbConstants.USER_SERIAL_NO + "=?",
                new String[]{serialNo + ""}, null, null, null, null, null);

        if (c != null && c.moveToFirst()) {
            user = buildFromCursor(c);
            if (!c.isClosed()) {
                c.close();
            }
        }
        VmrDebug.printLogI(this.getClass(), "User Info retrieved");
        return user;
    }

    public boolean deleteUser(String serialNo) {
        return db.delete(DbConstants.TABLE_USER, DbConstants.USER_SERIAL_NO + "=?", new String[]{ serialNo + ""}) > 0;
    }

    private User buildFromCursor(Cursor c) {
        User user = null;
        if (c != null) {
            user = new User();
            user.setSerialNo(c.getString(c.getColumnIndex(DbConstants.USER_SERIAL_NO)));
            user.setResult(c.getString(c.getColumnIndex(DbConstants.USER_RESULT)));
            user.setRootNodeRef(c.getString(c.getColumnIndex(DbConstants.USER_ROOT_NODE_REF)));
            user.setUrlType(c.getString(c.getColumnIndex(DbConstants.USER_URL_TYPE)));
            user.setUserType(c.getString(c.getColumnIndex(DbConstants.USER_TYPE)));
            user.setMembershipType(c.getString(c.getColumnIndex(DbConstants.USER_MEMBERSHIP_TYPE)));
            user.setCorpName(c.getString(c.getColumnIndex(DbConstants.USER_CORP_NAME)));
            user.setEmailId(c.getString(c.getColumnIndex(DbConstants.USER_EMAIL_ID)));
            user.setEmpType(c.getString(c.getColumnIndex(DbConstants.USER_EMP_TYPE)));
            user.setUserId(c.getString(c.getColumnIndex(DbConstants.USER_ID)));
            user.setSessionId(c.getString(c.getColumnIndex(DbConstants.USER_SESSION_ID)));
            user.setUserName(c.getString(c.getColumnIndex(DbConstants.USER_USER_NAME)));
            user.setCorpId(c.getString(c.getColumnIndex(DbConstants.USER_CORP_ID)));
            user.setLoggedInUserId(c.getString(c.getColumnIndex(DbConstants.USER_LOGGED_IN_USER_ID)));
            user.setFirstName(c.getString(c.getColumnIndex(DbConstants.USER_FIRST_NAME)));
            user.setLastName(c.getString(c.getColumnIndex(DbConstants.USER_LAST_NAME)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                date = sdf.parse(c.getString(c.getColumnIndex(DbConstants.USER_LAST_LOGIN)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setLastLogin(date);
        }
        return user;
    }
}
