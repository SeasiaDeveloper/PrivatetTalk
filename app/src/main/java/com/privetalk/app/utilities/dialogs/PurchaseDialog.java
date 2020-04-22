package com.privetalk.app.utilities.dialogs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;

/**
 * Created by zachariashad on 04/02/16.
 */
public class PurchaseDialog {

    private RelativeLayout rootView;
    private View dialogView;
    private View dismissDialog, use, purchase;
    private Animation in, out;
    private PurchaceListener purchaceListener;
    private TextView message;
    private View shadowView;

    public interface PurchaceListener {
        void onUsePressed();

        void onPurchacePressed();
    }

    public PurchaseDialog(Activity activity, RelativeLayout rootView) {
        this.rootView = rootView;

        dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_purchase, rootView, false);

        dismissDialog = dialogView.findViewById(R.id.closeDialog);
        use = dialogView.findViewById(R.id.use);
        purchase = dialogView.findViewById(R.id.purchase);

        shadowView = LayoutInflater.from(activity).inflate(R.layout.view_fade_in, rootView, false);

        shadowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rootView.addView(shadowView);

        message = (TextView) dialogView.findViewById(R.id.dialogMessage);

        use.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                purchaceListener.onUsePressed();
                dismiss();
            }
        });


        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        purchase.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                purchaceListener.onPurchacePressed();
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


    public PurchaseDialog setMessage(String message) {
        this.message.setText(message);
        return this;
    }

    public void show() {
        shadowView.animate().alpha(0.3f).setDuration(250);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//
//        dialogView.setLayoutParams(params);

        rootView.addView(dialogView);

        dialogView.bringToFront();

        dialogView.startAnimation(in);

    }

    public void dismiss() {
        shadowView.animate().alpha(0f).setDuration(250);
        dialogView.startAnimation(out);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.removeView(dialogView);
                rootView.removeView(shadowView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public PurchaseDialog setPurchaceListener(PurchaceListener purchaceListener) {
        this.purchaceListener = purchaceListener;
        return this;
    }

}
