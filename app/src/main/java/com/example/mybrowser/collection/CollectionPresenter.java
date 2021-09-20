package com.example.mybrowser.collection;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.mybrowser.bean.Collection;

import java.util.List;

public class CollectionPresenter {
    private static final int SHOW_ALL_COLLECTION = 0;

    private CollectionModel mCollectionModel;
    private CollectionView mCollectionView;
    private List<Collection> mCollections;
    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SHOW_ALL_COLLECTION:
                    mCollectionView.showAllCollection(mCollections);
                    break;
            }
        }
    };

    public CollectionPresenter(CollectionView collectionView) {
        mCollectionModel = new CollectionModel();
        mCollectionView = collectionView;
    }

    public void getCollections() {
        new Thread(() -> {
            mCollections = mCollectionModel.getCollection();
            Message message = new Message();
            message.what = SHOW_ALL_COLLECTION;
            mHandler.sendMessage(message);
        }).start();
    }

    public void deleteAllCollection() {
        new Thread(() -> mCollectionModel.deleteAllUserCollection()).start();
    }

    public void deleteSelectedCollection(List<Collection> toBeDeleted) {
        new Thread(() -> mCollectionModel.deleteSelectedUserCollection(toBeDeleted)).start();
    }
}
