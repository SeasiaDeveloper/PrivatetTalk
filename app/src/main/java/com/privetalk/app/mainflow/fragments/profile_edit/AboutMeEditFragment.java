package com.privetalk.app.mainflow.fragments.profile_edit;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.PriveTalkEditText;

public class AboutMeEditFragment extends FragmentWithTitle {

    public static boolean infochanged;
    private View rootView;
    private PriveTalkEditText editeValueText;
    public static String currentValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            currentValue = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails().about;
            infochanged = false;
        } else {
            currentValue = savedInstanceState.getString("currentValue");
            infochanged = savedInstanceState.getBoolean("infochanged");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_about_me, container, false);

        editeValueText = (PriveTalkEditText) rootView.findViewById(R.id.editItem);
        editeValueText.setText(currentValue);
        editeValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentValue = s.toString();
                infochanged = !currentValue.equals(CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails().about);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_NOTHING, getActivity(), rootView);
//        editeValueText.setSingleLine(false);
//        editeValueText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
//        editeValueText.setHint(getString(R.string.my_name_is));

        return rootView;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.fewThingsAboutMe);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentValue", currentValue);
        outState.putBoolean("infochanged", infochanged);
    }

    @Override
    public void onPause() {
        super.onPause();


        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_NOTIFICATION_ICON);
        intent.putExtra("toText", false);

        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_NOTIFICATION_ICON);
        intent.putExtra("toText", true);

        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
    }

}
