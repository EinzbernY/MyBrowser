package com.example.mybrowser.collection;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.example.mybrowser.MainActivity;
import com.example.mybrowser.R;
import com.example.mybrowser.bean.Collection;

import com.example.mybrowser.search.SearchFragment;

import java.util.ArrayList;
import java.util.List;


import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CollectionFragment extends Fragment implements CollectionView, View.OnClickListener, OnItemClickListener {
    private CollectionPresenter mCollectionPresenter;
    private List<Collection> mCollectionItems = new ArrayList<>();
    private List<String> mSelected = new ArrayList<>();
    private List<Collection> mSelectedItems = new ArrayList<>();
    private com.example.mybrowser.collection.CollectionAdapter mCollectionRecyclerviewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private MainActivity mActivity;

    private RecyclerView mCollectionList;
    private Button mDeleteAll;
    private Button mDeleteSelected;
    private ImageView mGoBack;

    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;
    private PopupWindow mDeleteAllCollectionWindow;

    @Override
    public void onResume(){
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                getActivity().onBackPressed();
                ((MainActivity)getActivity()).getNavigationBar().setVisibility(View.GONE);
                return true;
            }
            return false;
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //??????fragment???layout
        View view = inflater.inflate(R.layout.collection_fragment, container, false);//??????view????????????fragment???layout
        mCollectionPresenter = new CollectionPresenter(this);
        mActivity = (MainActivity)getActivity();
        mWindow =  mActivity.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mGoBack = view.findViewById(R.id.setting_back);
        mDeleteAll = view.findViewById(R.id.delete_all_collection);
        mDeleteSelected = view.findViewById(R.id.delete);
        mGoBack.setOnClickListener(this);
        mDeleteAll.setOnClickListener(this);
        mDeleteSelected.setOnClickListener(this);
        mCollectionList = view.findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mCollectionList.setLayoutManager(mLinearLayoutManager);
        mCollectionPresenter.getCollections();


        return view;

    }


    @Override
    public void showAllCollection(List<Collection> collections) {//??????????????????
        if (collections != null) {
            for (int i = collections.size()-1; i >= 0; i--) {
                mCollectionItems.add(collections.get(i));
            }
        }
        mCollectionRecyclerviewAdapter= new com.example.mybrowser.collection.CollectionAdapter(mCollectionItems, this);
        mCollectionList.setAdapter(mCollectionRecyclerviewAdapter);
    }

    @Override
    public void onClick(View view) {//????????????
        switch (view.getId()) {
            case R.id.delete_all_collection:
                showDeleteAllWindow();
                break;
            case R.id.delete:
                getSelectedItems();
                mSelected.clear();
                mCollectionRecyclerviewAdapter.setShowCheckBox(false);
                mDeleteSelected.setVisibility(View.GONE);
                mDeleteAll.setVisibility(View.VISIBLE);
                refreshUI();
                mCollectionPresenter.deleteSelectedCollection(mSelectedItems);
                break;
            case R.id.setting_back:
                mActivity.onBackPressed();
                break;
            case R.id.confirm_btn:
                mDeleteAllCollectionWindow.dismiss();
                mCollectionItems.clear();
                mCollectionRecyclerviewAdapter.notifyDataSetChanged();
                mCollectionPresenter.deleteAllCollection();
                break;
            case R.id.cancel_btn:
                mDeleteAllCollectionWindow.dismiss();

        }
    }


    public void goToSearch(String url) {//????????????????????????
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setURL(url);
        mActivity.replaceFragment(searchFragment);
    }

    private void refreshUI() {//????????????
        if (mCollectionRecyclerviewAdapter == null) {
            mCollectionRecyclerviewAdapter = new com.example.mybrowser.collection.CollectionAdapter(mCollectionItems, this);
            mCollectionList.setAdapter(mCollectionRecyclerviewAdapter);
        } else {
            mCollectionRecyclerviewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View view, int pos) {//??????
        if (mSelected.contains(String.valueOf(pos))) {
            mSelected.remove(String.valueOf(pos));//??????
        } else {
            mSelected.add(String.valueOf(pos));//??????
        }
    }

    @Override
    public boolean onLongClick(View view, int pos) {//??????
        refreshUI();
        mDeleteAll.setVisibility(View.GONE);//??????????????????
        mDeleteSelected.setVisibility(View.VISIBLE);//??????checkbox
        return true;
    }

    private void getSelectedItems() {//????????????????????????items
        if (!mSelected.isEmpty()) {
            if (mSelectedItems.isEmpty()) {
                for (int i = 0; i < mSelected.size(); i++) {
                    int pos = Integer.parseInt(mSelected.get(i));
                    mSelectedItems.add(mCollectionItems.get(pos));
                }
                for (int i = 0; i < mSelectedItems.size(); i++) {
                    mCollectionItems.remove(mSelectedItems.get(i));
                }
            } else {
                mSelectedItems.clear();
            }
        }
    }

    private  void showDeleteAllWindow(){
        mLayoutParams.alpha=0.9f;
        mWindow.setAttributes(mLayoutParams);
        View contentView = LayoutInflater.from(mActivity)
                .inflate(R.layout.confirm_window, null);

        mDeleteAllCollectionWindow =new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mDeleteAllCollectionWindow.setBackgroundDrawable(new BitmapDrawable());
        mDeleteAllCollectionWindow.setOutsideTouchable(true);
        mDeleteAllCollectionWindow.setAnimationStyle(R.style.pop_window_anim_style);
        View rootView=LayoutInflater.from(mActivity).inflate(R.layout.collection_fragment,null);
        mDeleteAllCollectionWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);

        //???????????? ??? ???????????? ??????
        TextView confirmText = contentView.findViewById(R.id.confirm_text);
        confirmText.setText(R.string.delete_all_collection_text);
        Button confirmBut=contentView.findViewById(R.id.confirm_btn);
        confirmBut.setText(R.string.delete_all);
        Button cancelBut=contentView.findViewById(R.id.cancel_btn);
        confirmBut.setOnClickListener(this);
        cancelBut.setOnClickListener(this);
        mDeleteAllCollectionWindow.setOnDismissListener(() -> {
            mLayoutParams.alpha = 1.0f;
            mWindow.setAttributes(mLayoutParams);
        });
    }

}