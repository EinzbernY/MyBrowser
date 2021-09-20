package com.example.mybrowser.discover;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.mybrowser.MainActivity;
import com.example.mybrowser.R;

public class DiscoverFragment extends Fragment {
    private ImageView mGoBack;
    private MainActivity mActivity;

    @Override
    public void onResume(){
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                mActivity.onBackPressed();
                return true;
            }
            return false;
        });
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_fragment, container, false);
        mActivity = (MainActivity) getActivity();
        mActivity.getNavigationBar().setVisibility(View.GONE);
        mActivity.getTopSearch().setVisibility(View.GONE);
        mGoBack = view.findViewById(R.id.discover_back);
        mGoBack.setOnClickListener(view1 -> mActivity.onBackPressed());

        return view;
    }
}