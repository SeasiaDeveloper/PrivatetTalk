package com.privetalk.app.utilities;

import android.view.MotionEvent;
import android.view.View;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 18/12/15.
 */
public abstract class FadeOnTouchListener implements View.OnTouchListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setTag(R.id.fade_tag, v.getAlpha());
                v.setAlpha(0.5f);
                return true;
            case MotionEvent.ACTION_UP:
                v.setAlpha((float) v.getTag(R.id.fade_tag));
                onClick(v, event);
                v.performClick();
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha((float) v.getTag(R.id.fade_tag));
                return true;
            default:
                return false;
        }

    }

    public abstract void onClick(View view, MotionEvent event);
}
