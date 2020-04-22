package com.privetalk.app.utilities;

import androidx.core.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zachariashad on 28/01/16.
 */
public abstract class TranslationTouchListener implements View.OnTouchListener {

    private float mDownRawX;
    private float mDownRawY;
    private float mDownX;
    private boolean mSwiping;
    private boolean longPressed;
    private int mSwipeSlop = -1;
    public float stopValue = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                mDownRawX = event.getRawX(); //get view x
                mDownRawY = event.getRawY();
                mDownX = event.getX();

                //notify view is pressed
                OnViewPressed(v);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x = event.getRawX();
                float y = event.getRawY();

                float deltaX = x - mDownRawX;
                float deltaY = y - mDownRawY;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
                    }
                }
                if (mSwiping) {
                    OnMove(v, mDownRawX, mDownRawY, deltaX, deltaY);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                OnRelease(v);
                if (mSwiping) {
                    mSwiping = false;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }

        return true;
    }

    public abstract void OnViewPressed(View view);

    public abstract void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY);

    public abstract void OnRelease(View view);
}
