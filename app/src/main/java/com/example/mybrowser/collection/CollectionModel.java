package com.example.mybrowser.collection;

import com.example.mybrowser.bean.Collection;
import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.maintools.MyApplication;
import com.example.mybrowser.utils.Util;

import java.util.List;

public class CollectionModel {
    private DBController mDBController;
    private String mUsername;

    public CollectionModel() {
        mDBController = DBController.getInstance(MyApplication.getContext());
        mUsername = Util.getInstance().getUserName();
    }

    public List<Collection> getCollection() {
        if (mUsername != null) {
            return mDBController.getUserCollection(mUsername);
        }
        return null;
    }

    public void deleteAllUserCollection() {
        mDBController.deleteAllCollection(mUsername);
    }

    public void deleteSelectedUserCollection(List<Collection> toBeDeleted) {
        mDBController.deleteSelectedCollection(toBeDeleted);
    }
}
