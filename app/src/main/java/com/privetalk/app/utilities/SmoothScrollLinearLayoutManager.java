package com.privetalk.app.utilities;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Created by zeniosagapiou on 29/01/16.
 */
public class SmoothScrollLinearLayoutManager extends LinearLayoutManager {

    private static final float MILLISECONDS_PER_CEBNTIMETER = 1000f;

    private Context mContext;
    private boolean canScroll = true;
    private float millisecondsPerCentimeter;

    public SmoothScrollLinearLayoutManager(Context context) {
        super(context);
        millisecondsPerCentimeter = MILLISECONDS_PER_CEBNTIMETER;
        mContext = context;
    }

    public SmoothScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        millisecondsPerCentimeter = MILLISECONDS_PER_CEBNTIMETER;
        mContext = context;
    }

    public SmoothScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        millisecondsPerCentimeter = MILLISECONDS_PER_CEBNTIMETER;
        mContext = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        //Create your RecyclerView.SmoothScroller instance? Check.
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {


            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return millisecondsPerCentimeter / displayMetrics.densityDpi;
            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return SmoothScrollLinearLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_START;
            }

        };

        //Docs do not tell us anything about this,
        //but we need to set the position we want to scroll to.
        smoothScroller.setTargetPosition(position);

        //Call startSmoothScroll(SmoothScroller)? Check.
        startSmoothScroll(smoothScroller);
    }

    public void setMillisecondsPerCebntimeter(float millisecondsPerCentimeter) {
        this.millisecondsPerCentimeter = millisecondsPerCentimeter;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        if (canScroll)
            return super.canScrollHorizontally();
        else
            return canScroll;
    }

    @Override
    public boolean canScrollVertically() {
        if (canScroll)
            return super.canScrollVertically();
        else
            return canScroll;
    }
}
