package com.vmr.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmr.app.Vmr;

/*
 * Created by abhijit on 9/7/16.
 */

class DbHelper extends SQLiteOpenHelper {

    private static final String createUserTable
            = ("CREATE TABLE " + DbConstants.TABLE_USER + " (") +
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

    private static final String createRecordTable
            = ("CREATE TABLE " + DbConstants.TABLE_RECORD + " (") +
            DbConstants.RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DbConstants.RECORD_MASTER_OWNER + " TEXT, " +
            DbConstants.RECORD_NODE_REF + " TEXT, " +
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
            DbConstants.RECORD_UPDATE_DATE + " DATETIME, " +
            DbConstants.RECORD_LAST_UPDATE_TIMESTAMP + " DATETIME );";

    private static final String createSharedTable
            = ("CREATE TABLE " + DbConstants.TABLE_SHARED + " (") +
            DbConstants.SHARED_RECORD_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DbConstants.SHARED_NODE_REF        + " TEXT, " +
            DbConstants.SHARED_PARENT_NODE_REF + " TEXT, " +
            DbConstants.SHARED_IS_FOLDER       + " NUMERIC, " +
            DbConstants.SHARED_TO_EMAIL_ID     + " TEXT, " +
            DbConstants.SHARED_USER_ID         + " TEXT, " +
            DbConstants.SHARED_FILE_NAME       + " TEXT, " +
            DbConstants.SHARED_PERMISSIONS     + " TEXT, " +
            DbConstants.SHARED_OWNER_NAME      + " TEXT, " +
            DbConstants.SHARED_RECORD_LIFE     + " DATETIME );" ;

    private static final String createTrashTable
            = "CREATE TABLE "        + DbConstants.TABLE_TRASH + " (" +
            DbConstants.SHARED_RECORD_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DbConstants.TRASH_NODE_REF        + " TEXT, " +
            DbConstants.TRASH_PARENT_NODE_REF + " TEXT, " +
            DbConstants.TRASH_IS_FOLDER       + " NUMERIC, " +
            DbConstants.TRASH_CREATED_BY      + " TEXT, " +
            DbConstants.TRASH_NAME            + " TEXT, " +
            DbConstants.TRASH_OWNER           + " TEXT );" ;

    DbHelper() {
        super(Vmr.getVMRContext(), DbConstants.DATABASE_NAME, null, DbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserTable);
        db.execSQL(createRecordTable);
        db.execSQL(createSharedTable);
        db.execSQL(createTrashTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_SHARED);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_TRASH);
        this.onCreate(db);
    }
}
