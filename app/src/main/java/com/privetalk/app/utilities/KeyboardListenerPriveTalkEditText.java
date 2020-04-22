package com.privetalk.app.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by zachariashad on 10/02/16.
 */
public class KeyboardListenerPriveTalkEditText extends PriveTalkEditText {
    private BackPressedListener mOnImeBack;

    public KeyboardListenerPriveTalkEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardListenerPriveTalkEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* constructors */

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack(this);
        }
        return super.dispatchKeyEvent(event);
    }

    public void setBackPressedListener(BackPressedListener listener) {
        mOnImeBack = listener;
    }

    public interface BackPressedListener {
        void onImeBack(KeyboardListenerPriveTalkEditText editText);
    }
}
