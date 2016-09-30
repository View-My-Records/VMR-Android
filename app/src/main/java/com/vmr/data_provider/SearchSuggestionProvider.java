package com.vmr.data_provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.vmr.app.Vmr;
import com.vmr.db.search_suggestion.SearchSuggestion;

import java.util.List;

/*
 * Created by abhijit on 9/10/16.
 */

public class SearchSuggestionProvider extends ContentProvider {

    List<SearchSuggestion> searchResults;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        searchResults = Vmr.getDbManager().getSuggestions("");

        MatrixCursor matrixCursor = new MatrixCursor(
                new String[]{
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );
        if (searchResults != null) {
            String query = uri.getLastPathSegment();
            int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

            int length = searchResults.size();

            for (int i = 0; i < length && matrixCursor.getCount() < limit; i++) {
//                SearchSuggestion s = searchResults.get(i);
                String recipe = searchResults.get(i).getRecordName();
                if (recipe.toLowerCase().contains(query.toLowerCase())) {
                    matrixCursor.addRow(new Object[]{i, recipe , i});
                }
            }
        }
        return matrixCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
