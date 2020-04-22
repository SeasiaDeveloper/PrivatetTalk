package com.privetalk.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flurry.android.FlurryAgent;
import com.privetalk.app.services.GetLocationService;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.customHandler;
import com.pushbots.push.Pushbots;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by zeniosagapiou on 22/01/16.
 */
public class PriveTalkApplication extends Application {

    private static PriveTalkApplication instance;

    @Override
    public void onCreate() {

        super.onCreate();

        Pushbots.sharedInstance().init(this);

        Pushbots.sharedInstance().setCustomHandler(customHandler.class);

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(com.privetalk.app.R.string.flurry_key));

//        FlurryAgent.init(this, getString(R.string.flurry_key));

        Fabric.with(this, new Crashlytics());

        instance = this;
        MultiDex.install(this);

        VKSdk.initialize(getApplicationContext());

        EasyImage.configuration(this)
                .setImagesFolderName("PriveTalk")
                .saveInRootPicturesDirectory().setCopyExistingPicturesToPublicLocation(true);




        SharedPreferences sharedPreferences = getSharedPreferences(getString(com.privetalk.app.R.string.preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PriveTalkConstants.CURRENT_CHAT_GUY, "").apply();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Answers.getInstance().logCustom(new CustomEvent("Device: " + Build.MODEL + " running " + pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized PriveTalkApplication getInstance() {
        return instance;
    }

}
