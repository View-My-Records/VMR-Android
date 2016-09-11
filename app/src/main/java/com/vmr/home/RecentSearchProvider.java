package com.vmr.home;

import android.content.SearchRecentSuggestionsProvider;

/*
 * Created by abhijit on 9/11/16.
 */

public class RecentSearchProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.vmr.home.RecentSearchProvider";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public RecentSearchProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
