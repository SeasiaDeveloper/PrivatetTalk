package com.privetalk.app.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.privetalk.app.R;


/**
 * Created by zachariashad on 22/03/16.
 */
public class DialogMessageLimit {


    public interface OnClickListener {

        void useClick();

        void purchaseClick();
    }

    public boolean isShowing;

    private View dialogView;
    private View useButton;
    private View purchaseButotn;
    private View closeButton;

    private OnClickListener onClickListener;

    private Context context;
    private RelativeLayout rootView;
    private Animation in, out;
    private View fadeView;

    public DialogMessageLimit(RelativeLayout rootView, final Context context) {

        this.context = context;

        this.rootView = rootView;

        in = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_out);

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_messages_limit, rootView, false);
        fadeView = LayoutInflater.from(context).inflate(R.layout.fade_view, rootView, false);

        useButton = dialogView.findViewById(R.id.useButton);
        purchaseButotn = dialogView.findViewById(R.id.purchasButton);
        closeButton = dialogView.findViewById(R.id.closeDialog);

        useButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                onClickListener.useClick();
            }
        });

        purchaseButotn.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                onClickListener.purchaseClick();
            }
        });

        closeButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        fadeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    public DialogMessageLimit setButtonsClickListener(OnClickListener onClickListener) {

        this.onClickListener = onClickListener;


        return this;
    }

    public void show() {
        isShowing = true;

        rootView.addView(fadeView);
        rootView.addView(dialogView);
        fadeView.animate().alpha(0.6f).setStartDelay(0).setDuration(500);

        dialogView.startAnimation(in);

    }


    public void dismiss() {
        isShowing = false;
        dialogView.startAnimation(out);
        fadeView.animate().alpha(0).setStartDelay(0).setDuration(400);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.removeView(fadeView);
                rootView.removeView(dialogView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


}
