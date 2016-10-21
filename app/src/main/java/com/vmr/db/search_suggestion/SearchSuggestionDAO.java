package com.vmr.db.search_suggestion;

/*
 * Created by abhijit on 9/8/16.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.Vmr;
import com.vmr.db.DbConstants;
import com.vmr.debug.VmrDebug;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionDAO {

    private SQLiteDatabase db;

    public SearchSuggestionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Fetch all records in db for current user
    public List<SearchSuggestion> getSuggestionsFromMyRecords(String searchTerm){
        List<SearchSuggestion> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_RECORD, // Table Name
                new String[] { DbConstants.RECORD_NAME, DbConstants.RECORD_IS_FOLDER, DbConstants.RECORD_NODE_REF, DbConstants.RECORD_PARENT_NODE_REF }, // Select columns
                DbConstants.RECORD_MASTER_OWNER + "=? AND " + DbConstants.RECORD_IS_FOLDER + "=? AND " + DbConstants.RECORD_NAME + " LIKE ? " , // where
                new String[] { Vmr.getLoggedInUserInfo().getLoggedinUserId(), "0", "%"+searchTerm+"%" } , // conditions
                null, // group by
                null, // having
                DbConstants.RECORD_NAME, // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Suggestion retrieved from Records");
            do {
                SearchSuggestion searchSuggestion = new SearchSuggestion();
                searchSuggestion.setRecordLocation("records");
                searchSuggestion.setRecordName(          c.getString( c.getColumnIndex(DbConstants.RECORD_NAME)));
                searchSuggestion.setRecordNodeRef(       c.getString( c.getColumnIndex(DbConstants.RECORD_NODE_REF)));
                searchSuggestion.setRecordParentNodeRef( c.getString( c.getColumnIndex(DbConstants.RECORD_PARENT_NODE_REF)));
                searchSuggestion.setIsFolder(            c.getInt(    c.getColumnIndex(DbConstants.RECORD_IS_FOLDER)) > 0);
                records.add(searchSuggestion);
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No suggestion found in Records");
        }
        c.close();

        return records;
    }

    // Fetch all records in db for current user
    public List<SearchSuggestion> getSuggestionsFromTrash(String searchTerm){
        List<SearchSuggestion> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_TRASH, // Table Name
                new String[] { DbConstants.TRASH_NAME, DbConstants.TRASH_IS_FOLDER, DbConstants.TRASH_NODE_REF, DbConstants.TRASH_PARENT_NODE_REF }, // Select columns
                DbConstants.TRASH_MASTER_OWNER + "=? AND " + DbConstants.TRASH_IS_FOLDER + "=? AND " + DbConstants.TRASH_NAME + " LIKE ? " , // where
                new String[] { Vmr.getLoggedInUserInfo().getLoggedinUserId(), "0" ,"%"+searchTerm+"%" } , // conditions
                null, // group by
                null, // having
                DbConstants.TRASH_NAME, // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Suggestion retrieved from Trash");
            do {
                SearchSuggestion searchSuggestion = new SearchSuggestion();
                searchSuggestion.setRecordLocation("trash");
                searchSuggestion.setRecordName(          c.getString( c.getColumnIndex(DbConstants.TRASH_NAME)));
                searchSuggestion.setRecordNodeRef(       c.getString( c.getColumnIndex(DbConstants.TRASH_NODE_REF)));
                searchSuggestion.setRecordParentNodeRef( c.getString( c.getColumnIndex(DbConstants.TRASH_PARENT_NODE_REF)));
                searchSuggestion.setIsFolder(            c.getInt(    c.getColumnIndex(DbConstants.TRASH_IS_FOLDER)) > 0);
                records.add(searchSuggestion);
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No suggestion found in Trash");
        }
        c.close();

        return records;
    }

    // Fetch all records in db for current user
    public List<SearchSuggestion> getSuggestionsFromShared(String searchTerm){
        List<SearchSuggestion> records = new ArrayList<>();
        Cursor c = db.query(
                DbConstants.TABLE_SHARED, // Table Name
                new String[] { DbConstants.SHARED_FILE_NAME, DbConstants.SHARED_IS_FOLDER, DbConstants.SHARED_NODE_REF, DbConstants.SHARED_PARENT_NODE_REF }, // Select columns
                DbConstants.SHARED_MASTER_OWNER + "=? AND " + DbConstants.SHARED_IS_FOLDER + "=? AND " + DbConstants.SHARED_FILE_NAME + " LIKE ? " , // where
                new String[] { Vmr.getLoggedInUserInfo().getLoggedinUserId(), "0" , "%"+searchTerm+"%" } , // conditions
                null, // group by
                null, // having
                DbConstants.SHARED_FILE_NAME, // order by
                null );

        if (c.moveToFirst()) {
            VmrDebug.printLogI(this.getClass(), "Suggestion retrieved from Shared");
            do {
                SearchSuggestion searchSuggestion = new SearchSuggestion();
                searchSuggestion.setRecordLocation("shared");
                searchSuggestion.setRecordName(          c.getString( c.getColumnIndex(DbConstants.SHARED_FILE_NAME)));
                searchSuggestion.setRecordNodeRef(       c.getString( c.getColumnIndex(DbConstants.SHARED_NODE_REF)));
                searchSuggestion.setRecordParentNodeRef( c.getString( c.getColumnIndex(DbConstants.SHARED_PARENT_NODE_REF)));
                searchSuggestion.setIsFolder(            c.getInt(    c.getColumnIndex(DbConstants.SHARED_IS_FOLDER)) > 0);
                records.add(searchSuggestion);
            } while (c.moveToNext());
        } else {
            VmrDebug.printLogI(this.getClass(), "No suggestion found in Shared");
        }
        c.close();

        return records;
    }
}
