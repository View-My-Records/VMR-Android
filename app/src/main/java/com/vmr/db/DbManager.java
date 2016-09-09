package com.vmr.db;

import android.database.sqlite.SQLiteDatabase;

import com.vmr.db.record.Record;
import com.vmr.db.record.RecordDAO;
import com.vmr.db.shared.SharedRecord;
import com.vmr.db.shared.SharedRecordDAO;
import com.vmr.db.trash.TrashRecord;
import com.vmr.db.trash.TrashRecordDAO;
import com.vmr.db.user.User;
import com.vmr.db.user.UserDAO;
import com.vmr.model.UserInfo;

import java.util.List;

/*
 * Created by abhijit on 9/7/16.
 */

public class DbManager {

    private UserDAO userDAO;
    private RecordDAO recordDAO;
    private TrashRecordDAO trashRecordDAO;
    private SharedRecordDAO sharedRecordDAO;

    public DbManager() {
        DbHelper dbHelper = new DbHelper();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        userDAO = new UserDAO(database);
        recordDAO = new RecordDAO(database);
        trashRecordDAO = new TrashRecordDAO(database);
        sharedRecordDAO = new SharedRecordDAO(database);
    }

    // Adds new user to user table
    public Long addUser(UserInfo userInfo) {
        return this.userDAO.addUser(userInfo);
    }

    // get user for given serial no
    public User getUser(String serialNo) {
        return this.userDAO.getUser(serialNo);
    }

    // Update userdata and returns updated data
    public User updateUser(UserInfo userInfo) {
        return this.userDAO.updateUser(userInfo);
    }

    // delete user for given serial no
    public boolean deleteUser(String serialNo) {
        return this.userDAO.deleteUser(serialNo);
    }

    // Retrieve all records for given parent
    public List<Record> getAllRecords(String parentNode) {
        return this.recordDAO.getAllRecords(parentNode);
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
    public boolean deleteRecord(Record record) {
        return this.recordDAO.deleteRecord(record);
    }

    public List<SharedRecord> getAllSharedByMe(){
        return this.sharedRecordDAO.getAllRecords();
    }

    public void updateAllSharedByMe(List<SharedRecord> sharedRecords){
        this.sharedRecordDAO.updateAllRecords(sharedRecords);
    }

    public List<TrashRecord> getAllTrash(String nodeRef){
        return this.trashRecordDAO.getAllRecords(nodeRef);
    }

    public void updateAllTrash(List<TrashRecord> trashRecords,String nodeRef){
        this.trashRecordDAO.updateAllRecords(trashRecords);
    }
}
