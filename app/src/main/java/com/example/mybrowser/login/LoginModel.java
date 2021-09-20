package com.example.mybrowser.login;

import com.example.mybrowser.bean.User;
import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.maintools.MyApplication;

public class LoginModel {
    private DBController mDbController;
    private User mUser;

    public LoginModel() {
        mUser = new User(null, "", "", "",null);
        mDbController = DBController.getInstance(MyApplication.getContext());
    }

    public User checkUserAndPassword () {
        return mDbController.checkUserAndPassword(mUser.getName(), mUser.getPassword());
    }

    public void setUser(String username, String password) {
        mUser.setName(username);
        mUser.setPassword(password);
        mUser.setCustomizeName(username);
    }
}