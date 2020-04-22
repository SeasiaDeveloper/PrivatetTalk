package com.privetalk.app.utilities.dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.privetalk.app.BuildConfig;
import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkTextView;

/**
 * Created by zachariashad on 04/02/16.
 */
public class AboutDialog {

    private DrawerLayout rootView;
    private View dialogView;
    private View dismissDialog;
    private Animation in, out;
    private View contactUs;
    private TextView termsAndConditions;
    private TextView privacyPolicy;
    private TextView datingSafetyGuide;
    private PriveTalkTextView version;
    private View fadeInView;
    public boolean isVisible;

    public AboutDialog(Context context, DrawerLayout rootView, LayoutInflater inflater) {

        this.rootView = rootView;
        dialogView = inflater.inflate(R.layout.dialog_about, null);
        fadeInView = inflater.inflate(R.layout.view_fade_in, rootView, false);
        rootView.addView(fadeInView);
        fadeInView.animate().alpha(0.6f).setStartDelay(0).setDuration(600);

        dismissDialog = dialogView.findViewById(R.id.closeDialog);
        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        fadeInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        version = (PriveTalkTextView) dialogView.findViewById(R.id.about_version);
        version.setText(context.getString(R.string.version_placeholder, BuildConfig.VERSION_NAME));

        contactUs = dialogView.findViewById(R.id.aboutContactUs);
        contactUs.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "support@privetalk.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                view.getContext().startActivity(Intent.createChooser(emailIntent, "Contact PriveTalk"));
            }
        });

        termsAndConditions = (TextView) dialogView.findViewById(R.id.termsAndConditions);
        termsAndConditions.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                openLink(Links.TERMS_AND_CONDITIONS, view.getContext());
            }
        });
//        termsAndConditions.setPaintFlags(termsAndConditions.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        privacyPolicy = (TextView) dialogView.findViewById(R.id.privacyAndPolicy);
        privacyPolicy.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                openLink(Links.PRIVACY_AND_POLICY, view.getContext());
            }
        });
//        privacyPolicy.setPaintFlags(privacyPolicy.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        datingSafetyGuide = (TextView) dialogView.findViewById(R.id.datingSafetyGuide);
        datingSafetyGuide.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                openLink(Links.DATING_SAFETY, view.getContext());
            }
        });
//        datingSafetyGuide.setPaintFlags(datingSafetyGuide.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        in = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_bottom_out);
    }

    public void show() {
        rootView.closeDrawers();
        rootView.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        isVisible = true;
        rootView.addView(dialogView);
        dialogView.startAnimation(in);

    }

    public void dismiss() {

        rootView.closeDrawers();
        rootView.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        isVisible = false;
        dialogView.startAnimation(out);
        fadeInView.animate().alpha(0).setStartDelay(0).setDuration(400);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.removeView(dialogView);
                rootView.removeView(fadeInView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void openLink(String url, Context context) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

}
