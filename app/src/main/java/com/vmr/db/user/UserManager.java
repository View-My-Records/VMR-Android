package com.vmr.db.user;

import android.database.sqlite.SQLiteDatabase;

import com.vmr.app.VMR;
import com.vmr.model.UserInfo;

import java.util.List;

/*
 * Created by abhijit on 9/6/16.
 */
public class UserManager {

    private UserDAO userDAO;

    public UserManager() {
        UserHelper userHelper = new UserHelper(VMR.getVMRContext());
        SQLiteDatabase database = userHelper.getWritableDatabase();
        userDAO = new UserDAO(database);
    }

    // Adds new user to user table
    public Long addUser(UserInfo userInfo) {
        return this.userDAO.addUser(userInfo);
    }

    // get user for given serial no
    public User getUser(String serialNo) {
        return this.userDAO.getUser(serialNo);
    }

    // Updated userdata and returns updated data
    public User updateUser(UserInfo userInfo) {
        return this.userDAO.updateUser(userInfo);
    }

    // delete user for given serial no
    public boolean deleteUser(String serialNo) {
        return this.userDAO.deleteUser(serialNo);
    }
}
