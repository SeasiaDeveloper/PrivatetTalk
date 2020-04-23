package com.privetalk.app.mainflow.fragments.profile_edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.ViewpagerFragment;

import java.util.Calendar;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class ProfileEditDatePickerFragment extends FragmentWithTitle implements DatePicker.OnDateChangedListener, ViewpagerFragment {

    private View rootView;
    private DatePicker mDatePicker;
    private Calendar mCalendar;
    private TextView mDateTitle;
    private boolean mTitleNeedsUpdate;
    private int fragmentPosition;
    private long currentBirthdayValue;

    public static ProfileEditDatePickerFragment newInstance(int position) {

        ProfileEditDatePickerFragment fragmentFirst = new ProfileEditDatePickerFragment();

        Bundle args = new Bundle();
        args.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, position);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            currentBirthdayValue = PersonalInfoEditParentFragment.currentUser.birthday;
            fragmentPosition = getArguments().getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
            currentBirthdayValue = savedInstanceState.getLong("currentBirthdayValue");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, fragmentPosition);
        outState.putLong("currentBirthdayValue", currentBirthdayValue);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_edit_datepicker, container, false);

        mDatePicker = (DatePicker) rootView.findViewById(R.id.myDatePicker);
        mDateTitle = (TextView) rootView.findViewById(R.id.dateTitle);

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(currentBirthdayValue);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker.init(year, month, day, this);
        updateTitle(year, month, day);


        return rootView;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        updateTitle(year, monthOfYear, dayOfMonth);
    }


    private void updateTitle(int year, int month, int day) {

        if (!mDatePicker.getCalendarViewShown()) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);

            String title = DateUtils.formatDateTime(getContext(),
                    mCalendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);

            mDateTitle.setText(title);
            mTitleNeedsUpdate = true;
        } else {
            if (mTitleNeedsUpdate) {
                mTitleNeedsUpdate = false;
                mDateTitle.setText("");

            }
        }
    }

    @Override
    protected String getActionBarTitle() {
        return PriveTalkApplication.getInstance().getResources().getStringArray(R.array.personalInfoEditArray)[fragmentPosition].replace(":", "");
    }

    @Override
    public void onPauseFragment() {
        if (mCalendar != null) {
            if (PersonalInfoEditParentFragment.currentUser.birthday != mCalendar.getTimeInMillis()) {
                PersonalInfoEditParentFragment.infoChanged = true;
                PersonalInfoEditParentFragment.currentUser.birthday = mCalendar.getTimeInMillis();
            }
        }
    }


    @Override
    public void onResumeFragment() {
        if (mDatePicker == null)
            return;

        if (PersonalInfoEditParentFragment.currentUser.birthday_edited) {
            mDatePicker.setEnabled(false);
            mDatePicker.setFocusable(false);
            mDatePicker.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.you_cannot_edit_name), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, getResources().getDimensionPixelSize(R.dimen.edit_pager_footer) + 10);
                    toast.show();
                }
            });

        } else {
            mDatePicker.setEnabled(true);
            Toast toast = Toast.makeText(getContext(), getString(R.string.edit_birthday_once), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, getResources().getDimensionPixelSize(R.dimen.edit_pager_footer) + 10);
            toast.show();
        }
    }
}
