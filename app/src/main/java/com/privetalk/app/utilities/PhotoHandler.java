package com.privetalk.app.utilities;


import android.content.Context;
import android.hardware.Camera;

/**
 * Created by zeniosagapiou on 23/02/16.
 */
public class PhotoHandler implements Camera.PictureCallback{

    private final Context context;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }
}
