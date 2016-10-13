package com.vmr.db.search_suggestion;

/*
 * Created by abhijit on 9/9/16.
 */

import com.vmr.db.DbConstants;
import com.vmr.db.record.Record;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.trash.TrashRecord;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestion {

    public static final String RECORD_NAME = "RECORD_NAME";
    public static final String RECORD_NODE = "RECORD_NODE";


    private String recordName;
    private boolean isFolder;
    private String recordNodeRef;
    private String recordParentNodeRef;
    private String recordLocation;

    public SearchSuggestion() {

    }

    public static List<SearchSuggestion> getSuggestionListFromRecords(List<Record> records){
        List<SearchSuggestion> searchSuggestions = new ArrayList<>();
        SearchSuggestion searchSuggestion;
        for (Record record : records) {
            searchSuggestion =  new SearchSuggestion();
            searchSuggestion.setRecordName(record.getRecordName());
            searchSuggestion.setIsFolder(record.isFolder());
            searchSuggestion.setRecordNodeRef(record.getNodeRef());
            searchSuggestion.setRecordParentNodeRef(record.getParentNodeRef());
            searchSuggestion.setRecordLocation(DbConstants.TABLE_RECORD);
            searchSuggestions.add(searchSuggestion);
        }
        return searchSuggestions;
    }

    public static List<SearchSuggestion> getSuggestionListFromTrash(List<TrashRecord> records){
        List<SearchSuggestion> searchSuggestions = new ArrayList<>();
        SearchSuggestion searchSuggestion;
        for (TrashRecord record : records) {
            searchSuggestion =  new SearchSuggestion();
            searchSuggestion.setRecordName(record.getRecordName());
            searchSuggestion.setIsFolder(record.isFolder());
            searchSuggestion.setRecordNodeRef(record.getNodeRef());
            searchSuggestion.setRecordParentNodeRef(record.getParentNodeRef());
            searchSuggestion.setRecordLocation(DbConstants.TABLE_TRASH);
            searchSuggestions.add(searchSuggestion);
        }
        return searchSuggestions;
    }

    public static List<SearchSuggestion> getSuggestionListFromShared(List<SharedRecord> records){
        List<SearchSuggestion> searchSuggestions = new ArrayList<>();
        SearchSuggestion searchSuggestion;
        for (SharedRecord record : records) {
            searchSuggestion =  new SearchSuggestion();
            searchSuggestion.setRecordName(record.getRecordName());
            searchSuggestion.setIsFolder(record.isFolder());
            searchSuggestion.setRecordNodeRef(record.getNodeRef());
            searchSuggestion.setRecordParentNodeRef(null);
            searchSuggestion.setRecordLocation(DbConstants.TABLE_SHARED);
            searchSuggestions.add(searchSuggestion);
        }
        return searchSuggestions;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean folder) {
        isFolder = folder;
    }

    public String getRecordNodeRef() {
        return recordNodeRef;
    }

    public void setRecordNodeRef(String recordNodeRef) {
        this.recordNodeRef = recordNodeRef;
    }

    public String getRecordParentNodeRef() {
        return recordParentNodeRef;
    }

    public void setRecordParentNodeRef(String recordParentNodeRef) {
        this.recordParentNodeRef = recordParentNodeRef;
    }

    public String getRecordLocation() {
        return recordLocation;
    }

    public void setRecordLocation(String recordLocation) {
        this.recordLocation = recordLocation;
    }
}
