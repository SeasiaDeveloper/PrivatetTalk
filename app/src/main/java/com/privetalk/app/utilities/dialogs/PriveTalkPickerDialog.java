package com.privetalk.app.utilities.dialogs;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.PriveTalkTextView;

/**
 * Created by zachariashad on 01/02/16.
 */
public class PriveTalkPickerDialog {

    private View rootView;
    private View dialogView;
    public NumberPicker numberPicker;
    private PriveTalkTextView dialogTitle;
    private View dismissDialog, closeDialog;
    private Animation in, out;
    private ActionDoneListener actionDoneListener;
    private OnAnimationEndListener animationEndListener;
    private View parentView;
    public boolean isVisible = false;

    public interface ActionDoneListener {

        void onDonePressed(int position);

    }

    public interface OnAnimationEndListener {

        void withEndAnimation();
    }


    public PriveTalkPickerDialog(Activity activity, RelativeLayout rootView) {
        this.rootView = rootView;
        //all views
        dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_picker, null);
        numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialogPicker);
        setDividerColor(numberPicker, Color.GRAY);
        dialogTitle = (PriveTalkTextView) dialogView.findViewById(R.id.dialogTitle);
        dismissDialog = dialogView.findViewById(R.id.closeDialog);
        closeDialog = dialogView.findViewById(R.id.doneButton);
        parentView = dialogView.findViewById(R.id.parentView);

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        in = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out);

    }

    public PriveTalkPickerDialog show() {
        isVisible = true;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        dialogView.setLayoutParams(params);

        ((RelativeLayout) rootView).addView(dialogView);

        dialogView.bringToFront();

        dialogView.startAnimation(in);

        return this;
    }

    public void dismiss() {
        isVisible = false;
        dialogView.startAnimation(out);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animationEndListener != null)
                    animationEndListener.withEndAnimation();
                ((RelativeLayout) rootView).removeView(dialogView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public PriveTalkPickerDialog setTitle(String title) {
        dialogTitle.setText(title);
        return this;
    }

    public PriveTalkPickerDialog setSelectionArray(String[] selection) {
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(selection.length);
        numberPicker.setDisplayedValues(selection);
        return this;
    }

    public PriveTalkPickerDialog setActionDoneListener(ActionDoneListener actionDoneListener1) {
        this.actionDoneListener = actionDoneListener1;
        closeDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                actionDoneListener.onDonePressed(numberPicker.getValue());
                dismiss();
            }
        });
        return this;
    }

    public PriveTalkPickerDialog setOnEndAnimationListener(OnAnimationEndListener animationListener1) {
        this.animationEndListener = animationListener1;
        return this;
    }

    public void setSelection(int position) {
        numberPicker.setValue(position + 1);
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
