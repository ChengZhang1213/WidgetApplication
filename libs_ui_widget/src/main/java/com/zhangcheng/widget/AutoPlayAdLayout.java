package com.zhangcheng.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by zhangcheng on 16/7/8.
 */
public class AutoPlayAdLayout extends FrameLayout implements ViewPager.OnPageChangeListener {
    private Context context;
    private ViewPager vp_content;
    private LinearLayout ll_point_group;

    private static final int DEFAULT_DOT_WIDTH = 5;
    private int indicateDotWidth = DEFAULT_DOT_WIDTH;
    private int totalCount = Integer.MAX_VALUE;
    private int currentPosition = 0;
    private int presentPosition = 0;
    private AutoPlayAdapter autoPlayAdapter;
    private AutoPlayRunnable autoPlayRunnable;
    private Handler handler;
    private int AUTO_PLAY_INTERVAL = 3000;
    private AddTextChangeListener addTextChangeListener;

    public AutoPlayAdLayout(Context context) {
        super(context);
        this.context = context;

    }

    public AutoPlayAdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public AutoPlayAdLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_auto_play, null);
        vp_content = (ViewPager) inflate.findViewById(R.id.vp_content);
        ll_point_group = (LinearLayout) inflate.findViewById(R.id.ll_point_group);
        indicateDotWidth = dip2px(context, DEFAULT_DOT_WIDTH);
        vp_content.setOnPageChangeListener(this);
        addView(inflate);

    }

    public void setAutoPlayAdapter(AutoPlayAdapter autoPlayAdapter) {
        this.autoPlayAdapter = autoPlayAdapter;
        if (autoPlayAdapter != null) {
            initAdapterData();
        }
    }

    private void initAdapterData() {
        vp_content.setAdapter(null);
        ll_point_group.removeAllViews();
        handler = new Handler();
        autoPlayRunnable = new AutoPlayRunnable();
        if (!autoPlayAdapter.isEmpty()) {
            autoPlayRunnable.start();
            int count = autoPlayAdapter.getCount();
            presentPosition = autoPlayAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View view = new View(context);
                if (currentPosition == i) {
                    view.setPressed(true);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            indicateDotWidth + dip2px(context, 3),
                            indicateDotWidth + dip2px(context, 3));
                    params.setMargins(indicateDotWidth, 0, 0, 0);
                    view.setLayoutParams(params);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicateDotWidth, indicateDotWidth);
                    params.setMargins(indicateDotWidth, 0, 0, 0);
                    view.setLayoutParams(params);
                }
                view.setBackgroundResource(R.drawable.auto_play_dot);
                ll_point_group.addView(view);
            }
            vp_content.setAdapter(new AutoPlayViewPagerAdapter());
            vp_content.setCurrentItem(0);
            vp_content.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        autoPlayRunnable.stop();
//                        startx = event.getX();
                    } else if (event.getAction() == MotionEvent.ACTION_UP
                            || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        autoPlayRunnable.start();
//                        lastx = event.getX();

                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        currentPosition = i;
        int childCount = ll_point_group.getChildCount();
        int choosePosition = 0;
        for (int j = 0; j < childCount; j++) {
            View childAt = ll_point_group.getChildAt(j);
            if (i % presentPosition == j) {
                childAt.setSelected(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        indicateDotWidth + dip2px(context, 3),
                        indicateDotWidth + dip2px(context, 3));
                params.setMargins(indicateDotWidth, 0, 0, 0);
                choosePosition  =j;

            } else {
                childAt.setSelected(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicateDotWidth, indicateDotWidth);
                params.setMargins(indicateDotWidth, 0, 0, 0);
                childAt.setLayoutParams(params);
            }
        }

        if (addTextChangeListener != null) {
            addTextChangeListener.doChangeText(choosePosition);
        }


    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void addTextChangeListener(AddTextChangeListener addTextChangeListener) {
        this.addTextChangeListener = addTextChangeListener;
    }


    private class AutoPlayViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return totalCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= presentPosition;
            View view = autoPlayAdapter.getView(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            int position = vp_content.getCurrentItem();
            if (position == 0) {
                position = presentPosition;
                vp_content.setCurrentItem(position, false);
            } else if (position == totalCount - 1) {
                position = presentPosition - 1;
                vp_content.setCurrentItem(position, false);
            }
        }
    }

    public interface AutoPlayAdapter {
        boolean isEmpty();

        View getView(int position);

        int getCount();
    }


    private class AutoPlayRunnable implements Runnable {

        private boolean shouldAutoPlay;

        public AutoPlayRunnable() {
            shouldAutoPlay = false;
        }

        public void start() {
            if (!shouldAutoPlay) {
                shouldAutoPlay = true;
                handler.removeCallbacks(this);
                handler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }

        public void stop() {
            if (shouldAutoPlay) {
                handler.removeCallbacks(this);
                shouldAutoPlay = false;
                // System.out.println(Math.abs(lastx - startx)+"----");
//                if (Math.abs(lastx - startx) >= 0
//                        && Math.abs(lastx - startx) < 50) {
//                    int currentItem = viewPager.getCurrentItem();
//                    Intent intent = new Intent(MainActivity.this,
//                            WebViewActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", mainNewsList.get(currentItem)
//                            .getTitle());
//                    bundle.putString("newsurl", mainNewsList.get(currentItem)
//                            .getUrl());
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
            }
        }

        @Override
        public void run() {
            if (shouldAutoPlay) {
                handler.removeCallbacks(this);
                int position = vp_content.getCurrentItem();
                if (position == presentPosition - 1) {
                    position = -1;
                }

                vp_content.setCurrentItem(position + 1, true);

                handler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }
    }
    public  void autoRun(boolean isRun){
        if(autoPlayRunnable!=null){
            if(isRun){
                autoPlayRunnable.start();
            }else{
                autoPlayRunnable.stop();
            }
        }
    }


}
