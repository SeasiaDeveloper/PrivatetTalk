package com.privetalk.app.welcome.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 13/01/16.
 */
public class VerifiedProfilesFragment extends Fragment {

    private View title;
    private View message;
    private View image;
    private View rootView;

    private Animation slideIn1;
    private Animation slideIn2;
    private Animation slideIn3;


    private boolean isViewCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_verified_profile_pictures, container, false);


        title = rootView.findViewById(R.id.titleVerified);
        message = rootView.findViewById(R.id.messageVerified);
        image = rootView.findViewById(R.id.photoVerified);

        title.setVisibility(View.INVISIBLE);
        message.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);

        slideIn1 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideIn2 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideIn3 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);

        slideIn2.setStartOffset(100);
        slideIn3.setStartOffset(200);

        isViewCreated = true;

        return  rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            title.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);

            title.startAnimation(slideIn1);
            image.startAnimation(slideIn2);
            message.startAnimation(slideIn3);

        }else if (!isVisibleToUser && isViewCreated){
            title.setVisibility(View.INVISIBLE);
            message.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
        }
    }
}
