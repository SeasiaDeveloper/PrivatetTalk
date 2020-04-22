package com.privetalk.app.utilities;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

import java.util.LinkedList;

/**
 * Created by zachariashad on 22/01/16.
 */
public class MyViewPager extends ViewPager {

    private LinkedList<Integer> myOrder;
    private int frontView = 0;
    private int position = 0;

    public MyViewPager(Context context) {
        super(context);
        myOrder = new LinkedList<>();
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        myOrder = new LinkedList<>();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {

        myOrder.clear();

        for (int j =0; j <childCount; j++){
            myOrder.add(j);
        }

        myOrder.remove(frontView);
        myOrder.addLast(frontView);

        return super.getChildDrawingOrder(childCount, myOrder.get(i));
    }


    public void setFrontView(int i) {
        frontView = i;
    }

}
