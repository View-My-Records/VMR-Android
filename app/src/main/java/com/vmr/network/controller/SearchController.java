package com.vmr.network.controller;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.app.Vmr;
import com.vmr.model.SearchResult;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.request.Constants;
import com.vmr.network.controller.request.SearchRequest;

import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 10/4/16.
 */

public class SearchController {

    private OnFetchResultsListener fetchResultsListener;

    public SearchController(OnFetchResultsListener fetchResultsListener) {
        this.fetchResultsListener = fetchResultsListener;
    }

    public void fetchResults(
            String queryTerm,
            String nodeRef,
            String contentType,
            boolean searchByPatternFlag ) {

        Map<String, String> formData = Vmr.getUserMap();
        formData.put(Constants.Request.FolderNavigation.Search.PAGE_MODE, Constants.Request.FolderNavigation.PageMode.SEARCH_FILE );
        formData.put(Constants.Request.FolderNavigation.Search.SEARCH_PATTERN, queryTerm );
        formData.put(Constants.Request.FolderNavigation.Search.SEARCH_BY_PATTERN, String.valueOf(searchByPatternFlag));
        formData.put(Constants.Request.FolderNavigation.Search.CONTENT_TYPE, contentType );
        formData.put(Constants.Request.FolderNavigation.Search.ALF_NODEREF, nodeRef );

        SearchRequest request =
                new SearchRequest(
                        formData,
                        new Response.Listener<List<SearchResult>>() {
                            @Override
                            public void onResponse(List<SearchResult> results) {
                                fetchResultsListener.onFetchResultsSuccess(results);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                fetchResultsListener.onFetchResultsFailure(error);
                            }
                        }
                );
        VolleySingleton.getInstance().addToRequestQueue(request, Constants.Request.FolderNavigation.Search.TAG);
    }

    public interface OnFetchResultsListener {
        void onFetchResultsSuccess(List<SearchResult> results);
        void onFetchResultsFailure(VolleyError error);
    }
}
