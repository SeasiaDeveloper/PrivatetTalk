package com.privetalk.app.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;

import java.io.IOException;

/**
 * Created by zachariashad on 20/01/16.
 */
public class BitmapUtilities {

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


    public static Bitmap getFixedSizeBitmapFromView(View view, int width, int height) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


    public static Bitmap autoRotateAndScaleBitmap(String path) {

        final Bitmap scaledBitmap = getScaledBitmapIfLarge(BitmapFactory.decodeFile(path));
        Bitmap imageBitmap = null;

        try {
            ExifInterface exif = new ExifInterface(path);

            switch (Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    imageBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
                break;

                case ExifInterface.ORIENTATION_ROTATE_180: {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    imageBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
                break;

                case ExifInterface.ORIENTATION_ROTATE_270: {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    imageBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
                break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageBitmap;
    }


    public static Bitmap getScaledBitmapIfLarge(Bitmap bitmap) {

        int bitmapMaxSide = Math.max(bitmap.getWidth(), bitmap.getHeight());

        Bitmap scaledBitmap = null;

        if (bitmapMaxSide > PriveTalkConstants.MAXIMUM_PHOTO_SIZE) {


            if (bitmap.getWidth() > bitmap.getHeight()) {

                float multiplier = (float) PriveTalkConstants.MAXIMUM_PHOTO_SIZE / (float) bitmap.getWidth();

                scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (multiplier * bitmap.getWidth()), (int) (multiplier * bitmap.getHeight()), false);

            } else {

                float multiplier = (float) PriveTalkConstants.MAXIMUM_PHOTO_SIZE / (float) bitmap.getHeight();

                scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (multiplier * bitmap.getWidth()), (int) (multiplier * bitmap.getHeight()), false);

            }
        }


        return scaledBitmap != null ? scaledBitmap : bitmap;

    }


    public static Bitmap getSmallScaledBitmap(Bitmap bitmap) {

        int bitmapMaxSide = Math.max(bitmap.getWidth(), bitmap.getHeight());

        Bitmap scaledBitmap = null;

        if (bitmapMaxSide > PriveTalkConstants.MAXIMUM_MESSAGE_PHOTO_SIZE) {


            if (bitmap.getWidth() > bitmap.getHeight()) {

                float multiplier = (float) PriveTalkConstants.MAXIMUM_MESSAGE_PHOTO_SIZE / (float) bitmap.getWidth();

                scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (multiplier * bitmap.getWidth()), (int) (multiplier * bitmap.getHeight()), false);

            } else {

                float multiplier = (float) PriveTalkConstants.MAXIMUM_MESSAGE_PHOTO_SIZE / (float) bitmap.getHeight();

                scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (multiplier * bitmap.getWidth()), (int) (multiplier * bitmap.getHeight()), false);

            }
        }


        return scaledBitmap != null ? scaledBitmap : bitmap;

    }

}
