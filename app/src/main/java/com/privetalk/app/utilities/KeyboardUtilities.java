package com.privetalk.app.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.privetalk.app.PriveTalkApplication;

/**
 * Created by zachariashad on 04/04/16.
 */
public class KeyboardUtilities {

    private static final String KEYBOARD_MEASURED = "keyboard-measured";

    public enum KEYBOARDMODE {ADJUST_VIEWS, ADJUST_NOTHING}


    public static void setMode(KEYBOARDMODE mode, Activity activity, View rootView) {

        switch (mode) {
            case ADJUST_VIEWS: {
                //make views adjustable to keyboard
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                rootView.setScrollContainer(true);
            }
            break;
            case ADJUST_NOTHING: {
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                rootView.setScrollContainer(false);
            }
            break;
        }

    }

    public static boolean keyboardMeasured() {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEYBOARD_MEASURED, false);
    }

    public static void setKeyboardMeasured(boolean measured) {
        SharedPreferences preferences = PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEYBOARD_MEASURED, measured).commit();
    }


    public static void closeKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

//    public static void showKeyboardForEditText(Activity activity, EditText view) {
//
//    }
}
