package com.privetalk.app.utilities;

import android.os.Handler;
import androidx.core.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zachariashad on 19/01/16.
 */
public abstract class LongPressAndClickListener implements View.OnTouchListener {

    private Handler handler;
    private float mDownRawX;
    private float mDownRawY;
    private float mDownX;
    private boolean mSwiping;
    private boolean longPressed;
    private int mSwipeSlop = -1;

    public float initialDistanceFromCarbageBucket;
    public float targetPositionX;
    public float targetPositionY;
    public float moveValue;
    public boolean canDelete = false;
    public View tempView;
    public View gradientShadowView;
    public CircleImageView tempImageView;
    public PriveTalkTextView tempTextView;

    public LongPressAndClickListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                mDownRawX = event.getRawX(); //get view x
                mDownRawY = event.getRawY();
                mDownX = event.getX();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        longPressed = true;
                        OnLongPress(v, mDownX, mDownX);
                    }
                }, 600);
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
                    if (longPressed)
                        OnMove(v, mDownRawX, mDownRawY, deltaX, deltaY);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                handler.removeCallbacksAndMessages(null);

                if (mSwiping) {
                    float x = event.getRawX();
                    float deltaX = x - mDownRawX;
                    float deltaXAbs = Math.abs(deltaX);
                    //if user dragged more tha view widtdh/4 then slide to open/close else back to place
//                    if (deltaXAbs > infoWidth / 4) {
//
//                    }
                    mSwiping = false;
                }
                if (longPressed) {
                    OnRelease(v, mDownRawX, mDownRawY);
                } else {
                    OnClick(v);
                }

                longPressed = false;
                mSwiping = false;

                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                handler.removeCallbacksAndMessages(null);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }
        return true;
    }


    public abstract void OnClick(View v);

    public abstract void OnLongPress(View v, float downX, float downY);

    public abstract void OnRelease(View v, float downX, float downY);

    public abstract void OnMove(View v, float downX, float downY, float deltaX, float deltaY);
}
