package com.privetalk.app.utilities;

import android.content.Context;
import androidx.percentlayout.widget.PercentRelativeLayout;
import android.util.AttributeSet;

/**
 * Created by zachariashad on 15/01/16.
 */
public class SquarePercentageLayout extends PercentRelativeLayout {


    public SquarePercentageLayout(Context context) {
        super(context);
    }

    public SquarePercentageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquarePercentageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if(widthMode == MeasureSpec.EXACTLY && widthSize > 0){
            size = widthSize;
        }
        else if(heightMode == MeasureSpec.EXACTLY && heightSize > 0){
            size = heightSize;
        }
        else{
            size = widthSize < heightSize ? widthSize : heightSize;
        }

        int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);

    }
}
