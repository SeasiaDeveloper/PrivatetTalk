package com.privetalk.app.mainflow.activities;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 17/12/15.
 */
public class LoadSettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_settings);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.progressbar_tint), android.graphics.PorterDuff.Mode.MULTIPLY);

    }
}
