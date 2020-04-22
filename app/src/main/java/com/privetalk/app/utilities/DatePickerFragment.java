package com.privetalk.app.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.widget.DatePicker;

import com.privetalk.app.mainflow.activities.CreateAccountActivity;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;

    public int selectedYear, selectedMonth, selectedDay;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) - 18;
        return new DatePickerDialog(getActivity(), this, year, 0, 1);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        int extras[] = new int[3];
        extras[YEAR] = year;
        extras[MONTH] = month;
        extras[DAY] = day;
        Intent intent = new Intent(CreateAccountActivity.DATE_RECEIVER_TAG);
        intent.putExtra("extras", extras);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

}