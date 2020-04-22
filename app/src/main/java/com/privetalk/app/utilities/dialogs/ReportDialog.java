package com.privetalk.app.utilities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.GRadioGroup;
import com.privetalk.app.utilities.KeyboardListenerPriveTalkEditText;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ReportDialog {

    private final GRadioGroup radioGroup;
    private RelativeLayout rootView;
    private View dialogView;
    private View dismissDialog, sumbitReport;
    public boolean isVisible = false;

    private Animation in, out;
    private AppCompatCheckBox blockUserRadioButton;
    private AppCompatCheckBox reportUserRadioButton;
    private AppCompatRadioButton rudeAbusiveBehaviorRadioButton;
    private AppCompatRadioButton idecentContentRadioButton;
    private AppCompatRadioButton scamAccountRadioButton;
    private AppCompatRadioButton spamAccountRadioButton;
    private View fadeInView;
    private String reportedUserId;
    private Context mContext;
    private KeyboardListenerPriveTalkEditText editText;
    private boolean fromChat = false;

    public ReportDialog(final Context context, final RelativeLayout rootView, LayoutInflater inflater, String reportedUserId) {
        this.rootView = rootView;
        this.mContext = context;
        this.reportedUserId = reportedUserId;

        rootView.setClipToPadding(false);
        rootView.setClipChildren(false);

        //dialog in and out animations
        in = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_in_2);
        out = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_out);

        dialogView = inflater.inflate(R.layout.dialog_report_user, rootView, false);
        fadeInView = inflater.inflate(R.layout.view_fade_in, rootView, false);
        fadeInView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (MotionEvent.ACTION_UP == event.getAction())
                    dismiss();

                return true;
            }
        });
        rootView.addView(fadeInView);
        fadeInView.animate().alpha(0.6f).setStartDelay(0).setDuration(600);
        //all dialog views
        dismissDialog = dialogView.findViewById(R.id.closeReportDialog);
        sumbitReport = dialogView.findViewById(R.id.sendReport);
        blockUserRadioButton = (AppCompatCheckBox) dialogView.findViewById(R.id.blockUserRadioButton);
        reportUserRadioButton = (AppCompatCheckBox) dialogView.findViewById(R.id.reportUserRadioButton);
        rudeAbusiveBehaviorRadioButton = (AppCompatRadioButton) dialogView.findViewById(R.id.rudeAbusiveBehaviorRadioButton);
        idecentContentRadioButton = (AppCompatRadioButton) dialogView.findViewById(R.id.indecentContentRadioButton);
        scamAccountRadioButton = (AppCompatRadioButton) dialogView.findViewById(R.id.scamAccountRadioButton);
        spamAccountRadioButton = (AppCompatRadioButton) dialogView.findViewById(R.id.spamAccountRadioButton);
        editText = (KeyboardListenerPriveTalkEditText) dialogView.findViewById(R.id.reportEditText);

        radioGroup = new GRadioGroup(rudeAbusiveBehaviorRadioButton, idecentContentRadioButton, scamAccountRadioButton, spamAccountRadioButton);

        sumbitReport.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {


                if (!blockUserRadioButton.isChecked() && !reportUserRadioButton.isChecked()) {
                    showNotAllFieldsDialog(view.getContext());
                    return;
                }

                startSending();
                dismiss();

            }
        });

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

        dialogView.findViewById(R.id.outsideArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public ReportDialog isFromChat(boolean fromChat) {
        this.fromChat = fromChat;
        return this;
    }

    private void showNotAllFieldsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.not_all_fields));
        builder.setNeutralButton(context.getString(R.string.okay), null);
        builder.create().show();
    }


    public void show() {
        isVisible = true;

        KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_VIEWS, (Activity) mContext, rootView);

        rootView.addView(dialogView);

        dialogView.startAnimation(in);

    }


    public void dismiss() {
        isVisible = false;
        dialogView.startAnimation(out);
        fadeInView.animate().alpha(0).setStartDelay(0).setDuration(400);

        KeyboardUtilities.closeKeyboard((Activity) mContext, rootView);

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

    private void startSending() {

        if (blockUserRadioButton.isChecked())
            try {
                sendReport(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        else if (reportUserRadioButton.isChecked())
            try {
                sendReport(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    private void sendReport(boolean isBlock) throws JSONException {
        String link = "";
        final JSONObject reportObject = new JSONObject();
        if (radioGroup.selectedRadioButton != null) {
            if (radioGroup.selectedRadioButton.equals(rudeAbusiveBehaviorRadioButton)) {
                reportObject.put("rude_abusive_behavior", true);
            } else if (radioGroup.selectedRadioButton.equals(idecentContentRadioButton)) {
                reportObject.put("indecent_content", true);
            } else if (radioGroup.selectedRadioButton.equals(spamAccountRadioButton)) {
                reportObject.put("scam_account", true);
            } else if (radioGroup.selectedRadioButton.equals(scamAccountRadioButton)) {
                reportObject.put("spam_account", true);
            }
        }

        reportObject.put("from_chat", fromChat);
        reportObject.put("details", editText.getText().toString());

        if (isBlock) {
            link = Links.BLOCK_USER;
            reportObject.put("blocked", reportedUserId);
            reportObject.put("reported", reportUserRadioButton.isChecked());
        } else {
            link = Links.REPORT_USER;
            reportObject.put("reported", reportedUserId);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, link, reportObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (!response.optString("blocked").isEmpty()) {
                    Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                    intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.USER_BLOCKED);
                    LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                    intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.USER_REPORTED);
                    LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PriveTalkUtilities.parseError(error);

                if (!reportObject.optString("blocked").isEmpty()) {
                    Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                    intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.BLOCK_FAILED);
                    LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                    intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.REPORT_FAILED);
                    LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return PriveTalkUtilities.getStandardHeaders();
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(jsonObjectRequest);

    }
}