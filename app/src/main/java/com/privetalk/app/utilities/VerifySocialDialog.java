//package net.cocooncreations.privetalk.utilities;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import R;
//
//public class VerifySocialDialog {
//
//    private RelativeLayout rootView;
//    private View dialogView;
//    private View dismissDialog;
//    private ImageView imageView;
//    private boolean isVisible = false;
//
//    //dialog views
//    private View googleLoginButton;
//    private View fbLoginButton;
//    private View vkLoginButton;
//    private Animation inAnimation, outAnimation;
//
//
//    public VerifySocialDialog(final Activity activity, RelativeLayout rootView, LayoutInflater inflater) {
//
//        this.rootView = rootView;
//
//        dialogView = inflater.inflate(R.layout.dialog_create_account, rootView, false);
//
//        //get views
//        googleLoginButton = dialogView.findViewById(R.id.googleLogin);
//        fbLoginButton = dialogView.findViewById(R.id.facebookLogin);
//        vkLoginButton = dialogView.findViewById(R.id.vkIcon);
//        dismissDialog = dialogView.findViewById(R.id.closeDialog);
//
//        //background imageview with blur screenshot
//        imageView = new ImageView(activity);
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        imageView.setImageBitmap(ImageHelper.blurBitmap(activity, BitmapFromView.getBitmapFromView(rootView), 20.f));
//
//        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
//            @Override
//            public void onClick(View view, MotionEvent event) {
//                dismiss();
//            }
//        });
//
//        fbLoginButton.setOnTouchListener(new FadeOnTouchListener() {
//            @Override
//            public void onClick(View view, MotionEvent event) {
//                facebookLoginButton.performClick();
//            }
//        });
//
//
//        googleLoginButton.setOnTouchListener(new FadeOnTouchListener() {
//            @Override
//            public void onClick(View view, MotionEvent event) {
//                googleLoginButton.setEnabled(false);
//                if (mGoogleApiClient.isConnected()) {
//                    getUserInfoGooglePlus();
//                } else {
//                    mGoogleApiClient.connect();
//                }
//            }
//        });
//
//        vkLoginButton.setOnTouchListener(new FadeOnTouchListener() {
//            @Override
//            public void onClick(View view, MotionEvent event) {
//                vkStuff();
//            }
//        });
//
//
//        inAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in_2);
//        outAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out_2);
//    }
//
//    public void show() {
//        isVisible = true;
//        rootView.addView(imageView);
//        rootView.addView(dialogView);
//        dialogView.startAnimation(inAnimation);
//    }
//
//    public void dismiss() {
//        isVisible = false;
//        dialogView.startAnimation(outAnimation);
//
//        outAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                rootView.removeView(dialogView);
//                rootView.removeView(imageView);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//    }
//}
