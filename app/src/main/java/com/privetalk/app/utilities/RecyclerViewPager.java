//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.privetalk.app.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.lsjwzh.widget.recyclerviewpager.R.styleable;
import com.lsjwzh.widget.recyclerviewpager.ViewUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecyclerViewPager extends RecyclerView {

    public static final boolean DEBUG = false;
    private RecyclerViewPagerAdapter<?> mViewPagerAdapter;
    private float mTriggerOffset;
    private float mFlingFactor;
    private float mTouchSpan;
    private List<RecyclerViewPager.OnPageChangedListener> mOnPageChangedListeners;
    private int mSmoothScrollTargetPosition;
    private int mPositionBeforeScroll;
    private boolean mSinglePageFling;
    boolean mNeedAdjust;
    int mFisrtLeftWhenDragging;
    int mFirstTopWhenDragging;
    View mCurView;
    int mMaxLeftWhenDragging;
    int mMinLeftWhenDragging;
    int mMaxTopWhenDragging;
    int mMinTopWhenDragging;
    private int mPositionOnTouchDown;
    private boolean mHasCalledOnPageChanged;
    private boolean reverseLayout;

    public RecyclerViewPager(Context context) {
        this(context, (AttributeSet)null);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTriggerOffset = 0.25F;
        this.mFlingFactor = 0.15F;
        this.mSmoothScrollTargetPosition = -1;
        this.mPositionBeforeScroll = -1;
        this.mMaxLeftWhenDragging = -2147483648;
        this.mMinLeftWhenDragging = 2147483647;
        this.mMaxTopWhenDragging = -2147483648;
        this.mMinTopWhenDragging = 2147483647;
        this.mPositionOnTouchDown = -1;
        this.mHasCalledOnPageChanged = true;
        this.reverseLayout = false;
        this.initAttrs(context, attrs, defStyle);
        this.setNestedScrollingEnabled(false);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, styleable.RecyclerViewPager, defStyle, 0);
        this.mFlingFactor = a.getFloat(styleable.RecyclerViewPager_rvp_flingFactor, 0.15F);
        this.mTriggerOffset = a.getFloat(styleable.RecyclerViewPager_rvp_triggerOffset, 0.25F);
        this.mSinglePageFling = a.getBoolean(styleable.RecyclerViewPager_rvp_singlePageFling, this.mSinglePageFling);
        a.recycle();
    }

    public void setFlingFactor(float flingFactor) {
        this.mFlingFactor = flingFactor;
    }

    public float getFlingFactor() {
        return this.mFlingFactor;
    }

    public void setTriggerOffset(float triggerOffset) {
        this.mTriggerOffset = triggerOffset;
    }

    public float getTriggerOffset() {
        return this.mTriggerOffset;
    }

    public void setSinglePageFling(boolean singlePageFling) {
        this.mSinglePageFling = singlePageFling;
    }

    public boolean isSinglePageFling() {
        return this.mSinglePageFling;
    }



    protected void onRestoreInstanceState(Parcelable state) {
        try {
            Field e = state.getClass().getDeclaredField("mLayoutState");
            e.setAccessible(true);
            Object layoutState = e.get(state);
            Field fAnchorOffset = layoutState.getClass().getDeclaredField("mAnchorOffset");
            Field fAnchorPosition = layoutState.getClass().getDeclaredField("mAnchorPosition");
            fAnchorPosition.setAccessible(true);
            fAnchorOffset.setAccessible(true);
            if(fAnchorOffset.getInt(layoutState) > 0) {
                fAnchorPosition.set(layoutState, Integer.valueOf(fAnchorPosition.getInt(layoutState) - 1));
            } else if(fAnchorOffset.getInt(layoutState) < 0) {
                fAnchorPosition.set(layoutState, Integer.valueOf(fAnchorPosition.getInt(layoutState) + 1));
            }

            fAnchorOffset.setInt(layoutState, 0);
        } catch (Throwable var6) {
            var6.printStackTrace();
        }

        super.onRestoreInstanceState(state);
    }

    public void setAdapter(Adapter adapter) {
        this.mViewPagerAdapter = this.ensureRecyclerViewPagerAdapter(adapter);
        super.setAdapter(this.mViewPagerAdapter);
    }

    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        this.mViewPagerAdapter = this.ensureRecyclerViewPagerAdapter(adapter);
        super.swapAdapter(this.mViewPagerAdapter, removeAndRecycleExistingViews);
    }

    public Adapter getAdapter() {
        return this.mViewPagerAdapter != null?this.mViewPagerAdapter.mAdapter:null;
    }

    public RecyclerViewPagerAdapter getWrapperAdapter() {
        return this.mViewPagerAdapter;
    }

    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if(layout instanceof LinearLayoutManager) {
            this.reverseLayout = ((LinearLayoutManager)layout).getReverseLayout();
        }

    }

    public boolean fling(int velocityX, int velocityY) {
        boolean flinging = super.fling((int)((float)velocityX * this.mFlingFactor), (int)((float)velocityY * this.mFlingFactor));
        if(flinging) {
            if(this.getLayoutManager().canScrollHorizontally()) {
                this.adjustPositionX(velocityX);
            } else {
                this.adjustPositionY(velocityY);
            }
        }

        return flinging;
    }

    public void smoothScrollToPosition(int position) {
        this.mSmoothScrollTargetPosition = position;
        if(this.getLayoutManager() != null && this.getLayoutManager() instanceof LinearLayoutManager) {
            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(this.getContext()) {
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return this.getLayoutManager() == null?null:((LinearLayoutManager)this.getLayoutManager()).computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel  (DisplayMetrics displayMetrics) {
                    return 100f / displayMetrics.densityDpi;
                }

                protected void onTargetFound(View targetView, State state, Action action) {

                    if(this.getLayoutManager() != null) {
                        int dx = this.calculateDxToMakeVisible(targetView, this.getHorizontalSnapPreference());
                        int dy = this.calculateDyToMakeVisible(targetView, this.getVerticalSnapPreference());
                        if(dx > 0) {
                            dx -= this.getLayoutManager().getLeftDecorationWidth(targetView);
                        } else {
                            dx += this.getLayoutManager().getRightDecorationWidth(targetView);
                        }

                        if(dy > 0) {
                            dy -= this.getLayoutManager().getTopDecorationHeight(targetView);
                        } else {
                            dy += this.getLayoutManager().getBottomDecorationHeight(targetView);
                        }

                        int distance = (int)Math.sqrt((double)(dx * dx + dy * dy));
                        int time = this.calculateTimeForDeceleration(distance);
                        if(time > 0) {
                            action.update(-dx, -dy, time, this.mDecelerateInterpolator);
                        }

                    }
                }
            };
            linearSmoothScroller.setTargetPosition(position);
            this.getLayoutManager().startSmoothScroll(linearSmoothScroller);
        } else {
            super.smoothScrollToPosition(position);
        }

    }

    public void scrollToPosition(int position) {
        this.mPositionBeforeScroll = this.getCurrentPosition();
        this.mSmoothScrollTargetPosition = position;
        super.scrollToPosition(position);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if(VERSION.SDK_INT < 16) {
                    RecyclerViewPager.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    RecyclerViewPager.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                if(RecyclerViewPager.this.mSmoothScrollTargetPosition >= 0 && RecyclerViewPager.this.mSmoothScrollTargetPosition < RecyclerViewPager.this.mViewPagerAdapter.getItemCount() && RecyclerViewPager.this.mOnPageChangedListeners != null) {
                    Iterator i$ = RecyclerViewPager.this.mOnPageChangedListeners.iterator();

                    while(i$.hasNext()) {
                        RecyclerViewPager.OnPageChangedListener onPageChangedListener = (RecyclerViewPager.OnPageChangedListener)i$.next();
                        if(onPageChangedListener != null) {
                            onPageChangedListener.OnPageChanged(RecyclerViewPager.this.mPositionBeforeScroll, RecyclerViewPager.this.getCurrentPosition());
                        }
                    }
                }

            }
        });
    }

    public int getCurrentPosition() {
        boolean curPosition = true;
        int curPosition1;
        if(this.getLayoutManager().canScrollHorizontally()) {
            curPosition1 = ViewUtils.getCenterXChildPosition(this);
        } else {
            curPosition1 = ViewUtils.getCenterYChildPosition(this);
        }

        if(curPosition1 < 0) {
            curPosition1 = this.mSmoothScrollTargetPosition;
        }

        return curPosition1;
    }

    protected void adjustPositionX(int velocityX) {
        if(this.reverseLayout) {
            velocityX *= -1;
        }

        int childCount = this.getChildCount();
        if(childCount > 0) {
            int curPosition = ViewUtils.getCenterXChildPosition(this);
            int childWidth = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
            int flingCount = this.getFlingCount(velocityX, childWidth);
            int targetPosition = curPosition + flingCount;
            if(this.mSinglePageFling) {
                flingCount = Math.max(-1, Math.min(1, flingCount));
                targetPosition = flingCount == 0?curPosition:this.mPositionOnTouchDown + flingCount;
            }

            targetPosition = Math.max(targetPosition, 0);
            targetPosition = Math.min(targetPosition, this.mViewPagerAdapter.getItemCount() - 1);
            if(targetPosition == curPosition && (this.mSinglePageFling && this.mPositionOnTouchDown == curPosition || !this.mSinglePageFling)) {
                View centerXChild = ViewUtils.getCenterXChild(this);
                if(centerXChild != null) {
                    if(this.mTouchSpan > (float)centerXChild.getWidth() * this.mTriggerOffset * this.mTriggerOffset && targetPosition != 0) {
                        if(!this.reverseLayout) {
                            --targetPosition;
                        } else {
                            ++targetPosition;
                        }
                    } else if(this.mTouchSpan < (float)centerXChild.getWidth() * -this.mTriggerOffset && targetPosition != this.mViewPagerAdapter.getItemCount() - 1) {
                        if(!this.reverseLayout) {
                            ++targetPosition;
                        } else {
                            --targetPosition;
                        }
                    }
                }
            }

            this.smoothScrollToPosition(this.safeTargetPosition(targetPosition, this.mViewPagerAdapter.getItemCount()));
        }

    }

    public void addOnPageChangedListener(RecyclerViewPager.OnPageChangedListener listener) {
        if(this.mOnPageChangedListeners == null) {
            this.mOnPageChangedListeners = new ArrayList();
        }

        this.mOnPageChangedListeners.add(listener);
    }

    public void removeOnPageChangedListener(RecyclerViewPager.OnPageChangedListener listener) {
        if(this.mOnPageChangedListeners != null) {
            this.mOnPageChangedListeners.remove(listener);
        }

    }

    public void clearOnPageChangedListeners() {
        if(this.mOnPageChangedListeners != null) {
            this.mOnPageChangedListeners.clear();
        }

    }

    protected void adjustPositionY(int velocityY) {
        if(this.reverseLayout) {
            velocityY *= -1;
        }

        int childCount = this.getChildCount();
        if(childCount > 0) {
            int curPosition = ViewUtils.getCenterYChildPosition(this);
            int childHeight = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
            int flingCount = this.getFlingCount(velocityY, childHeight);
            int targetPosition = curPosition + flingCount;
            if(this.mSinglePageFling) {
                flingCount = Math.max(-1, Math.min(1, flingCount));
                targetPosition = flingCount == 0?curPosition:this.mPositionOnTouchDown + flingCount;
            }

            targetPosition = Math.max(targetPosition, 0);
            targetPosition = Math.min(targetPosition, this.mViewPagerAdapter.getItemCount() - 1);
            if(targetPosition == curPosition && (this.mSinglePageFling && this.mPositionOnTouchDown == curPosition || !this.mSinglePageFling)) {
                View centerYChild = ViewUtils.getCenterYChild(this);
                if(centerYChild != null) {
                    if(this.mTouchSpan > (float)centerYChild.getHeight() * this.mTriggerOffset && targetPosition != 0) {
                        if(!this.reverseLayout) {
                            --targetPosition;
                        } else {
                            ++targetPosition;
                        }
                    } else if(this.mTouchSpan < (float)centerYChild.getHeight() * -this.mTriggerOffset && targetPosition != this.mViewPagerAdapter.getItemCount() - 1) {
                        if(!this.reverseLayout) {
                            ++targetPosition;
                        } else {
                            --targetPosition;
                        }
                    }
                }
            }

            this.smoothScrollToPosition(this.safeTargetPosition(targetPosition, this.mViewPagerAdapter.getItemCount()));
        }

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == 0) {
            this.mPositionOnTouchDown = this.getLayoutManager().canScrollHorizontally()?ViewUtils.getCenterXChildPosition(this):ViewUtils.getCenterYChildPosition(this);
        }

        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == 2 && this.mCurView != null) {
            this.mMaxLeftWhenDragging = Math.max(this.mCurView.getLeft(), this.mMaxLeftWhenDragging);
            this.mMaxTopWhenDragging = Math.max(this.mCurView.getTop(), this.mMaxTopWhenDragging);
            this.mMinLeftWhenDragging = Math.min(this.mCurView.getLeft(), this.mMinLeftWhenDragging);
            this.mMinTopWhenDragging = Math.min(this.mCurView.getTop(), this.mMinTopWhenDragging);
        }

        return super.onTouchEvent(e);
    }

    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if(state == 1) {
            this.mNeedAdjust = true;
            this.mCurView = this.getLayoutManager().canScrollHorizontally()?ViewUtils.getCenterXChild(this):ViewUtils.getCenterYChild(this);
            if(this.mCurView != null) {
                if(this.mHasCalledOnPageChanged) {
                    this.mPositionBeforeScroll = this.getChildLayoutPosition(this.mCurView);
                    this.mHasCalledOnPageChanged = false;
                }

                this.mFisrtLeftWhenDragging = this.mCurView.getLeft();
                this.mFirstTopWhenDragging = this.mCurView.getTop();
            } else {
                this.mPositionBeforeScroll = -1;
            }

            this.mTouchSpan = 0.0F;
        } else if(state == 2) {
            this.mNeedAdjust = false;
            if(this.mCurView != null) {
                if(this.getLayoutManager().canScrollHorizontally()) {
                    this.mTouchSpan = (float)(this.mCurView.getLeft() - this.mFisrtLeftWhenDragging);
                } else {
                    this.mTouchSpan = (float)(this.mCurView.getTop() - this.mFirstTopWhenDragging);
                }
            } else {
                this.mTouchSpan = 0.0F;
            }

            this.mCurView = null;
        } else if(state == 0) {
            if(this.mNeedAdjust) {
                int var4 = this.getLayoutManager().canScrollHorizontally()?ViewUtils.getCenterXChildPosition(this):ViewUtils.getCenterYChildPosition(this);
                if(this.mCurView != null) {
                    var4 = this.getChildAdapterPosition(this.mCurView);
                    int var5;
                    if(this.getLayoutManager().canScrollHorizontally()) {
                        var5 = this.mCurView.getLeft() - this.mFisrtLeftWhenDragging;
                        if((float)var5 > (float)this.mCurView.getWidth() * this.mTriggerOffset && this.mCurView.getLeft() >= this.mMaxLeftWhenDragging) {
                            if(!this.reverseLayout) {
                                --var4;
                            } else {
                                ++var4;
                            }
                        } else if((float)var5 < (float)this.mCurView.getWidth() * -this.mTriggerOffset && this.mCurView.getLeft() <= this.mMinLeftWhenDragging) {
                            if(!this.reverseLayout) {
                                ++var4;
                            } else {
                                --var4;
                            }
                        }
                    } else {
                        var5 = this.mCurView.getTop() - this.mFirstTopWhenDragging;
                        if((float)var5 > (float)this.mCurView.getHeight() * this.mTriggerOffset && this.mCurView.getTop() >= this.mMaxTopWhenDragging) {
                            if(!this.reverseLayout) {
                                --var4;
                            } else {
                                ++var4;
                            }
                        } else if((float)var5 < (float)this.mCurView.getHeight() * -this.mTriggerOffset && this.mCurView.getTop() <= this.mMinTopWhenDragging) {
                            if(!this.reverseLayout) {
                                ++var4;
                            } else {
                                --var4;
                            }
                        }
                    }
                }

                this.smoothScrollToPosition(this.safeTargetPosition(var4, this.mViewPagerAdapter.getItemCount()));
                this.mCurView = null;
            } else if(this.mSmoothScrollTargetPosition != this.mPositionBeforeScroll) {
                if(this.mOnPageChangedListeners != null) {
                    Iterator i$ = this.mOnPageChangedListeners.iterator();

                    while(i$.hasNext()) {
                        RecyclerViewPager.OnPageChangedListener onPageChangedListener = (RecyclerViewPager.OnPageChangedListener)i$.next();
                        if(onPageChangedListener != null) {
                            onPageChangedListener.OnPageChanged(this.mPositionBeforeScroll, this.mSmoothScrollTargetPosition);
                        }
                    }
                }

                this.mHasCalledOnPageChanged = true;
                this.mPositionBeforeScroll = this.mSmoothScrollTargetPosition;
            }

            this.mMaxLeftWhenDragging = -2147483648;
            this.mMinLeftWhenDragging = 2147483647;
            this.mMaxTopWhenDragging = -2147483648;
            this.mMinTopWhenDragging = 2147483647;
        }

    }

    @NonNull
    protected RecyclerViewPagerAdapter ensureRecyclerViewPagerAdapter(Adapter adapter) {
        return adapter instanceof RecyclerViewPagerAdapter?(RecyclerViewPagerAdapter)adapter:new RecyclerViewPagerAdapter(this, adapter);
    }

    private int getFlingCount(int velocity, int cellSize) {
        if(velocity == 0) {
            return 0;
        } else {
            int sign = velocity > 0?1:-1;
            return (int)((double)sign * Math.ceil((double)((float)(velocity * sign) * this.mFlingFactor / (float)cellSize - this.mTriggerOffset)));
        }
    }

    private int safeTargetPosition(int position, int count) {
        return position < 0?0:(position >= count?count - 1:position);
    }

    public interface OnPageChangedListener {
        void OnPageChanged(int var1, int var2);
    }
}
