package com.privetalk.app.utilities;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by zachariashad on 20/01/16.
 */
public class MyLinearLayoutManager extends LinearLayoutManager {

    private boolean canScroll = true;

    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
