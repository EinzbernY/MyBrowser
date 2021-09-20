package com.example.mybrowser.home;
import com.example.mybrowser.bean.Quick;

import java.util.List;

public interface QuickView {
    void insertSuccess();

    void insertFail();

    void showAll(List<Quick> quicks);
}