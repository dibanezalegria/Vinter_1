package com.example.android.vinter_1;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom ViewPager that allows to enable/disable swiping between pages
 *
 */
public class CustomViewPager extends ViewPager {

    private boolean mSwipingEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSwipingEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSwipingEnabled ? super.onTouchEvent(event) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mSwipingEnabled ? super.onInterceptTouchEvent(event) : false;
    }

    public boolean isSwipingEnabled() {
        return mSwipingEnabled;
    }

    public void setSwipingEnabled(boolean swipingEnabled) {
        mSwipingEnabled = swipingEnabled;
    }
}
