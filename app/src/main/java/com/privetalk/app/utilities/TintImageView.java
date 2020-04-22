package com.privetalk.app.utilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TintImageView extends ImageView {

    public TintImageView(Context context) {
        super(context);
    }

    public TintImageView(Context context, AttributeSet attrs) {
       super(context, attrs);

       initView();
   }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           ColorStateList imageTintList = getImageTintList();
           if (imageTintList == null) {
               return;
           }

           setColorFilter(imageTintList.getDefaultColor(), PorterDuff.Mode.SRC_IN);
       }
   }
}