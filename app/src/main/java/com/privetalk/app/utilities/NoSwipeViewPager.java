package com.privetalk.app.utilities;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoSwipeViewPager extends ViewPager {

    private boolean isSwipeAllowed = false;

    public NoSwipeViewPager(Context context) {
        super(context);
    }

    public NoSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//
//        return true;
//
////        return super.canScroll(v, checkV, dx, x, y);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isSwipeAllowed && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isSwipeAllowed && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {

        return false;
    }

}