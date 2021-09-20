package com.example.mybrowser.register;

import com.example.mybrowser.bean.User;
import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.maintools.MyApplication;

public class RegisterModel {
    private DBController mDBController;
    private User mUser;

    public RegisterModel() {
        mUser = new User(null, "", "", "",null);
        mDBController = DBController.getInstance(MyApplication.getContext());
    }

    public boolean checkUserExist(String username) {
        return mDBController.checkUserExist(username);
    }

    public void insertUser(String username, String password) {
        mUser.setName(username);
        mUser.setPassword(password);
        mUser.setCustomizeName(username);
        mDBController.insertUser(mUser);
    }
}
