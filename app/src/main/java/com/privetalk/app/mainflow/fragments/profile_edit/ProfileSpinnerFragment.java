package com.privetalk.app.mainflow.fragments.profile_edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.R;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkPicker;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.ViewpagerFragment;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class ProfileSpinnerFragment extends FragmentWithTitle implements ViewpagerFragment {

    private View rootView;
    private int fragmentPosition;
    private String[] spinnerValues;
    private int position;
    private PriveTalkPicker priveTalkPicker;

    public static ProfileSpinnerFragment newInstance(int position) {

        ProfileSpinnerFragment fragmentFirst = new ProfileSpinnerFragment();

        Bundle args = new Bundle();
        args.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, position);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragmentPosition = getArguments().getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        }

        switch (fragmentPosition) {
            case PersonalInfoEditPagerFragment.HEIGHT:
                position = 13;
                spinnerValues = new String[PriveTalkConstants.height.size()];

                for (int i = 0; i < PriveTalkConstants.height.size(); i++) {

                    if (PersonalInfoEditParentFragment.currentUser.currentUserDetails.height == PriveTalkConstants.height.get(i))
                        position = i + 1;

                    String display = PriveTalkUtilities.centimetersToFeetAndInches(PriveTalkConstants.height.get(i)) + " (" + PriveTalkConstants.height.get(i) + "cm" + ")";

                    if (i == 0)
                        display += getString(R.string.or_less);
                    else if (i == PriveTalkConstants.height.size() - 1)
                        display += getString(R.string.or_more);

                    spinnerValues[i] = display;

                }
                break;
            case PersonalInfoEditPagerFragment.WEIGHT:
                position = 45;
                spinnerValues = new String[1 + PriveTalkConstants.MAXIMUM_PERSON_WEIGHT - PriveTalkConstants.MINIMUM_PERSON_WEIGHT];
                for (int i = PriveTalkConstants.MINIMUM_PERSON_WEIGHT; i <= PriveTalkConstants.MAXIMUM_PERSON_WEIGHT; i++) {
                    if (PersonalInfoEditParentFragment.currentUser.currentUserDetails.weight == i)
                        position = 1 + i - PriveTalkConstants.MINIMUM_PERSON_WEIGHT;

                    String display = Math.round(i * PriveTalkConstants.ONE_KILOGRAM_TO_LBS) + "lb " + "(" + i + " kg" + ")";
                    if (i == PriveTalkConstants.MINIMUM_PERSON_WEIGHT)
                        display += getString(R.string.or_less);
                    else if (i == PriveTalkConstants.MAXIMUM_PERSON_WEIGHT)
                        display += getString(R.string.or_more);

                    spinnerValues[i - PriveTalkConstants.MINIMUM_PERSON_WEIGHT] = String.valueOf(display);
                }
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_edit_spinner, container, false);

        priveTalkPicker = (PriveTalkPicker) rootView.findViewById(R.id.numberPicker);
        priveTalkPicker.setMinValue(1);
        priveTalkPicker.setMaxValue(spinnerValues.length);
        priveTalkPicker.setValue(position);
        priveTalkPicker.setWrapSelectorWheel(false);
        priveTalkPicker.setDisplayedValues(spinnerValues);
        //here bug

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, fragmentPosition);
    }

    @Override
    protected String getActionBarTitle() {
        return getResources().getStringArray(R.array.personalInfoEditArray)[fragmentPosition];
    }

    @Override
    public void onPauseFragment() {

        if (priveTalkPicker != null) {
            switch (fragmentPosition) {
                case PersonalInfoEditPagerFragment.HEIGHT:
                    if (PersonalInfoEditParentFragment.currentUser.currentUserDetails.height != (PriveTalkConstants.height.get(priveTalkPicker.getValue() - 1))) {
                        PersonalInfoEditParentFragment.currentUser.currentUserDetails.height = (PriveTalkConstants.height.get(priveTalkPicker.getValue() - 1));

                    }

                    break;
                case PersonalInfoEditPagerFragment.WEIGHT:
                    if (PersonalInfoEditParentFragment.currentUser.currentUserDetails.weight != (PriveTalkConstants.MINIMUM_PERSON_WEIGHT + priveTalkPicker.getValue() - 1)) {
                        PersonalInfoEditParentFragment.currentUser.currentUserDetails.weight = (PriveTalkConstants.MINIMUM_PERSON_WEIGHT + priveTalkPicker.getValue() - 1);

                    }
                    break;

            }
        }
    }

    @Override
    public void onResumeFragment() {

    }
}
