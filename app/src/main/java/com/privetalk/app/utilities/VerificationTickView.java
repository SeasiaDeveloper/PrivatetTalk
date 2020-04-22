package com.privetalk.app.utilities;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;

import com.privetalk.app.R;

/**
 * Created by zeniosagapiou on 22/02/16.
 */
public class VerificationTickView extends TintImageView {

    public VerificationTickView(Context context) {
        super(context);
    }

    public VerificationTickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerificationTickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled)
            setColorFilter(ContextCompat.getColor(getContext(), R.color.tick_green), PorterDuff.Mode.SRC_IN);
        else
            setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
    }
}
