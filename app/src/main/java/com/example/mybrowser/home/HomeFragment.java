package com.example.mybrowser.home;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mybrowser.MainActivity;
import com.example.mybrowser.maintools.MyApplication;
import com.example.mybrowser.R;
import com.example.mybrowser.bean.Quick;
import com.example.mybrowser.discover.DiscoverFragment;
import com.example.mybrowser.search.SearchFragment;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements QuickView {
    private static final String TAG = "TEST";
    private ImageView mUser;
    private EditText mSearchContent;
    private String mContent;

    private LinearLayout mHomeSearchBar;
    private ImageView mLogo;
    private MainActivity mActivity;
    private MainActivity.MyTouchListener HomeFragmentListener;
    private RecyclerView mQuickPageRecyclerView;//快捷页recyclerView
    private QuickPageAdapter mQuickPageAdapter;//快捷页adapter
    private ArrayList<QuickPage> mQuickPagePageList;//快捷页list
    private QuickPage selectedQucikPage;//选中的那个quick page
    private QuickPresenter mQuickPresenter;
    private String mQuickPageURL;
    private QuickPage mAddButton;

    private String mQuickPageTitle;
    private LinearLayout mDiscover;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unRegisterMyTouchListener(HomeFragmentListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                mActivity.finish();
                return true;
            }
            return false;
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        mQuickPresenter = new QuickPresenter(this);
        mLogo = view.findViewById(R.id.logo);
        ((MainActivity)getActivity()).getNavigationBar().setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).getTopSearch().setVisibility(View.GONE);
        mHomeSearchBar = view.findViewById(R.id.search_bar);
        mSearchContent = view.findViewById(R.id.search_content);
        mSearchContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        mDiscover = view.findViewById(R.id.discover);

        final MainActivity activity = (MainActivity) getActivity();
        mActivity = (MainActivity)getActivity();

        mQuickPageRecyclerView = view.findViewById(R.id.quick_recyclerview);//快捷页recyclerview
        mQuickPagePageList = new ArrayList<>();//数据列表集合
        //使用了google的一个API获取到图标icon：https://www.google.com/s2/favicons?sz=64&domain_url=
        mAddButton = new QuickPage("添加快捷","",R.drawable.add_quick_page,"",true);
        mQuickPagePageList.add(mAddButton);
        initRecyclerView();//初始化quick page的recyclerveiw
        mQuickPresenter.getAllQuick();

        HomeFragmentListener = event -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                float mDownY = event.getRawY() - MainActivity.mTitleBarHeight;
                float mTopSearchY1 = mHomeSearchBar.getTop();
                float mTopSearchY2 = mHomeSearchBar.getBottom();

                if(mDownY<mTopSearchY1||mDownY>mTopSearchY2){
                    Log.d(TAG,"WebViewTouchListener: "+ event.getAction());
                    mLogo.requestFocus();
                    InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mLogo.getWindowToken(), 0);
                }
            }
        };
        mActivity.registerMyTouchListener(HomeFragmentListener);

        mDiscover.setOnClickListener(view1 -> activity.replaceFragment(new DiscoverFragment()));

        mSearchContent.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                mContent = mSearchContent.getText().toString();
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setURL(mContent);
                activity.replaceFragment(searchFragment);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchContent.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        return view;
    }

    /**
     * 初始化quick page的recyclerview
     */
    private void initRecyclerView()
    {

        //设置recyclerview
        GridLayoutManager GridLayoutManager = new GridLayoutManager(MyApplication.getContext(), 4);
        GridLayoutManager.setOrientation(RecyclerView.VERTICAL);//RecyclerView.VERTICAL
        mQuickPageRecyclerView.setLayoutManager(GridLayoutManager);
        mQuickPageAdapter = new QuickPageAdapter(getActivity(),mQuickPagePageList);
        mQuickPageRecyclerView.setAdapter(mQuickPageAdapter);


        //设置item点击事件,初始化之后要重新设置点击监听事件等，不设置可能不起作用
        mQuickPageAdapter.setItemClickListener(position -> {
            selectedQucikPage = mQuickPagePageList.get(position);

            //判断是添加快捷页还是编辑快捷页
            if(selectedQucikPage.getIsAddIcon()){//是的话调用showAddQuickPageDialog
                showAddQuickPageDialog(getActivity(),position);
            }else{//不是的话直接跳转到对应的URL页面
                mQuickPageURL = selectedQucikPage.getUrl();
                System.out.println("传入URL检测 mQuickPageURL:"+mQuickPageURL);

                SearchFragment searchFragment = new SearchFragment();//跳转到指定页面
                searchFragment.setURL(mQuickPageURL);
                mActivity.replaceFragment(searchFragment);
                InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(GridLayoutManager.findViewByPosition(position).getWindowToken(), 0);
            }

            //刷新点击选中的那个，也可以全部刷新  adapter.notifyDataSetChanged();
            mQuickPageAdapter.notifyItemChanged(position);
        });

        //item长按事件监听
        mQuickPageAdapter.setItemLongClickListener(position -> {
            selectedQucikPage = mQuickPagePageList.get(position);
            if(selectedQucikPage.getIsAddIcon()){//是的话调用添加对话框showAddQuickPageDialog
                showAddQuickPageDialog(getActivity(),position);
            }else{//不是的话调用编辑对话框showEditQuickPageDialog
                showEditQuickPageDialog(getActivity(),position);
            }
        });

    }

    //显示快捷页的"编辑"对话框
    private void showEditQuickPageDialog(Context context, final int position)
    {
        final QuickPage quickPage = mQuickPagePageList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.quick_page_dialog,null);
        ImageView iv_headImg = view.findViewById(R.id.iv_headImg);//图片
        final EditText et_title = view.findViewById(R.id.et_title);//名称
        final EditText et_url = view.findViewById(R.id.et_url);//URL

        //设置头像，这里设置出的头像不是按照原来的可能，这里可能有错，要按照selStudent的来设置要修改一下才行
        //如果设置了网上图片url的就按照网上的设置，否则设置本地的图片
        if(quickPage.getImgPathUrl()!=null && !quickPage.getImgPathUrl().isEmpty())
        {
            Glide.with(context).load(quickPage.getImgPathUrl()).into(iv_headImg);
        }else {
            Glide.with(context).load(quickPage.getImgPathId()).into(iv_headImg);
        }

//        //加载图片
//        Glide.with(context).load(quickPage.getImgPathId()).into(iv_headImg);

        //设置数据
        et_title.setText(quickPage.getTitle());
        et_url.setText(quickPage.getUrl()+"");

        //移动光标到末尾
        //SomeUtils.moveFocus(et_title);

        builder.setView(view);
        builder.setCancelable(true); //点返回是否隐藏
        builder.setTitle("编辑快捷页");
        builder.setNegativeButton("取消",null)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    //修改该快捷页面
                    String title = mQuickPagePageList.get(position).getTitle();
                    String url = mQuickPagePageList.get(position).getUrl();
                    String newTitle = et_title.getText().toString();
                    String newUrl = et_url.getText().toString();
                    mQuickPresenter.changeQuick(title, url, newTitle, newUrl);
                    quickPage.setTitle(newTitle);
                    quickPage.setUrl(newUrl);
                    //刷新列表
                    mQuickPageAdapter.notifyItemChanged(position);
                }).setNeutralButton("删除", (dialog, i) -> {
                    // 删除该快捷页面
                    mQuickPresenter.deleteQuick(mQuickPagePageList.get(position).getTitle(), mQuickPagePageList.get(position).getUrl());
                    mQuickPagePageList.remove(position);
                    //刷新列表
                    mQuickPageAdapter.notifyDataSetChanged();
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);//点dialog的外面隐藏
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.search_hint_text));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.search_hint_text));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.search_hint_text));

    }

    //显示快捷页的"添加"对话框
    private void showAddQuickPageDialog(Context context, final int position)
    {
        final QuickPage quickPage = mQuickPagePageList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.quick_page_dialog,null);
        ImageView iv_headImg = view.findViewById(R.id.iv_headImg);//图片
        final EditText et_title = view.findViewById(R.id.et_title);//名称
        final EditText et_url = view.findViewById(R.id.et_url);//URL

        //设置头像，这里设置出的头像不是按照原来的可能，这里可能有错，要按照selStudent的来设置要修改一下才行
        //如果设置了网上图片url的就按照网上的设置，否则设置本地的图片
        if(quickPage.getImgPathUrl()!=null && !quickPage.getImgPathUrl().isEmpty())
        {
            Glide.with(context).load(quickPage.getImgPathUrl()).into(iv_headImg);
        }else {
            Glide.with(context).load(quickPage.getImgPathId()).into(iv_headImg);
        }

//        //加载图片
//        Glide.with(context).load(quickPage.getImgPathId()).into(iv_headImg);

        //设置为空
        et_title.setText("");
        et_url.setText("");

        //移动光标到末尾
        //SomeUtils.moveFocus(et_title);

        builder.setView(view);
        builder.setCancelable(true); //点返回是否隐藏
        builder.setTitle("添加快捷页");


        builder.setNegativeButton("取消",null)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    mQuickPageTitle = et_title.getText().toString();//输入title
                    mQuickPageURL = "https://"+et_url.getText().toString();//输入URL

                    //添加数据
                    mQuickPresenter.insertQuick(mQuickPageTitle, mQuickPageURL);
                    mQuickPagePageList.remove(mAddButton);
                    mQuickPagePageList.add(new QuickPage(mQuickPageTitle,mQuickPageURL,R.mipmap.quick_page,"https://www.google.com/s2/favicons?sz=64&domain_url="+mQuickPageURL,false));
                    mQuickPagePageList.add(mAddButton);
                    //刷新列表
                    mQuickPageAdapter.notifyItemChanged(position);
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);//点dialog的外面隐藏
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.search_hint_text));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.search_hint_text));

    }

    @Override
    public void insertSuccess() {
        Toast.makeText(mActivity, "插入成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void insertFail() {
        Toast.makeText(mActivity, "插入重复", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAll(List<Quick> quicks) {
        if (quicks != null) {
            mQuickPagePageList.remove(mAddButton);
            for (int i = 0; i < quicks.size(); i++) {//使用google的API加载图标logo，可能加载缓慢或者失败
                QuickPage quickPage = new QuickPage(quicks.get(i).getTitle(),
                        quicks.get(i).getUrl(), R.mipmap.quick_page,
                        "https://www.google.com/s2/favicons?sz=64&domain_url="+quicks.get(i).getUrl(),
                        false);
                mQuickPagePageList.add(quickPage);
            }
            mQuickPagePageList.add(mAddButton);
            mQuickPageAdapter.notifyDataSetChanged();
        }
    }

}
