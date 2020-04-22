package com.privetalk.app.utilities;

import android.content.Context;

import com.pushbots.google.gcm.GCMBroadcastReceiver;


/**
 * Created by zachariashad on 15/04/16.
 */
public class SugarReceiver extends GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return "com.privetalk.app.utilities.CustomNotifier";
//        return "com.pushbots.push.GCMIntentService";

    }
}
