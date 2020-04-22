package com.privetalk.app.mainflow.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.R;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.PriveTalkConstants;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class BeARoyalUserFragment extends FragmentWithTitle {

    private String title;

    private View rootView;
    private View activateNow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_royal_user_benefits, container, false);

        activateNow = rootView.findViewById(R.id.activateNow);

        activateNow.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ROYAL_USER_PLANS_FRAGMENT_ID);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PriveTalkConstants.ACTION_BAR_TITLE, title);
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.beRoyalUser);
    }
}
