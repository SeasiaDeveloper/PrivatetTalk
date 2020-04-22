package com.privetalk.app.utilities.dialogs;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import com.privetalk.app.R;


/**
 * Created by zachariashad on 17/05/16.
 */
public class MaterialProgressDialogBuilder extends AlertDialog.Builder{


    public MaterialProgressDialogBuilder(Context context) {
        super(context);
        setView(R.layout.layout_progress_dialog);
        setCancelable(false);
    }

    public MaterialProgressDialogBuilder(Context context, int theme) {
        super(context, theme);
        setView(R.layout.layout_progress_dialog);
        setCancelable(false);
    }


}
