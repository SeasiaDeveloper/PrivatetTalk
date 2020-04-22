package com.privetalk.app.utilities;

import android.content.Context;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by zachariashad on 11/02/16.
 */
public class PriveTalkRadioButton extends AppCompatRadioButton {
    public PriveTalkRadioButton(Context context) {
        super(context);
    }

    public PriveTalkRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PriveTalkRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        if(isChecked()) {
            if(getParent() instanceof RadioGroup) {
                ((RadioGroup)getParent()).clearCheck();
            }
        } else {
            setChecked(true);
        }
    }
}
