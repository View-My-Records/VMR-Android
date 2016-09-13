package com.vmr.home;

import android.content.SearchRecentSuggestionsProvider;

/*
 * Created by abhijit on 9/11/16.
 */

public class SearchHistoryProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.vmr.home.SearchHistoryProvider";
    public final static int MODE = DATABASE_MODE_QUERIES ;

    public SearchHistoryProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
