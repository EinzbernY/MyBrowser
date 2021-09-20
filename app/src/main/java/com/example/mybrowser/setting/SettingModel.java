package com.example.mybrowser.setting;
import com.example.mybrowser.dao.DBController;
import com.example.mybrowser.maintools.MyApplication;
import com.example.mybrowser.utils.Util;

public class SettingModel {
    private DBController mDBController;
    private String mUsername;

    public SettingModel() {
        mDBController = new DBController(MyApplication.getContext());
        mUsername = Util.getInstance().getUserName();
    }

    public void changeName(String newName) {
        mDBController.changeUsername(mUsername, newName);
    }

    public void setProfile(String profile) {
        mDBController.setProfile(mUsername, profile);
    }
    public String getProfile(){
        return mDBController.getProfile(mUsername);
    }

    public boolean checkPassword(String password) {
        return mDBController.checkUserAndPassword(mUsername, password) != null;
    }

    public void changePassword(String newPassword) {
        mDBController.changeUsername(mUsername, newPassword);
    }

    public void deleteUser() {
        mDBController.deleteUser(mUsername);
    }
}
