package com.vmr.db;

import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.Vmr;
import com.vmr.db.recently_accessed.Recent;
import com.vmr.db.recently_accessed.RecentDAO;
import com.vmr.db.record.Record;
import com.vmr.db.record.RecordDAO;
import com.vmr.db.search_suggestion.SearchSuggestion;
import com.vmr.db.search_suggestion.SearchSuggestionDAO;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.shared.SharedRecordDAO;
import com.vmr.db.trash.TrashRecord;
import com.vmr.db.trash.TrashRecordDAO;
import com.vmr.db.user.DbUser;
import com.vmr.db.user.UserDAO;
import com.vmr.model.UserInfo;
import com.vmr.model.VmrFolder;
import com.vmr.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Created by abhijit on 9/7/16.
 */

public class DbManager {

    private UserDAO userDAO;
    private RecordDAO recordDAO;
    private RecentDAO recentDAO;
    private TrashRecordDAO trashRecordDAO;
    private SharedRecordDAO sharedRecordDAO;
    private SearchSuggestionDAO searchSuggestionDAO;

    public DbManager() {
        DbHelper dbHelper = new DbHelper();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        userDAO = new UserDAO(database);
        recordDAO = new RecordDAO(database);
        recentDAO = new RecentDAO(database);
        trashRecordDAO = new TrashRecordDAO(database);
        sharedRecordDAO = new SharedRecordDAO(database);
        searchSuggestionDAO =  new SearchSuggestionDAO(database);
    }

    // Adds new user to user table
    public Long addUser(UserInfo userInfo) {
        return this.userDAO.addUser(userInfo);
    }

    // get user for given serial no
    public DbUser getUser(String serialNo) {
        return this.userDAO.getUser(serialNo);
    }

    public List<DbUser> getAllIndividualUsers() {
        return this.userDAO.getUsers(Constants.MembershipType.INDIVIDUAL);
    }

    public List<DbUser> getAllFamilyUsers() {
        return this.userDAO.getUsers(Constants.MembershipType.FAMILY);
    }

    public List<DbUser> getAllProfessionalUsers() {
        return this.userDAO.getUsers(Constants.MembershipType.PROFESSIONAL);
    }

    public List<DbUser> getAllCorporateUsers() {
        return this.userDAO.getUsers(Constants.MembershipType.CORPORATE);
    }

    // Update userdata and returns updated data
    public DbUser updateUser(UserInfo userInfo) {
        return this.userDAO.updateUser(userInfo);
    }

    // delete user for given serial no
    public boolean deleteUser(String serialNo) {
        return this.userDAO.deleteUser(serialNo);
    }

    // Retrieve all records for given parent
    public List<Record> getAllRecords(String parentNode) {
        return this.recordDAO.getAllRecords(parentNode, false);
    }

    public Long addRecord(Record record) {
        return this.recordDAO.addRecord(record);
    }

    public boolean updateTimestamp(String nodeRef) {
        return this.recordDAO.updateTimestamp(nodeRef);
    }

    public Record getRecord(String parentNode) {
        return this.recordDAO.getRecord(parentNode);
    }

    public boolean isRecordAvailableOffline(String nodeRef) {
        return this.recordDAO.isRecordAvailableOffline(nodeRef);
    }

    public boolean setRecordAvailableOffline(String nodeRef) {
        return this.recordDAO.setRecordAvailableOffline(nodeRef);
    }

    // Retrieve all records for given parent
    public List<Record> getFolders(String parentNode) {
        return this.recordDAO.getFolders(parentNode);
    }

    public void removeAllRecords(String parentNode) {
        this.recordDAO.removeAllRecords(parentNode);
    }

    public void removeAllRecords(String parentNode, VmrFolder vmrFolder) {
        if(vmrFolder.getAll().size() > 0){
            this.recordDAO.removeAllRecords(parentNode, vmrFolder);
        } else {
            this.recordDAO.removeAllRecords(parentNode);
        }
    }

    public List<Record> getAllRecords(String parentNode, boolean override) {
        return this.recordDAO.getAllRecords(parentNode, override);
    }

    // Retrieve all un-indexed records for given parent
    public List<Record> getAllUnIndexedRecords(String parentNode) {
        return this.recordDAO.getAllUnIndexedRecords(parentNode);
    }

    // Retrieve all un-indexed records for given parent
    public List<Record> getAllSharedWithMeRecords(String parentNode) {
        return this.recordDAO.getAllSharedWithMeRecords(parentNode);
    }

    // Update all records in the given list
    public void updateAllRecords(List<Record> records) {
        this.recordDAO.updateAllRecords(records);
    }

    // Delete given record
    public boolean moveRecordToTrash(Record record) {
        return this.recordDAO.deleteRecord(record);
    }

    public boolean deleteRecordFromTrash(TrashRecord record) {
        return this.trashRecordDAO.deleteRecord(record);
    }

    public List<SharedRecord> getAllSharedByMe(){
        return this.sharedRecordDAO.getAllRecords();
    }

    public void updateAllSharedByMe(List<SharedRecord> sharedRecords){
        this.sharedRecordDAO.updateAllRecords(sharedRecords);
    }

    public List<TrashRecord> getAllTrash(){
        return this.trashRecordDAO.getAllRecords();
    }

    public void updateAllTrash(List<TrashRecord> trashRecords){
        this.trashRecordDAO.updateAllRecords(trashRecords);
    }

    public List<Recent> getAllRecentlyAccsseed(){
        return this.recentDAO.getAllRecents();
    }

    public void addNewRecent(Record record){

        Recent recent = new Recent();
        recent.setNodeRef(record.getNodeRef());
        recent.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
        recent.setName(record.getRecordName());
        recent.setLocation(DbConstants.TABLE_RECORD);
        recent.setLastAccess(new Date());

        if(this.recentDAO.checkRecord(record.getNodeRef())){
            this.recentDAO.updateRecord(recent);
        } else {
            this.recentDAO.addRecent(recent);
        }
    }

    public void addNewRecent(SharedRecord record){

        Recent recent = new Recent();
        recent.setNodeRef(record.getNodeRef());
        recent.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
        recent.setName(record.getRecordName());
        recent.setLocation(DbConstants.TABLE_SHARED);
        recent.setLastAccess(new Date());

        if(this.recentDAO.checkRecord(record.getNodeRef())){
            this.recentDAO.updateRecord(recent);
        } else {
            this.recentDAO.addRecent(recent);
        }
    }

    public void addNewRecent(TrashRecord record){

        Recent recent = new Recent();
        recent.setNodeRef(record.getNodeRef());
        recent.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
        recent.setName(record.getRecordName());
        recent.setLocation(DbConstants.TABLE_TRASH);
        recent.setLastAccess(new Date());

        if(this.recentDAO.checkRecord(record.getNodeRef())) {
            this.recentDAO.updateRecord(recent);
        } else {
            this.recentDAO.addRecent(recent);
        }
    }

    public void deleteRecent(Recent recent){
        this.recentDAO.deleteRecord(recent);
    }

    public void deleteRecent(Record record){

        Recent recent = new Recent();
        recent.setNodeRef(record.getNodeRef());
        recent.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
        recent.setName(record.getRecordName());
        recent.setLocation(DbConstants.TABLE_RECORD);
        recent.setLastAccess(new Date());

        this.recentDAO.deleteRecord(recent);
    }

    public void deleteRecent(SharedRecord sharedRecord){

        Recent recent = new Recent();
        recent.setNodeRef(sharedRecord.getNodeRef());
        recent.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
        recent.setName(sharedRecord.getRecordName());
        recent.setLocation(DbConstants.TABLE_SHARED);
        recent.setLastAccess(new Date());

        this.recentDAO.deleteRecord(recent);
    }

    public void deleteRecent(TrashRecord trashRecord){

        Recent recent = new Recent();
        recent.setNodeRef(trashRecord.getNodeRef());
        recent.setMasterRecordOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
        recent.setName(trashRecord.getRecordName());
        recent.setLocation(DbConstants.TABLE_TRASH);
        recent.setLastAccess(new Date());

        this.recentDAO.deleteRecord(recent);
    }

    public List<SearchSuggestion> getSuggestions(String searchTerm){
        List<SearchSuggestion> suggestionList =  new ArrayList<>();
        suggestionList.addAll(this.getSuggestionsFromMyRecords(searchTerm));
        suggestionList.addAll(this.getSuggestionsFromTrash(searchTerm));
        suggestionList.addAll(this.getSuggestionsFromShared(searchTerm));
        return suggestionList;
    }

    private List<SearchSuggestion> getSuggestionsFromMyRecords(String searchTerm){
       return this.searchSuggestionDAO.getSuggestionsFromMyRecords(searchTerm);
    }

    private List<SearchSuggestion> getSuggestionsFromTrash(String searchTerm){
        return this.searchSuggestionDAO.getSuggestionsFromTrash(searchTerm);
    }

    private List<SearchSuggestion> getSuggestionsFromShared(String searchTerm){
        return this.searchSuggestionDAO.getSuggestionsFromShared(searchTerm);
    }
}
