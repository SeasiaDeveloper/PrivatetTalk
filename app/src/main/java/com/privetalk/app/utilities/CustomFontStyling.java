package com.privetalk.app.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 31/12/15.
 */
public class CustomFontStyling {

    enum FontType {REGULAR, BOLD, LIGHT, UNSPECIFIED}

    private static final String LIGHT = "{l}";
    private static final String BOLD = "{b}";
    private static final String REGULAR = "{r}";
    private static final String REGULAR_CLOSED = "{/r}";
    private static final String LIGHT_CLOSED = "{/l}";
    private static final String BOLD_CLOSED = "{/b}";

    private FontType fontType = FontType.UNSPECIFIED;

    private Context mContext;
    private Typeface normal;
    private List<StyledStringObject> stringObjectList;

    public CustomFontStyling(Context context, Typeface defaultTypeface) {
        this.mContext = context;
        normal = defaultTypeface;
    }

    public SpannableStringBuilder getCustomText(String stringie) {
        SpannableStringBuilder SS;
        stringObjectList = new ArrayList<>();

        String result = "";
        for (int i = 0; i < stringie.length(); i++) {

            result = result + stringie.charAt(i);

            if (result.contains(LIGHT) && fontType != FontType.LIGHT) {
                fontType = FontType.LIGHT;
                result = result.replace(LIGHT, "");
                if (result.length() > 0) {
                    stringObjectList.add(new StyledStringObject(result, normal));
                    result = new String();
                }
            } else if (result.contains(BOLD) && fontType != FontType.BOLD) {
                fontType = FontType.BOLD;
                result = result.replace(BOLD, "");
                if (result.length() > 0) {
                    stringObjectList.add(new StyledStringObject(result, normal));
                    result = new String();
                }
            } else if (result.contains(REGULAR) && fontType != FontType.REGULAR) {
                fontType = FontType.REGULAR;
                result = result.replace(REGULAR, "");
                if (result.length() > 0) {
                    stringObjectList.add(new StyledStringObject(result, normal));
                    result = new String();
                }
            }

            if (result.contains(LIGHT_CLOSED)) {
                fontType = FontType.UNSPECIFIED;
                result = result.replace(LIGHT_CLOSED, "");
                stringObjectList.add(new StyledStringObject(result, PriveTalkConstants.robottoLight));
                result = new String();
            } else if (result.contains(BOLD_CLOSED)) {
                fontType = FontType.UNSPECIFIED;
                result = result.replace(BOLD_CLOSED, "");
                stringObjectList.add(new StyledStringObject(result, PriveTalkConstants.robottoBold));
                result = new String();
            } else if (result.contains(REGULAR_CLOSED)) {
                fontType = FontType.UNSPECIFIED;
                result = result.replace(REGULAR_CLOSED, "");
                stringObjectList.add(new StyledStringObject(result, PriveTalkConstants.robottoRegular));
                result = new String();
            }


        }

        if (result.length() > 0) {
            stringObjectList.add(new StyledStringObject(result, normal));
        }


        String textToBeStyled = "";
        for (int i = 0; i < stringObjectList.size(); i++) {
            textToBeStyled = textToBeStyled + stringObjectList.get(i).stringie;
        }

        SS = new SpannableStringBuilder(textToBeStyled);

        int counter = 0;
        for (int i = 0; i < stringObjectList.size(); i++) {
            SS.setSpan(new CustomTypefaceSpan("", stringObjectList.get(i).typeface), counter, counter + stringObjectList.get(i).stringie.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            counter += stringObjectList.get(i).stringie.length();
        }


        return SS;
    }

}
