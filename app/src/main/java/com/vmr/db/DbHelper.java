package com.vmr.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmr.app.VMR;

/*
 * Created by abhijit on 9/7/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    private String createUserTable;
    private String createRecordTable;

    public DbHelper() {
        super(VMR.getVMRContext(), DbConstants.DATABASE_NAME, null, DbConstants.VERSION);
        createUserTable = ("CREATE TABLE " + DbConstants.TABLE_USER + " (") +
                DbConstants.USER_SERIAL_NO + " TEXT PRIMARY KEY, " +
                DbConstants.USER_RESULT + " TEXT, " +
                DbConstants.USER_ROOT_NODE_REF + " TEXT, " +
                DbConstants.USER_URL_TYPE + " TEXT, " +
                DbConstants.USER_TYPE + " TEXT, " +
                DbConstants.USER_MEMBERSHIP_TYPE + " TEXT, " +
                DbConstants.USER_CORP_NAME + " TEXT, " +
                DbConstants.USER_EMAIL_ID + " TEXT, " +
                DbConstants.USER_EMP_TYPE + " TEXT, " +
                DbConstants.USER_ID + " TEXT, " +
                DbConstants.USER_SESSION_ID + " TEXT, " +
                DbConstants.USER_USER_NAME + " TEXT, " +
                DbConstants.USER_CORP_ID + " TEXT, " +
                DbConstants.USER_LOGGED_IN_USER_ID + " TEXT, " +
                DbConstants.USER_LAST_NAME + " TEXT, " +
                DbConstants.USER_FIRST_NAME + " TEXT, " +
                DbConstants.USER_LAST_LOGIN + " DATETIME );";

        createRecordTable = ("CREATE TABLE " + DbConstants.TABLE_RECORD + " (") +
                DbConstants.RECORD_NODE_REF + " TEXT PRIMARY KEY, " +
                DbConstants.RECORD_PARENT_NODE_REF + " TEXT, " +
                DbConstants.RECORD_NAME + " TEXT, " +
                DbConstants.RECORD_DOC_TYPE + " TEXT, " +
                DbConstants.RECORD_FOLDER_CATEGORY + " TEXT, " +
                DbConstants.RECORD_FILE_CATEGORY + " TEXT, " +
                DbConstants.RECORD_FILE_SIZE + " INTEGER, " +
                DbConstants.RECORD_FILE_MIME_TYPE + " TEXT, " +
                DbConstants.RECORD_IS_FOLDER + " NUMERIC, " +
                DbConstants.RECORD_IS_SHARED + " NUMERIC, " +
                DbConstants.RECORD_IS_WRITABLE + " NUMERIC, " +
                DbConstants.RECORD_IS_DELETABLE + " NUMERIC, " +
                DbConstants.RECORD_OWNER + " TEXT, " +
                DbConstants.RECORD_CREATED_BY + " TEXT, " +
                DbConstants.RECORD_CREATION_DATE + " DATETIME, " +
                DbConstants.RECORD_UPDATED_BY + " TEXT, " +
                DbConstants.RECORD_UPDATE_DATE + " DATETIME );";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserTable);
        db.execSQL(createRecordTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_RECORD);
        this.onCreate(db);
    }
}
