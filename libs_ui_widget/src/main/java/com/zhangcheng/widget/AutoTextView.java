package com.zhangcheng.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zhangcheng on 16/8/16.
 * 上下滚动的TextView 对应的是Marque状态的TextView
 */
public class AutoTextView extends TextView implements Animator.AnimatorListener {
    private static final int ANIMATION_DURATION = 200;
    private float height;
    private AnimatorSet animatorStartSet;
    private AnimatorSet animatorEndSet;
    private String text;

    public AutoTextView(Context context) {
        super(context);
    }

    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight();
    }

    private void initStartAnimation() {
        ObjectAnimator translate = ObjectAnimator.ofFloat(this, "translationY", 0, -height);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        animatorStartSet = new AnimatorSet();
        animatorStartSet.play(translate).with(alpha);
        animatorStartSet.setDuration(ANIMATION_DURATION);
        animatorStartSet.addListener(this);
    }

    private void initEndAnimation() {
        ObjectAnimator translate = ObjectAnimator.ofFloat(this, "translationY", height, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        animatorEndSet = new AnimatorSet();
        animatorEndSet.play(translate).with(alpha);
        animatorEndSet.setDuration(ANIMATION_DURATION);
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        this.text = text;
        if (null == animatorStartSet) {
            initStartAnimation();
        }
        animatorStartSet.start();
    }


    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        super.setText(text);
        if (null == animatorEndSet) {
            initEndAnimation();
        }
        animatorEndSet.start();
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
