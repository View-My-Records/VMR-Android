package com.vmr.db.shared_by_me;

/*
 * Created by abhijit on 9/8/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmr.db.DbConstants;

public class SharedRecordHelper extends SQLiteOpenHelper {

    public SharedRecordHelper(Context context) {
        super(context, DbConstants.DATABASE_NAME, null, DbConstants.VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE "                     + DbConstants.TABLE_SHARED + " (");
        stringBuilder.append( DbConstants.SHARED_NODE_REF        + " TEXT PRIMARY KEY, ");
        stringBuilder.append( DbConstants.SHARED_PARENT_NODE_REF + " TEXT, ");
        stringBuilder.append( DbConstants.SHARED_IS_FOLDER       + " NUMERIC, ");
        stringBuilder.append( DbConstants.SHARED_TO_EMAIL_ID     + " TEXT, ");
        stringBuilder.append( DbConstants.SHARED_USER_ID         + " TEXT, ");
        stringBuilder.append( DbConstants.SHARED_FILE_NAME       + " TEXT, ");
        stringBuilder.append( DbConstants.SHARED_PERMISSIONS     + " TEXT, ");
        stringBuilder.append( DbConstants.SHARED_OWNER_NAME      + " TEXT, ");
        stringBuilder.append( DbConstants.SHARED_RECORD_LIFE     + " DATETIME );");
        try {
            db.execSQL(stringBuilder.toString());
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_SHARED);
    }
}
