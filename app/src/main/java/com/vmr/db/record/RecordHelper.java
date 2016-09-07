package com.vmr.db.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmr.db.DbConstants;

/*
 * Created by abhijit on 9/5/16.
 */
public class RecordHelper extends SQLiteOpenHelper {

    public RecordHelper(Context context) {
        super(context, DbConstants.DATABASE_NAME, null, DbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE "                     + DbConstants.TABLE_RECORD + " (");
        stringBuilder.append( DbConstants.RECORD_NODE_REF        + " TEXT PRIMARY KEY, ");
        stringBuilder.append( DbConstants.RECORD_PARENT_NODE_REF + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_NAME            + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_DOC_TYPE        + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_FOLDER_CATEGORY + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_FILE_CATEGORY   + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_FILE_SIZE       + " INTEGER, ");
        stringBuilder.append( DbConstants.RECORD_FILE_MIME_TYPE  + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_IS_FOLDER       + " NUMERIC, ");
        stringBuilder.append( DbConstants.RECORD_IS_SHARED       + " NUMERIC, ");
        stringBuilder.append( DbConstants.RECORD_IS_WRITABLE     + " NUMERIC, ");
        stringBuilder.append( DbConstants.RECORD_IS_DELETABLE    + " NUMERIC, ");
        stringBuilder.append( DbConstants.RECORD_OWNER           + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_CREATED_BY      + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_CREATION_DATE   + " DATETIME, ");
        stringBuilder.append( DbConstants.RECORD_UPDATED_BY      + " TEXT, ");
        stringBuilder.append( DbConstants.RECORD_UPDATE_DATE     + " DATETIME );");
        try {
            sqLiteDatabase.execSQL(stringBuilder.toString());
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_RECORD);
        this.onCreate(sqLiteDatabase);
    }
}
