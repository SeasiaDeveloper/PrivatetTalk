package com.privetalk.app.utilities;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zeniosagapiou on 04/05/16.
 */
public class NounderlineTextView extends TextView {

    public NounderlineTextView(Context context) {
        this(context, null);
        setSpannableFactory(Factory.getInstance());
    }

    public NounderlineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
        setSpannableFactory(Factory.getInstance());
    }


    public NounderlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSpannableFactory(Factory.getInstance());
    }

    private static class Factory extends Spannable.Factory {
        private final static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        @Override
        public Spannable newSpannable(CharSequence source) {
            return new SpannableNoUnderline(source);
        }
    }

    private static class SpannableNoUnderline extends SpannableString {
        public SpannableNoUnderline(CharSequence source) {
            super(source);
        }

        @Override
        public void setSpan(Object what, int start, int end, int flags) {
            if (what instanceof URLSpan) {
                what = new UrlSpanNoUnderline((URLSpan) what);
            }
            super.setSpan(what, start, end, flags);
        }
    }

    public static class UrlSpanNoUnderline extends URLSpan {
        public UrlSpanNoUnderline(URLSpan src) {
            super(src.getURL());
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
