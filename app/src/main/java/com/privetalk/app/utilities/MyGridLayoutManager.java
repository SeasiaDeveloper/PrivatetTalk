package com.privetalk.app.utilities;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by zachariashad on 20/01/16.
 */
public class MyGridLayoutManager extends GridLayoutManager {

    private boolean canScroll = true;

    public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean canScrollVertically() {

        if (canScroll)
            return super.canScrollVertically();
        else
            return canScroll;
    }
}
