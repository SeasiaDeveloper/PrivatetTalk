package com.privetalk.app.utilities;

import android.content.Context;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zeniosagapiou on 25/04/16.
 */
public class SquareCircleImageview extends CircleImageView {

    public SquareCircleImageview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCircleImageview(Context context, AttributeSet attrs, int defStyle) {
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
