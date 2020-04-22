package com.privetalk.app.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 18/05/16.
 */
public class NotificationSettingContainer extends RelativeLayout {

//    private String rowText;
    private MySwitch emailSwitch;
    private MySwitch phoneSwitch;
    private PriveTalkTextView textView;
    private ProgressBar progressBar;

    public NotificationSettingContainer(Context context) {
        super(context);
        initView(LayoutInflater.from(context));
    }

    public NotificationSettingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(LayoutInflater.from(context));
    }

    public NotificationSettingContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(LayoutInflater.from(context));
    }


    private void initView(LayoutInflater mInflater) {
        View convertView = mInflater.inflate(R.layout.layout_notification_settings, this, true);
        emailSwitch = (MySwitch) convertView.findViewById(R.id.emailSwitch);
        phoneSwitch = (MySwitch) convertView.findViewById(R.id.phoneSwitch);
        textView = (PriveTalkTextView) convertView.findViewById(R.id.theText);
        progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

    }


    public void setSwitchEnable(boolean isEnabled){
        emailSwitch.setEnabled(isEnabled);
        phoneSwitch.setEnabled(isEnabled);
    }

    public void showProgressBar(boolean visibility){
        progressBar.setVisibility(visibility ? VISIBLE : GONE);
    }

    public MySwitch getEmailSwitch() {
        return emailSwitch;
    }

    public MySwitch getPhoneSwitch() {
        return phoneSwitch;
    }


    public void setSwitchesClickListener(View.OnClickListener onClickListener){
        emailSwitch.setOnClickListener(onClickListener);
        phoneSwitch.setOnClickListener(onClickListener);
    }


    public void setText(int resId){
        textView.setText(getContext().getString(resId));
    }
}
