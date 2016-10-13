package com.vmr.data_provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.search_suggestion.SearchSuggestion;

import java.util.List;

/*
 * Created by abhijit on 9/10/16.
 */

public class SearchSuggestionProvider extends ContentProvider {

    int NUMBER_OF_SEARCH_SUGGESTIONS = 5;

    List<SearchSuggestion> searchResults;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

            searchResults = Vmr.getDbManager().getSuggestions("");

            MatrixCursor matrixCursor = new MatrixCursor(
                    new String[]{
                            BaseColumns._ID,                            // id
                            SearchManager.SUGGEST_COLUMN_ICON_1,        // icon
                            SearchManager.SUGGEST_COLUMN_TEXT_1,        // record name to be shown
                            SearchManager.SUGGEST_COLUMN_ICON_2,
                            SearchManager.SUGGEST_COLUMN_TEXT_2,        // location of file/folder
                            SearchManager.SUGGEST_COLUMN_INTENT_ACTION, // Action Search for folder / view for file
                            SearchManager.SUGGEST_COLUMN_INTENT_DATA    // node ref of file/folder
                    }
            );
            if (searchResults != null) {
                String query = uri.getLastPathSegment();
            int limit = NUMBER_OF_SEARCH_SUGGESTIONS;

            int length = searchResults.size();

            for (int id = 0; id < length && matrixCursor.getCount() < limit; id++) {
                SearchSuggestion s = searchResults.get(id);
                String recordName = s.getRecordName();

                String intentDate = s.getRecordLocation() + "#" + s.getRecordNodeRef() + "#" + s.getRecordName() + "#" + s.isFolder();

                if (recordName.toLowerCase().contains(query.toLowerCase())) {
                    if (s.isFolder()) {
                        matrixCursor.addRow(
                            new Object[]{
                                    id,                     // id
                                    R.drawable.ic_folder,   // icon
                                    s.getRecordName(),      // record name to be shown
                                    null,
                                    s.getRecordLocation(),  // location of file/folder
                                    Intent.ACTION_SEARCH,
                                    intentDate    // node ref of file/folder
                            });
                    } else {
                        matrixCursor.addRow(
                            new Object[]{
                                    id,                     // id
                                    R.drawable.ic_file,     // icon
                                    s.getRecordName(),      // record name to be shown
                                    null,
                                    s.getRecordLocation(),  // location of file/folder
                                    Intent.ACTION_VIEW,
                                    intentDate              // intentData
                            });
                    }
                }
            }
        }

        if (matrixCursor.getCount() == 0) {
            matrixCursor.addRow(
                    new Object[]{
                            null,
                            android.R.drawable.stat_notify_error,
                            "No records found",
                            0,
                            null,
                            null,
                            null
                    });
        }

        return matrixCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
