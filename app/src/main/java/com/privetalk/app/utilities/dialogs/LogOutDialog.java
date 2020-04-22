package com.privetalk.app.utilities.dialogs;

import android.app.Activity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;

/**
 * Created by zachariashad on 04/02/16.
 */
public class LogOutDialog {

    private RelativeLayout rootView;
    private View dialogView;
    private View dismissDialog, noButton, yesButton;
    private Animation in, out;
    private ButtonYesListener buttonYesListener;

    public interface ButtonYesListener {
        void onYesPressed();
    }

    public LogOutDialog(Activity activity, RelativeLayout rootView, LayoutInflater inflater) {
        this.rootView = rootView;

        dialogView = inflater.inflate(R.layout.dialog_logout, null);


        dismissDialog = dialogView.findViewById(R.id.closeDialog);
        noButton = dialogView.findViewById(R.id.noButton);
        yesButton = dialogView.findViewById(R.id.yesButton);


        noButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });


        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        yesButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                buttonYesListener.onYesPressed();
            }
        });

        dialogView.findViewById(R.id.outsideArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        in = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out);
    }


    public void show() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        dialogView.setLayoutParams(params);

        rootView.addView(dialogView);

        dialogView.bringToFront();

        dialogView.startAnimation(in);
    }

    public void dismiss() {
        dialogView.startAnimation(out);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((DrawerLayout) rootView.getParent()).removeView(rootView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setYesButtonListener(ButtonYesListener buttonListener) {
        this.buttonYesListener = buttonListener;
    }

}
