package com.privetalk.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.welcome.WelcomeActivity;

/**
 * Created by zachariashad on 17/12/15.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.privetalk.app.R.layout.activity_launcher);

        Intent intent;

        if (CurrentUserDatasource.getInstance(this).getCurrentUserInfo() != null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, WelcomeActivity.class);
        }

        startActivity(intent);
    }
}
