package com.privetalk.app.utilities;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

/**
 * Created by zeniosagapiou on 23/02/16.
 */
public class RotateTransformation extends BitmapTransformation{

    public RotateTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.rotateImage(toTransform, -90)   ;}

    @Override
    public String getId() {
        return "RotateTransformation";

    }
}
