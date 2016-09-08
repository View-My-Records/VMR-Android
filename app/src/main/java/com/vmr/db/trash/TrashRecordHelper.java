package com.vmr.db.trash;

/*
 * Created by abhijit on 9/8/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmr.db.DbConstants;

public class TrashRecordHelper extends SQLiteOpenHelper {

    public TrashRecordHelper(Context context) {
        super(context, DbConstants.DATABASE_NAME, null, DbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE "                    + DbConstants.TABLE_TRASH + " (");
        stringBuilder.append( DbConstants.TRASH_NODE_REF        + " TEXT PRIMARY KEY, ");
        stringBuilder.append( DbConstants.TRASH_PARENT_NODE_REF + " TEXT, ");
        stringBuilder.append( DbConstants.TRASH_IS_FOLDER       + " NUMERIC, ");
        stringBuilder.append( DbConstants.TRASH_CREATED_BY      + " TEXT, ");
        stringBuilder.append( DbConstants.TRASH_NAME            + " TEXT, ");
        stringBuilder.append( DbConstants.TRASH_OWNER           + " TEXT );");
        try {
            db.execSQL(stringBuilder.toString());
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_TRASH);
    }
}
