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
 * Created by zachariashad on 18/12/15.
 */
public class PriveIsDefferentFragment extends Fragment {

    private View rootView;
    private View priveIsDifferent;
    private View rewards;
    private View verified;
    private View timer;
    private View noPhoto;
    private boolean isViewCreated;
    private Animation slideIn;
    private Animation slideIn1;
    private Animation slideIn2;
    private Animation slideIn3;
    private Animation slideIn4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_prive_is_different, container, false);

        priveIsDifferent = rootView.findViewById(R.id.priveIsDifferent);
        rewards = rootView.findViewById(R.id.rewards);
        verified = rootView.findViewById(R.id.verifiedSmall);
        timer = rootView.findViewById(R.id.timer);
        noPhoto = rootView.findViewById(R.id.noPhoto);

        priveIsDifferent.setVisibility(View.INVISIBLE);
        rewards.setVisibility(View.INVISIBLE);
        verified.setVisibility(View.INVISIBLE);
        timer.setVisibility(View.INVISIBLE);
        noPhoto.setVisibility(View.INVISIBLE);

        isViewCreated = true;

        slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideIn1 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideIn2 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideIn3 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideIn4 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);

        slideIn1.setStartOffset(100);
        slideIn2.setStartOffset(150);
        slideIn3.setStartOffset(200);
        slideIn4.setStartOffset(250);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            priveIsDifferent.setVisibility(View.VISIBLE);
            rewards.setVisibility(View.VISIBLE);
            verified.setVisibility(View.VISIBLE);
            timer.setVisibility(View.VISIBLE);
            noPhoto.setVisibility(View.VISIBLE);

            priveIsDifferent.startAnimation(slideIn);
            verified.startAnimation(slideIn1);
            rewards.startAnimation(slideIn2);
            timer.startAnimation(slideIn3);
            noPhoto.startAnimation(slideIn4);

        }else if (!isVisibleToUser && isViewCreated){
            priveIsDifferent.setVisibility(View.INVISIBLE);
            rewards.setVisibility(View.INVISIBLE);
            verified.setVisibility(View.INVISIBLE);
            timer.setVisibility(View.INVISIBLE);
            noPhoto.setVisibility(View.INVISIBLE);
        }
    }

}
