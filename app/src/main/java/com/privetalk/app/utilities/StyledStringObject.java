package com.privetalk.app.utilities;

import android.graphics.Typeface;

/**
 * Created by zachariashad on 31/12/15.
 */
public class StyledStringObject {

    public String stringie;
    public Typeface typeface;

    public StyledStringObject() {
    }

    public StyledStringObject(String stringie, Typeface typeface) {
        this.stringie = stringie;
        this.typeface = typeface;
    }
}
