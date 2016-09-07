package com.vmr.db.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmr.db.DbConstants;

/*
 * Created by abhijit on 9/6/16.
 */
public class UserHelper extends SQLiteOpenHelper {



    public UserHelper(Context context) {
        super(context, DbConstants.DATABASE_NAME, null, DbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE "    + DbConstants.TABLE_USER + " (");
        stringBuilder.append( DbConstants.USER_SERIAL_NO         + " TEXT PRIMARY KEY, ");
        stringBuilder.append( DbConstants.USER_RESULT            + " TEXT, ");
        stringBuilder.append( DbConstants.USER_ROOT_NODE_REF     + " TEXT, ");
        stringBuilder.append( DbConstants.USER_URL_TYPE          + " TEXT, ");
        stringBuilder.append( DbConstants.USER_TYPE + " TEXT, ");
        stringBuilder.append( DbConstants.USER_MEMBERSHIP_TYPE   + " TEXT, ");
        stringBuilder.append( DbConstants.USER_CORP_NAME         + " TEXT, ");
        stringBuilder.append( DbConstants.USER_EMAIL_ID          + " TEXT, ");
        stringBuilder.append( DbConstants.USER_EMP_TYPE          + " TEXT, ");
        stringBuilder.append( DbConstants.USER_ID + " TEXT, ");
        stringBuilder.append( DbConstants.USER_SESSION_ID        + " TEXT, ");
        stringBuilder.append( DbConstants.USER_USER_NAME + " TEXT, ");
        stringBuilder.append( DbConstants.USER_CORP_ID           + " TEXT, ");
        stringBuilder.append( DbConstants.USER_LOGGED_IN_USER_ID + " TEXT, ");
        stringBuilder.append( DbConstants.USER_LAST_NAME         + " TEXT, ");
        stringBuilder.append( DbConstants.USER_FIRST_NAME        + " TEXT, ");
        stringBuilder.append( DbConstants.USER_LAST_LOGIN        + " DATETIME );");
        try {
            sqLiteDatabase.execSQL(stringBuilder.toString());
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_USER );
        this.onCreate(sqLiteDatabase);
    }
}
