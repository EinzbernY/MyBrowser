package com.example.mybrowser.search;
import com.example.mybrowser.bean.Collection;
import com.example.mybrowser.bean.History;
import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.maintools.MyApplication;
import com.example.mybrowser.utils.Util;

import java.util.List;

public class SearchModel {
    private DBController mDBController;
    private String mUsername;

    public SearchModel() {
        mDBController = DBController.getInstance(MyApplication.getContext());
        mUsername = Util.getInstance().getUserName();
    }

    public void insertHistory(List<History> histories) {
        mDBController.insertHistory(histories);
    }
}
