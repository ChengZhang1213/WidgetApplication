package com.example.app;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangcheng on 16/9/11.
 */
public abstract class LoadMoreBaseView <T extends RecyclerView> extends LinearLayout {

    protected T recyclerView;
    // pull state
    private static final int PULL_UP_STATE = 0;
    private static final int PULL_DOWN_STATE = 1;
    // refresh states
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    /**
     * last y
     */
    private int lastMotionY;
    /**
     * header view
     */
    private View headerView;
    /**
     * footer view
     */
    private View footerView;
    /**
     * header view height
     */
    private int headerViewHeight;
    /**
     * footer view height
     */
    private int footerViewHeight;
    /**
     * header view image
     */
    private ImageView headerImageView;
    /**
     * footer view image
     */
    private ImageView footerImageView;
    /**
     * header tip text
     */
    private TextView headerTextView;
    /**
     * footer tip text
     */
    private TextView footerTextView;
    /**
     * header refresh time
     */
    private TextView headerUpdateTextView;
    /**
     * header progress bar
     */
    private ProgressBar headerProgressBar;
    /**
     * footer progress bar
     */
    private ProgressBar footerProgressBar;
    /**
     * layout inflater
     */
    private LayoutInflater layoutInflater;
    /**
     * header view current state
     */
    private int headerState;
    /**
     * footer view current state
     */
    private int footerState;
    /**
     * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
     */
    private int pullState;
    /**
     * 变为向下的箭头,改变箭头方向
     */
    private RotateAnimation flipAnimation;
    /**
     * 变为逆向的箭头,旋转
     */
    private RotateAnimation reverseFlipAnimation;
    /**
     * footer refresh listener
     */
    private OnFooterRefreshListener onFooterRefreshListener;
    /**
     * footer refresh listener
     */
    private OnHeaderRefreshListener onHeaderRefreshListener;

    public LoadMoreBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadMoreBaseView(Context context) {
        super(context);
    }


    /**
     * init
     */
    private void init(Context context, AttributeSet attrs) {
        // Load all of the animations we need in code rather than through XML
        flipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        flipAnimation.setInterpolator(new LinearInterpolator());
        flipAnimation.setDuration(250);
        flipAnimation.setFillAfter(true);
        reverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseFlipAnimation.setInterpolator(new LinearInterpolator());
        reverseFlipAnimation.setDuration(250);
        reverseFlipAnimation.setFillAfter(true);

        layoutInflater = LayoutInflater.from(getContext());
        // header view 在此添加,保证是第一个添加到linearlayout的最上端
        addHeaderView();
        recyclerView = createRecyclerView(context, attrs);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(recyclerView);
    }

    private void addHeaderView() {
        // header view
        headerView = layoutInflater.inflate(R.layout.refresh_header, this, false);

        headerImageView = (ImageView) headerView.findViewById(R.id.pull_to_refresh_image);
        headerTextView = (TextView) headerView.findViewById(R.id.pull_to_refresh_text);
        headerUpdateTextView = (TextView) headerView.findViewById(R.id.pull_to_refresh_updated_at);
        headerProgressBar = (ProgressBar) headerView.findViewById(R.id.pull_to_refresh_progress);
        headerUpdateTextView.setText("最近更新:" + getFormatDateString("MM-dd HH:mm"));
        // header layout
        measureView(headerView);
        headerViewHeight = headerView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, headerViewHeight);
        // 设置topMargin的值为负的header View高度,即将其隐藏在最上方
        params.topMargin = -(headerViewHeight);
        // headerView.setLayoutParams(params1);
        addView(headerView, params);

    }

    public static final String getFormatDateString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    private void addFooterView() {
        // footer view
        footerView = layoutInflater.inflate(R.layout.refresh_footer, this, false);
        footerImageView = (ImageView) footerView.findViewById(R.id.pull_to_load_image);
        footerTextView = (TextView) footerView.findViewById(R.id.pull_to_load_text);
        footerProgressBar = (ProgressBar) footerView.findViewById(R.id.pull_to_load_progress);
        measureView(footerView);
        footerViewHeight = footerView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, footerViewHeight);
        addView(footerView, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addFooterView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE://刷新时禁止滑动
                if (headerState == REFRESHING || footerState == REFRESHING) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - lastMotionY;
                if (isRefreshViewScroll(deltaY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    /*
     * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
     * false)PullBaseView 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // onInterceptTouchEvent已经记录
                // lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - lastMotionY;
                if (pullState == PULL_DOWN_STATE) {
                    headerPrepareToRefresh(deltaY);
                } else if (pullState == PULL_UP_STATE) {
                    footerPrepareToRefresh(deltaY);
                }
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderTopMargin();
                if (pullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        // 开始刷新
                        headerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-headerViewHeight);
                    }
                } else if (pullState == PULL_UP_STATE) {
                    if (Math.abs(topMargin) >= headerViewHeight + footerViewHeight) {
                        // 开始执行footer 刷新
                        footerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-headerViewHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     *
     * @param deltaY , deltaY > 0 是向下运动,< 0是向上运动
     * @return
     */
    private boolean isRefreshViewScroll(int deltaY) {
        if (headerState == REFRESHING || footerState == REFRESHING) {
            return false;
        }
        if (deltaY >= -20 && deltaY <= 20)
            return false;

        if (recyclerView != null) {
            // 子view(ListView or GridView)滑动到最顶端
            if (deltaY > 0) {
                View child = recyclerView.getChildAt(0);
                if (child == null) {
                    // 如果mRecyclerView中没有数据,不拦截
                    return false;
                }
                if (isScrollTop() && child.getTop() == 0) {
                    pullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = recyclerView.getPaddingTop();
                if (isScrollTop() && Math.abs(top - padding) <= 8) {// 这里之前用3可以判断,但现在不行,还没找到原因
                    pullState = PULL_DOWN_STATE;
                    return true;
                }

            } else if (deltaY < 0) {
                View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                if (lastChild == null) {
                    // 如果mRecyclerView中没有数据,不拦截
                    return false;
                }
                // 最后一个子view的Bottom小于父View的高度说明mRecyclerView的数据没有填满父view,
                // 等于父View的高度说明mRecyclerView已经滑动到最后
                if (lastChild.getBottom() <= getHeight() && isScrollBottom()) {
                    pullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断mRecyclerView是否滑动到顶部
     *
     * @return
     */
    boolean isScrollTop() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断mRecyclerView是否滑动到底部
     *
     * @return
     */
    boolean isScrollBottom() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager.findLastVisibleItemPosition() == (recyclerView.getAdapter().getItemCount() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * header 准备刷新,手指移动过程,还没有释放
     *
     * @param deltaY ,手指滑动的距离
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
        if (newTopMargin >= 0 && headerState != RELEASE_TO_REFRESH) {
            headerTextView.setText(R.string.pull_to_refresh_release_label);
            headerUpdateTextView.setVisibility(View.VISIBLE);
            headerImageView.clearAnimation();
            headerImageView.startAnimation(flipAnimation);
            headerState = RELEASE_TO_REFRESH;
        } else if (newTopMargin < 0 && newTopMargin > -headerViewHeight) {// 拖动时没有释放
            headerImageView.clearAnimation();
            headerImageView.startAnimation(flipAnimation);
            // headerImageView.
            headerTextView.setText(R.string.pull_to_refresh_pull_label);
            headerState = PULL_TO_REFRESH;
        }
    }

    /**
     * footer 准备刷新,手指移动过程,还没有释放 移动footer view高度同样和移动header view
     * 高度是一样，都是通过修改header view的topmargin的值来达到
     *
     * @param deltaY ,手指滑动的距离
     */
    private void footerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 如果header view topMargin 的绝对值大于或等于header + footer 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        if (Math.abs(newTopMargin) >= (headerViewHeight + footerViewHeight) && footerState != RELEASE_TO_REFRESH) {
            footerTextView.setText(R.string.pull_to_refresh_footer_release_label);
            footerImageView.clearAnimation();
            footerImageView.startAnimation(flipAnimation);
            footerState = RELEASE_TO_REFRESH;
        } else if (Math.abs(newTopMargin) < (headerViewHeight + footerViewHeight)) {
            footerImageView.clearAnimation();
            footerImageView.startAnimation(flipAnimation);
            footerTextView.setText(R.string.pull_to_refresh_footer_pull_label);
            footerState = PULL_TO_REFRESH;
        }
    }

    /**
     * 修改Header view top margin的值
     *
     * @param deltaY
     * @description
     */
    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) headerView.getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * 0.3f;
        // 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
        // 表示如果是在上拉后一段距离,然后直接下拉
        if (deltaY > 0 && pullState == PULL_UP_STATE && Math.abs(params.topMargin) <= headerViewHeight) {
            return params.topMargin;
        }
        // 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
        if (deltaY < 0 && pullState == PULL_DOWN_STATE && Math.abs(params.topMargin) >= headerViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        headerView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * header refreshing
     */
    public void headerRefreshing() {
        headerState = REFRESHING;
        setHeaderTopMargin(0);
        headerImageView.setVisibility(View.GONE);
        headerImageView.clearAnimation();
        headerImageView.setImageDrawable(null);
        headerProgressBar.setVisibility(View.VISIBLE);
        headerTextView.setText(R.string.pull_to_refresh_refreshing_label);
        if (onHeaderRefreshListener != null) {
            onHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    /**
     * footer refreshing
     */
    private void footerRefreshing() {
        footerState = REFRESHING;
        int top = headerViewHeight + footerViewHeight;
        setHeaderTopMargin(-top);
        footerImageView.setVisibility(View.GONE);
        footerImageView.clearAnimation();
        footerImageView.setImageDrawable(null);
        footerTextView.setText(R.string.pull_to_refresh_footer_refreshing_label);
        footerProgressBar.setVisibility(View.VISIBLE);
        if (onFooterRefreshListener != null) {
            onFooterRefreshListener.onFooterRefresh(this);
        }
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @param topMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     * @description
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) headerView.getLayoutParams();
        params.topMargin = topMargin;
        headerView.setLayoutParams(params);
        invalidate();
    }

    /**
     * header view 完成更新后恢复初始状态
     */
    public void onHeaderRefreshComplete() {
        setHeaderTopMargin(-headerViewHeight);
        headerImageView.setVisibility(View.VISIBLE);
        headerImageView.setImageResource(R.mipmap.ic_pulltorefresh_arrow);
        headerTextView.setText(R.string.pull_to_refresh_pull_label);
        headerUpdateTextView.setText("最近更新：" + getFormatDateString("MM-dd HH:mm"));
        headerProgressBar.setVisibility(View.GONE);
        headerState = PULL_TO_REFRESH;
    }

    /**
     * footer view 完成更新后恢复初始状态
     */
    public void onFooterRefreshComplete() {
        setHeaderTopMargin(-headerViewHeight);
        footerImageView.setVisibility(View.GONE);
        footerImageView.setImageResource(R.mipmap.ic_pulltorefresh_arrow_up);
        footerTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        footerProgressBar.setVisibility(View.GONE);
        footerState = PULL_TO_REFRESH;
        if (recyclerView != null) {
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }

    }

    /**
     * 获取当前header view 的topMargin
     *
     * @description
     */
    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) headerView.getLayoutParams();
        return params.topMargin;
    }


    /**
     * set headerRefreshListener
     *
     * @description
     */
    public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
        onHeaderRefreshListener = headerRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
        onFooterRefreshListener = footerRefreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer
     * view should be refreshed.
     */
    public interface OnFooterRefreshListener {
        void onFooterRefresh(LoadMoreBaseView view);
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header
     * view should be refreshed.
     */
    public interface OnHeaderRefreshListener {
        void onHeaderRefresh(LoadMoreBaseView view);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


    protected abstract T createRecyclerView(Context context, AttributeSet attrs);
}

