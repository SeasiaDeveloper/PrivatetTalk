package com.privetalk.app.welcome.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 18/12/15.
 */
public class WelcomeFragment extends Fragment{

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        Configuration c = getResources().getConfiguration();
        if (c.fontScale > 1f)
            c.fontScale = 1f;

        return rootView;
    }
}
