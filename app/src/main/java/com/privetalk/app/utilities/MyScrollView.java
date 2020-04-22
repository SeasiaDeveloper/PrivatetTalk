package com.privetalk.app.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by zachariashad on 29/01/16.
 */
public class MyScrollView extends ScrollView {

    private boolean canScroll = true;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean canScrollVertically(int direction) {
        if (canScroll)
            return super.canScrollVertically(direction);
        else
            return canScroll;
    }

    public boolean isScrollable() {
        return canScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (canScroll) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return canScroll; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!canScroll) return false;
        else return super.onInterceptTouchEvent(ev);
    }


    public void setScrollEnabled(boolean canScroll) {
        this.canScroll = canScroll;
    }

}
