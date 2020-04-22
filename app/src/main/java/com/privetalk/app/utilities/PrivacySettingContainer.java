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
public class PrivacySettingContainer extends RelativeLayout {

    private String rowText;
    private MySwitch mySwitch;
    private PriveTalkTextView textView;
    private ProgressBar progressBar;

    public PrivacySettingContainer(Context context) {
        super(context);
        initView(LayoutInflater.from(context));
    }

    public PrivacySettingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(LayoutInflater.from(context));
    }

    public PrivacySettingContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(LayoutInflater.from(context));
    }


    private void initView(LayoutInflater mInflater) {
        View convertView = mInflater.inflate(R.layout.layout_privacy_settings_row, this, true);
        mySwitch = (MySwitch) convertView.findViewById(R.id.theSwitch);
        textView = (PriveTalkTextView) convertView.findViewById(R.id.theText);
        progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

    }


    public void setSwitchEnable(boolean isEnabled){
        mySwitch.setEnabled(isEnabled);
    }


    public void showProgressBar(boolean visibility){
        progressBar.setVisibility(visibility ? VISIBLE : GONE);
    }

    public MySwitch getMySwitch() {
        return mySwitch;
    }


    public void setSwitchesClickListener(View.OnClickListener onClickListener){
        mySwitch.setOnClickListener(onClickListener);
    }


    public void setText(int resId){
        textView.setText(getContext().getString(resId));
    }
}
