package com.example.mybrowser.maintools;

import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.utils.Util;

public class MainModel {
    private DBController mDBController;
    private String username;

    public MainModel() {
        mDBController = DBController.getInstance(MyApplication.getContext());
        username = Util.getInstance().getUserName();
    }

    public void addCollection(String url, String title) {
        mDBController.addCollection(username, url, title);
    }
}