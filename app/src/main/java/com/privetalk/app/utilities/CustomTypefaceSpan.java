package com.privetalk.app.utilities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Created by zeniosagapiou on 10/12/15.
 */
public class CustomTypefaceSpan extends TypefaceSpan
{
    private final Typeface typeface;

    public CustomTypefaceSpan(String family, Typeface type) {
        super(family);
        this.typeface = type;
    }

    @Override
    public void updateDrawState(final TextPaint drawState)
    {
        apply(drawState);
    }

    @Override
    public void updateMeasureState(final TextPaint paint)
    {
        apply(paint);
    }

    private void apply(final Paint paint)
    {
        final Typeface oldTypeface = paint.getTypeface();
        final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
        final int fakeStyle = oldStyle & ~typeface.getStyle();

        if ((fakeStyle & Typeface.BOLD) != 0)
        {
            paint.setFakeBoldText(true);
        }

        if ((fakeStyle & Typeface.ITALIC) != 0)
        {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(typeface);
    }
}