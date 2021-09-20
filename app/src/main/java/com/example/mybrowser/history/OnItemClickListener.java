package com.example.mybrowser.history;

import android.view.View;

public interface OnItemClickListener {
    void onClick(View view, int pos);

    boolean onLongClick(View view, int pos);
}

