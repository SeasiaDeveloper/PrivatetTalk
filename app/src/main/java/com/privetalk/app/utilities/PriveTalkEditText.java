/*
   Pinnacle Services Ltd holds all copyrights and is the sole proprietor of the source code of the application.
   Cocoon Creations Services Ltd may develop and/or market for any other project or any other customer
   similar Source Code, modified and reproduced. Cocoon Creations Services Ltd retains the explicit right
   to modify and reproduce or use any part and/or modules of the Source Code, which are not  specific to Pinnacle Services
   Ltd API calls, in relation to any other project, in perpetuity and retains the right to transfer such right
   Any Modification to the Source Code shall mean any change, enhancement or addition
   created by Cocoon Creations Services Ltd to or of the Source Code and any Modification, other than for
   the purpose of this application, shall be considered to be a new version of the Source Code,
   which does not belong to the Pinnacle Services Ltd, which can be used by Cocoon Creations Services Ltd
   in relation to any other project and/or any other customers.
   */

package com.privetalk.app.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.privetalk.app.R;


/**
 * Created by zeniosagapiou on 23/09/15.
 */
public class PriveTalkEditText extends EditText {

    private String ttfName;
    private Typeface font;
    private CharSequence text;
    private BufferType bufferType;

    public PriveTalkEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PriveTalkEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (PriveTalkConstants.robottoRegular == null)
            PriveTalkConstants.robottoRegular = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.roboto_regular));
        if (PriveTalkConstants.robottoBold == null)
            PriveTalkConstants.robottoBold = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.roboto_bold));
        if (PriveTalkConstants.robottoLight == null)
            PriveTalkConstants.robottoLight = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.roboto_light));

        try {
            this.ttfName = attrs.getAttributeValue(context.getResources().getString(R.string.privetalk_namespace), "ttf_name");

            if (ttfName.startsWith("@string/") || ttfName.startsWith("@")) {
                TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
                this.ttfName = ta.getString(R.styleable.CustomFontTextView_ttf_name);
                ta.recycle();
            }

            this.setPaintFlags(this.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

            font = Typeface.createFromAsset(context.getAssets(), ttfName);
            setTypeface(font);
        } catch (Exception e) {
//            e.printStackTrace();
            font = Typeface.defaultFromStyle(Typeface.NORMAL);
            setTypeface(font);
        }

        setCustomHint();
        setText(text, bufferType);
    }

    private void setCustomHint() {
        if (font == null || getHint() == null)
            return;

        CustomFontStyling customFontStyling = new CustomFontStyling(getContext(), font);
        setHint(customFontStyling.getCustomText(getHint().toString()));

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null)
            return;

        this.text = text;
        this.bufferType = type;
        if (font == null)
            return;

        CustomFontStyling customFontStyling = new CustomFontStyling(getContext(), font);
        super.setText(customFontStyling.getCustomText(text.toString()), type);
    }
}
