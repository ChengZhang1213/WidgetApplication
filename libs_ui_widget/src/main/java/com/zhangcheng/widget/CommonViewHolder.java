package com.zhangcheng.widget;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangcheng on 16/7/9.
 */
public class CommonViewHolder {
    private SparseArray<View> viewSparseArray;
    private View convertView;

    public CommonViewHolder(Context context, ViewGroup rootView, int layoutResID, int position) {
        this.viewSparseArray = new SparseArray<View>();
        this.convertView = LayoutInflater.from(context).inflate(layoutResID, rootView, false);
        convertView.setTag(this);
    }

    /**
     * get a model of commonViewHolder
     *
     * @param context
     * @param convertView
     * @param rootView
     * @param layoutResID
     * @param position
     * @return CommonViewHolder
     */
    public static CommonViewHolder get(Context context, View convertView, ViewGroup rootView, int layoutResID, int position) {
        if (convertView == null) {
            return new CommonViewHolder(context, rootView, layoutResID, position);
        }
        return (CommonViewHolder) convertView.getTag();
    }

    public <T extends View> T getView(int viewID) {
        View view = viewSparseArray.get(viewID);
        if (view == null) {
            view = convertView.findViewById(viewID);
            viewSparseArray.put(viewID, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return convertView;
    }
}
