package com.example.mybrowser.maintools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.mybrowser.utils.Util;

public class MainPresenter {
    private static final int ADD_COLLECTION = 0;

    private MainModel mMainModel;
    private MainView mMainView;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ADD_COLLECTION:
                    mMainView.showAddCollection();
                    break;
            }
        }
    };

    public MainPresenter(MainView mainView) {
        mMainModel = new MainModel();
        mMainView = mainView;
    }


    public void addCollection(String url, String title) {
        new Thread(() -> {
            mMainModel.addCollection(url, title);
            Message message = new Message();
            message.what = ADD_COLLECTION;
            mHandler.sendMessage(message);
        }).start();
    }
}