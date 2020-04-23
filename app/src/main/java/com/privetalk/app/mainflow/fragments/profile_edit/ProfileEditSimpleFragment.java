package com.privetalk.app.mainflow.fragments.profile_edit;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.LocationUtilities;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.ViewpagerFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileEditSimpleFragment extends FragmentWithTitle implements ViewpagerFragment {

    private View rootView;
    private int fragmentPosition;
    private PriveTalkEditText editeValueText;

    private String currentValue;
    private AsyncTask<Void, Void, String> getLocationAdditionalInfo;
//    private SharedPreferences sharedPreferences;

    public static ProfileEditSimpleFragment newInstance(int position) {

        ProfileEditSimpleFragment fragmentFirst = new ProfileEditSimpleFragment();

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
            switch (fragmentPosition) {
              /*  case PersonalInfoEditPagerFragment.NAME:
                    currentValue = PersonalInfoEditParentFragment.currentUser.name;
                    break;
                case PersonalInfoEditPagerFragment.LOCATION:
                    currentValue = PersonalInfoEditParentFragment.currentUser.location;
                    LocationUtilities.saveSelectedLocation(-1, -1);
                    break;*/

            }
        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
            currentValue = savedInstanceState.getString("currentValue");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

//        System.out.println("ONCREATEVIEW IN SINGLEFRAGMENT: " + fragmentPosition);

        rootView = inflater.inflate(R.layout.fragment_edit_simple, container, false);

        editeValueText = (PriveTalkEditText) rootView.findViewById(R.id.editItem);
        if (currentValue != null)
            editeValueText.setText(currentValue);

        switch (fragmentPosition) {
          /*  case PersonalInfoEditPagerFragment.NAME:
                editeValueText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                editeValueText.setHint(getString(R.string.my_name_is));
                break;
            case PersonalInfoEditPagerFragment.LOCATION:
                editeValueText.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                editeValueText.setHint(getString(R.string.location).replace(":", ""));
                editeValueText.setFocusable(false);
                editeValueText.setFocusableInTouchMode(false);
                editeValueText.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        PriveTalkUtilities.changeFragment(view.getContext(), true, PriveTalkConstants.MAP_FRAGMENT_ID);
                    }
                });
                rootView.findViewById(R.id.gpsView).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.gpsImage).setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        editeValueText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                ((ViewSwitcher) rootView.findViewById(R.id.gpsImageContainer)).setDisplayedChild(0);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        ((ViewSwitcher) rootView.findViewById(R.id.gpsImageContainer)).setDisplayedChild(1);
                        editeValueText.setText(LocationUtilities.getAdminArea());
                    }
                });
                break;*/
            case PersonalInfoEditPagerFragment.WORK:
                editeValueText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                editeValueText.setHint(getString(R.string.work));
                break;
        }
        return rootView;
    }

    @Override
    protected String getActionBarTitle() {
       return PriveTalkApplication.getInstance().getResources().getStringArray(R.array.personalInfoEditArray)[fragmentPosition].replace(":", "");
     //  return "nm";

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, fragmentPosition);
        outState.putString("currentValue", currentValue);
    }

    @Override
    public void onPauseFragment() {
//        System.out.println("ON PAUSE CALLED!!!");

        if (editeValueText != null) {

           /* switch (fragmentPosition) {
                case PersonalInfoEditPagerFragment.NAME:
                    PersonalInfoEditParentFragment.infoChanged = !editeValueText.getText().toString().equals(PersonalInfoEditParentFragment.currentUser.name)
                            || PersonalInfoEditParentFragment.infoChanged;
                    PersonalInfoEditParentFragment.currentUser.name = editeValueText.getText().toString();
//                    System.out.println("Change name");
                    break;

                case PersonalInfoEditPagerFragment.LOCATION:
                    PersonalInfoEditParentFragment.infoChanged = !editeValueText.getText().toString().equals(PersonalInfoEditParentFragment.currentUser.location)
                            || PersonalInfoEditParentFragment.infoChanged;
                    PersonalInfoEditParentFragment.currentUser.location = editeValueText.getText().toString();
//                    System.out.println("Change Location");
                    break;
            }*/
        }
//        else {
//            System.out.println("editeValueText: IS NULL!!!!");
//        }

        if (getLocationAdditionalInfo != null)
            getLocationAdditionalInfo.cancel(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onResumeFragment() {

//        System.out.println("ONRESUMEFRAGMENT!! IN SINGLEFRAGMENT: " + fragmentPosition);
/*
        if (fragmentPosition == PersonalInfoEditPagerFragment.NAME && editeValueText != null) {

            if (!PersonalInfoEditParentFragment.currentUser.name_edited)
                editeValueText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            if (!PersonalInfoEditParentFragment.currentUser.name_edited) {
                                Toast toast = Toast.makeText(getContext(), getString(R.string.name_change), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM, 0, getResources().getDimensionPixelSize(R.dimen.edit_pager_footer) + 10);
                                toast.show();
                            }
                        }
                    }
                });
            else {
                editeValueText.setFocusable(false);
                editeValueText.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        Toast toast = Toast.makeText(getContext(), getString(R.string.you_cannot_edit_name), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, getResources().getDimensionPixelSize(R.dimen.edit_pager_footer) + 10);
                        toast.show();
                    }
                });
            }
        } else if (fragmentPosition == PersonalInfoEditPagerFragment.LOCATION) {

            float longitude = LocationUtilities.getSelectedLongitude();
            float lattitude = LocationUtilities.getSelectedLatitude();

            if (lattitude != -1 || longitude != -1)
                getAdditionalInfo();
        }*/
    }

    private void getAdditionalInfo() {

//        System.out.println("GET ADDITION INFO");

        getLocationAdditionalInfo = new AsyncTask<Void, Void, String>() {


            @Override
            protected void onPostExecute(String areaName) {


                if (editeValueText != null && areaName != null && !areaName.isEmpty())
                    editeValueText.setText(areaName);

                LocationUtilities.saveSelectedLocation(-1, -1);

            }

            @Override
            protected String doInBackground(Void... params) {

                float lat = LocationUtilities.getSelectedLatitude();
                float lng = LocationUtilities.getSelectedLongitude();

                if (lat == 0 && lng == 0)
                    return "";

                Geocoder geocoder = new Geocoder(PriveTalkApplication.getInstance(), Locale.getDefault());

                // lat,lng, your current location
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 10);

                    if (addresses != null && !addresses.isEmpty()) {

                        String locationToPrint = "";

                        for (Address address : addresses){

                            LocationUtilities.setPostalCode(address.getPostalCode());
                            LocationUtilities.setAdminArea(address.getAdminArea() == null ? addresses.get(0).getLocality() : addresses.get(0).getAdminArea());
                            LocationUtilities.setCountryCode(address.getCountryCode());

                            String adminArea = address.getAdminArea();
                            String countryName = address.getCountryName();

                            if (adminArea != null ){
                                locationToPrint = adminArea;
                                return locationToPrint;
                            }else if (countryName != null){
                                locationToPrint = countryName;
                            }
                        }
                        return locationToPrint;

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "";
            }
        }.execute();
    }
}
