package com.example.mybrowser.history;

import com.example.mybrowser.bean.History;
import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.maintools.MyApplication;

import java.util.List;

public class HistoryModel {
    private DBController mDBController;

    public HistoryModel() {
        mDBController = DBController.getInstance(MyApplication.getContext());
    }

    public List<History> getAllHistory() {
        return mDBController.getAll();
    }

    public void deleteAll() {
        mDBController.deleteAllHistory();
    }

    public void deleteSelected(List<History> toBeDeleted) {
        mDBController.deleteSelectedHistory(toBeDeleted);
    }
}
