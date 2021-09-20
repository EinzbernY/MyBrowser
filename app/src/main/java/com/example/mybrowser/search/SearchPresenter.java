package com.example.mybrowser.search;


import com.example.mybrowser.bean.History;

import java.util.List;

public class SearchPresenter {
    private SearchModel mSearchModel;

    public SearchPresenter() {
        mSearchModel = new SearchModel();
    }

    public void insertHistories(List<History> histories) {
        new Thread(() -> mSearchModel.insertHistory(histories)).start();
    }
}


