package com.zhangcheng.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by zhangcheng on 16/8/16.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    private Context context;
    private List<T> dataListA;
    private boolean isSigle = true;
    private int resID;

    public CommonAdapter(Context context, List<T> dataListA, int resID) {
        this.context = context;
        this.dataListA = dataListA;
        this.resID = resID;

    }


    public List<T> getDataListA() {
        return dataListA;
    }

    public void setDataListA(List<T> dataListA) {
        this.dataListA = dataListA;
    }

    @Override
    public int getCount() {
        return dataListA.size();
    }

    @Override
    public Object getItem(int position) {
        return dataListA.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder commonViewHolder = CommonViewHolder.get(context, convertView, parent, resID, position);
        initListData(commonViewHolder, dataListA.get(position));
        return commonViewHolder.getConvertView();
    }

    public abstract void initListData(CommonViewHolder commonViewHolder, T data);
}

