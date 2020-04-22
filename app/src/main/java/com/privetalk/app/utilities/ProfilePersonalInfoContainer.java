package com.privetalk.app.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.privetalk.app.R;

/**
 * Created by zeniosagapiou on 14/01/16.
 */
public class ProfilePersonalInfoContainer extends FrameLayout {

    private TextView staticField;
    public ImageView imageView;
    private TextView placeholderField;
    public static long inflaterCounter;
    private View view;
    private final static int inflaterInterval = 10;

    private String text;
    private int lines;
    private int imageResource;


    public ProfilePersonalInfoContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public ProfilePersonalInfoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(final AttributeSet attrs, final Context context) {

//        setVisibility(INVISIBLE);

        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProfilePersonalInfoContainer,
                0, 0);

        if (inflaterCounter == 0) {

            doInflate(a, context);
            inflaterCounter++;
        }
//
//        doInflate(a, context);
//
        postDelayed(new Runnable() {
            @Override
            public void run() {

                doInflate(a, context);

            }
        }, inflaterInterval * (inflaterCounter - 1));


        inflaterCounter++;
    }



    private void doInflate(TypedArray a, Context context) {

        if (getChildCount() == 0) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_profile_personal_info, ProfilePersonalInfoContainer.this, false);
            addView(view);
        }

//        if (getChildCount() == 0)

        if (staticField == null)
            staticField = (TextView) view.findViewById(R.id.profileLocationStatic);

        staticField.setText(a.getString(R.styleable.ProfilePersonalInfoContainer_statictitleText));
        imageView = (ImageView) view.findViewById(R.id.prifleImageView);
        if (imageResource > 0)
            imageView.setImageResource(imageResource);
        else
            imageView.setImageResource(a.getResourceId(R.styleable.ProfilePersonalInfoContainer_imageViewId, R.drawable.settings));
        placeholderField = (TextView) view.findViewById(R.id.profileLocationPlaceholder);
        if (lines > 0)
            placeholderField.setMaxLines(lines);


        if (a.getString(R.styleable.ProfilePersonalInfoContainer_dynamicDescriptionText) != null)
            placeholderField.setText(a.getString(R.styleable.ProfilePersonalInfoContainer_dynamicDescriptionText));
        else if (text != null)
            placeholderField.setText(text);
        else
            placeholderField.setText(context.getString(R.string.not_yet_set));
    }

    public void setText(String text) {
        this.text = text;
        if (placeholderField != null && text != null)
            placeholderField.setText(text);
    }

    public void setMaxLines(int lines) {
        this.lines = lines;
        if (placeholderField != null && lines > 0)
            placeholderField.setMaxLines(lines);
    }

    public void setImageResource(int resource) {
        this.imageResource = resource;
        if (imageView != null)
            imageView.setImageResource(resource);

    }

}
