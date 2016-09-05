package com.vmr.db.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Created by abhijit on 9/5/16.
 */
public class RecordHelper extends SQLiteOpenHelper {

    //DB Constants
    public static final String DATABASE_NAME = "my_records.db";
    public static final int VERSION = 1;

    //Table Name
    public static final String TABLE_NAME = "MY_RECORDS";

    //Column Names
    public static final String COL_RECORD_ID = "ITEM_ID ";
    public static final String COL_ITEM_NODE_REF        = "ITEM_NODE_REF";
    public static final String COL_ITEM_PARENT_NODE_REF = "ITEM_PARENT_NOD";
    public static final String COL_ITEM_NAME            = "ITEM_NAME";
    public static final String COL_ITEM_DOC_TYPE        = "ITEM_DOC_TYPE ";
    public static final String COL_FOLDER_CATEGORY      = "FOLDER_CATEGORY";
    public static final String COL_FILE_CATEGORY        = "FILE_CATEGORY ";
    public static final String COL_FILE_SIZE            = "FILE_SIZE";
    public static final String COL_FILE_MIME_           = "FILE_MIME_";
    public static final String COL_IS_FOLDER            = "IS_FOLDER";
    public static final String COL_IS_SHARED            = "IS_SHARED ";
    public static final String COL_WRITE                = "WRITE ";
    public static final String COL_DELETE               = "DELETE ";
    public static final String COL_ITEM_OWNER           = "ITEM_OWNER ";
    public static final String COL_CREATED_BY           = "CREATED_BY ";
    public static final String COL_CREATED_DATE         = "CREATED_DATE ";
    public static final String COL_UPDATED_BY           = "UPDATED_BY";
    public static final String COL_UPDATED_DATE         = "UPDATED_DATE ";

    public RecordHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE " + TABLE_NAME + " (");
        stringBuilder.append( COL_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        stringBuilder.append( COL_ITEM_NODE_REF        + " TEXT, ");
        stringBuilder.append( COL_ITEM_PARENT_NODE_REF + " TEXT, ");
        stringBuilder.append( COL_ITEM_NAME            + " TEXT, ");
        stringBuilder.append( COL_ITEM_DOC_TYPE        + " TEXT, ");
        stringBuilder.append( COL_FOLDER_CATEGORY      + " TEXT, ");
        stringBuilder.append( COL_FILE_CATEGORY        + " TEXT, ");
        stringBuilder.append( COL_FILE_SIZE            + " INTEGER, ");
        stringBuilder.append( COL_FILE_MIME_           + " TEXT, ");
        stringBuilder.append( COL_IS_FOLDER            + " NUMERIC, ");
        stringBuilder.append( COL_IS_SHARED            + " NUMERIC, ");
        stringBuilder.append( COL_WRITE                + " NUMERIC, ");
        stringBuilder.append( COL_DELETE               + " NUMERIC, ");
        stringBuilder.append( COL_ITEM_OWNER           + " TEXT, ");
        stringBuilder.append( COL_CREATED_BY           + " TEXT, ");
        stringBuilder.append( COL_CREATED_DATE         + " DATETIME, ");
        stringBuilder.append( COL_UPDATED_BY           + " TEXT, ");
        stringBuilder.append( COL_UPDATED_DATE         + " DATETIME );");
        try {
            sqLiteDatabase.execSQL(stringBuilder.toString());
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }
}
