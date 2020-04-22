package com.privetalk.app.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class PriveTalkPicker extends NumberPicker {


    public PriveTalkPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public PriveTalkPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {

//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PriveTalkPicker, 0, 0);
//
//        CharSequence[] entries = a.getTextArray(R.styleable.PriveTalkPicker_android_entries);
//
//        this.setMinValue(1);
//        this.setMaxValue(entries.length);
//
//        String stringArray[] = new String[entries.length];
//        int i=0;
//
//        for(CharSequence ch: entries){
//            stringArray[i++] = ch.toString();
//        }
//
//        this.setDisplayedValues(stringArray);

    }
}
