package com.example.mybrowser.setting;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mybrowser.R;
import com.example.mybrowser.utils.Util;


public class SettingPresenter {
    private static final int NAME_CHANGED = 0;
    private static final int PROFILE_CHANGED = 1;
    private static final int CHECK_FALSE = 2;
    private static final int CHECK_SUCCESS = 3;
    private static final int DELETE_USER = 4;
    private static final int GET_PROFILE=5;

    private SettingModel mSettingModel;
    private SettingView mSettingView;
    private Boolean mCheckRes;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case NAME_CHANGED:
                    mSettingView.showChangeName();
                    break;
                case PROFILE_CHANGED:
                    mSettingView.showProfileAfterChange();
                    break;
                case CHECK_FALSE:
                    mSettingView.showCheckPasswordFalse();
                    break;
                case CHECK_SUCCESS:
                    mSettingView.showCheckPasswordSuccess();
                    break;
                case DELETE_USER:
                    mSettingView.showDeleteUser();
                    break;

            }
        }
    };

    public SettingPresenter(SettingView settingsView) {
        mSettingModel = new SettingModel();
        mSettingView = settingsView;
    }

    public void changeName(String newName) {
        new Thread(() -> {
            mSettingModel.changeName(newName);
            Message message = new Message();
            message.what = NAME_CHANGED;
            mHandler.sendMessage(message);
        }).start();
    }

    public void changeProfile(String profile) {
        new Thread(() -> {
            mSettingModel.setProfile(profile);
            Message message = new Message();
            message.what = PROFILE_CHANGED;
            mHandler.sendMessage(message);
        }).start();

    }

    public void changePassword(String password, String newPassword) {
        new Thread(() -> {
            mCheckRes = mSettingModel.checkPassword(password);
            Message message = new Message();
            if (mCheckRes) {
                mSettingModel.changePassword(newPassword);
                message.what = CHECK_SUCCESS;
            } else {
                message.what = CHECK_FALSE;
            }
            mHandler.sendMessage(message);
        }).start();
    }

    public void deleteUser(String password) {
        new Thread(() -> {
            mCheckRes = mSettingModel.checkPassword(password);
            Message message = new Message();
            if (mCheckRes) {
                mSettingModel.deleteUser();
                message.what = DELETE_USER;
            } else {
                message.what = CHECK_FALSE;
            }
            mHandler.sendMessage(message);
        }).start();
    }
    public void getProfile(){
        new Thread(() -> {
            mSettingModel.getProfile();
            Message message = new Message();
            message.what = GET_PROFILE;
            mHandler.sendMessage(message);

        }).start();

    }
}
