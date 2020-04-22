package com.privetalk.app.utilities;

import android.content.Context;
import androidx.appcompat.widget.SearchView;
import android.util.AttributeSet;

/**
 * Created by zeniosagapiou on 21/01/16.
 */
public class MySearchView extends SearchView{

    public MySearchView(Context context) {
        super(context);
    }

    public MySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
