package com.privetalk.app.utilities;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by zeniosagapiou on 21/04/16.
 */
public class SquareViewPager extends ViewPager {

    public SquareViewPager(Context context) {
        super(context);
    }

    public SquareViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width == 0) {
            super.setMeasuredDimension(height, height);
//            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else {
            super.setMeasuredDimension(width, width);
//            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
//        if (getMeasuredHeight() <= 0) {
//            int width = getMeasuredWidth();
//            setMeasuredDimension(width, width);
//        }
    }
}
