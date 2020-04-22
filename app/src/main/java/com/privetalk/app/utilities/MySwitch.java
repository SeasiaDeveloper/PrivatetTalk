package com.privetalk.app.utilities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;
import android.util.AttributeSet;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 17/02/16.
 */
public class MySwitch extends SwitchCompat {

    private Drawable track;
    private int greenColor;
    private int grayColor;

    public MySwitch(Context context) {
        super(context);
        grayColor = ContextCompat.getColor(context, R.color.disable_button_gray);
        greenColor = ContextCompat.getColor(context, R.color.verification_green);
        setTrack(isSelected());
    }

    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        greenColor = ContextCompat.getColor(context, R.color.verification_green);
        grayColor = ContextCompat.getColor(context, R.color.disable_button_gray);
        setTrack(isSelected());
    }

    public MySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        greenColor = ContextCompat.getColor(context, R.color.verification_green);
        grayColor = ContextCompat.getColor(context, R.color.disable_button_gray);
        setTrack(isSelected());
    }


    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return super.getOnFocusChangeListener();
    }


    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        setTrack(checked);
    }

    @Override
    public void setEnabled(boolean enabled) {
        setAlpha(enabled ? 1f : 0.7f);
        super.setEnabled(enabled);
    }

    private void setTrack(boolean checked) {
        track = getTrackDrawable();
        if (track != null) {
            if (checked) {
                track.setColorFilter(greenColor, PorterDuff.Mode.SRC_IN);
            } else {
                track.setColorFilter(grayColor, PorterDuff.Mode.SRC_IN);
            }
            setTrackDrawable(track);
        }
    }

}
