package com.example.mybrowser.login;

import com.example.mybrowser.bean.User;

public interface LoginView {
    void showLoginSuccessfully(User user);
    void showLoginFailed();
}
