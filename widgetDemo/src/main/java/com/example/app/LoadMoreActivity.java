package com.example.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import com.zhangcheng.widget.CommonAdapter;
import com.zhangcheng.widget.CommonViewHolder;
import com.zhangcheng.widget.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangcheng on 16/8/16.
 * 混合下拉刷新控件使用,只提供上拉加载更多
 */
public class LoadMoreActivity extends Activity {
    private LoadMoreListView lv_load_more;
    private LoadMoreAdapter loadMoreAdapter;
    private Context context;
    private List<String> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);
        context = LoadMoreActivity.this;
        lv_load_more = (LoadMoreListView) findViewById(R.id.lv_load_more);
        dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataList.add(i + " item");
        }
        loadMoreAdapter = new LoadMoreAdapter<String>(context, dataList, R.layout.item_load);
        lv_load_more.setAdapter(loadMoreAdapter);
        lv_load_more.setCanLoadMore(true);
        lv_load_more.setLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                int size = dataList.size();
                if (size < 50) {
                    for (int i = size; i < size+10; i++) {
                        dataList.add(i + " item");
                    }
//                    lv_load_more.setAdapter(loadMoreAdapter);
                }
                else {
                    lv_load_more.setCanLoadMore(false);
                    lv_load_more.hideFooter(true);
                }
                loadMoreAdapter.notifyDataSetChanged();


            }

        });

    }

    private class LoadMoreAdapter<T> extends CommonAdapter<T> {
        public LoadMoreAdapter(Context context, List<T> dataListA, int resID) {
            super(context, dataListA, resID);
        }

        @Override
        public void initListData(CommonViewHolder commonViewHolder, T data) {
            String dataString = (String) data;
            TextView tv_item = commonViewHolder.getView(R.id.tv_item);
            tv_item.setText(dataString);
        }
    }
}
