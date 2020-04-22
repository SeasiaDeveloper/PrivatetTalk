package com.privetalk.app.utilities.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.utilities.PriveTalkConstants;

/**
 * Created by zachariashad on 22/06/16.
 */
public class InstructionsDialog {

    public static final int FLAMES_INSTRUCTIONS = 0;
    public static final int HOT_WHEEL_INSTRUCTIONS = 1;

    public InstructionsDialog(Context context, final RelativeLayout rootView, final int instructions) {

        final View convertView = LayoutInflater.from(context).inflate(R.layout.layout_instructions, rootView, false);
        rootView.addView(convertView);
        View gotIt = convertView.findViewById(R.id.gotItButton);

        Glide.with(context).load(R.drawable.slide_left).into((ImageView) convertView.findViewById(R.id.slideLeft));
        Glide.with(context).load(R.drawable.slide_right).into((ImageView) convertView.findViewById(R.id.slideRight));
        Glide.with(context).load(R.drawable.slide_down).into((ImageView) convertView.findViewById(R.id.slideDown));
        Glide.with(context).load(R.drawable.slide_up).into((ImageView) convertView.findViewById(R.id.slideUp));
        Glide.with(context).load(R.drawable.click_icon).into((ImageView) convertView.findViewById(R.id.click));

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.removeView(convertView);
                switch (instructions) {
                    case FLAMES_INSTRUCTIONS:
                        PriveTalkApplication.getInstance()
                                .getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                                .edit().putBoolean(PriveTalkConstants.KEY_FLAMES_INSTRUCTIONS_READ, true).commit();
                        break;
                    case HOT_WHEEL_INSTRUCTIONS:
                        PriveTalkApplication.getInstance()
                                .getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                                .edit().putBoolean(PriveTalkConstants.KEY_HOT_WHEEL_INSTRUCTIONS_READ, true).commit();
                        break;
                }
            }
        });

    }
}
