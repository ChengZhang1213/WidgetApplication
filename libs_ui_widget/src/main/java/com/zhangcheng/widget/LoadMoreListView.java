package com.zhangcheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

/**
 * Created by zhangcheng on 15/11/30.
 * 混合下拉刷新控件使用,只提供上拉加载更多
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    private int visibleItemCount;
    private int visibleLastIndex;
    private boolean isload;

    // footer view
    private LayoutInflater mInflater;
    private TextView tv_footer;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mIsLoadingMore = false;
    private OtherActionListener otherActionListener;

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // footer
        View inflate = mInflater.inflate(R.layout.item_footer, null);
        tv_footer = (TextView) inflate.findViewById(R.id.tv_footer);

        tv_footer.setText("加载数据中...");

        addFooterView(inflate,null,false);
        this.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemLastIndex = getAdapter().getCount() - 1;
        int lastIndex = itemLastIndex + 1;
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == itemLastIndex) {
            Log.i("LoadingMore", "loading " + isload);
            if (isload) {
                mOnLoadMoreListener.onLoadMore();
            }
        }
        if(otherActionListener!=null){
            otherActionListener.doOtherAction(scrollState);
        }


    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }


    public void setLoadMoreListener(OnLoadMoreListener moreListener) {
        this.mOnLoadMoreListener = moreListener;
    }

    public void setLoadComplete() {
        this.setSelection(visibleLastIndex - visibleItemCount + 1);
    }


    public void setCanLoadMore(boolean isLoad) {
        this.isload = isLoad;
        setFooterState(isload);
    }

    public void setFooterState(boolean isLoadFinish) {
        if (isLoadFinish) {
            tv_footer.setText("加载中...");
        } else {
            tv_footer.setText("暂无更多数据");
        }
    }
    public void hideFooter(boolean isHide){
        if(isHide){
            tv_footer.setVisibility(View.INVISIBLE);
        }else{
            tv_footer.setVisibility(View.VISIBLE);
        }
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
    public void setOtherActionListener(OtherActionListener otherActionListener){
        this.otherActionListener =otherActionListener;
    }


    public void onLoadMore() {

        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();

    }
    public interface  OtherActionListener{
        public void doOtherAction(int scrollStateIdle);
    }
}

