package com.privetalk.app.mainflow.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.percentlayout.widget.PercentLayoutHelper;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.InterestsDatasource;
import com.privetalk.app.database.objects.AttributesObject;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.LocationUtilities;
import com.privetalk.app.utilities.MyScrollView;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.SearchFilterContainer;
import com.privetalk.app.utilities.TranslationTouchListener;
import com.privetalk.app.utilities.dialogs.PriveTalkPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zachariashad on 28/01/16.
 */
public class SearchFilterFragment extends FragmentWithTitle implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int BUTTON = 0;
    private static final int PROGRESS = 1;

    private View rootView;

    private ImageView maleIcon;
    private ImageView femaleIcon;
    private View doneButton;
    private String[] interests;

    private MyScrollView myScrollView;

    //    private PriveTalkPickerDialog priveTalkPickerDialog;
    private String[] dialogValues;

    //distance seek bar views and values
    private ImageView distanceBarTrack;
    private ImageView distanceThumb;
    private PriveTalkTextView searchWithinDistance;
    private PercentRelativeLayout.LayoutParams distanceBarParams;
    private PercentLayoutHelper.PercentLayoutInfo distanceInfo;
    private float distanceParentLayoutWidth;
    private int distanceParentLayoutLeft;
    private float distanceTrackLength;
    private float distanceParentRatio;
    private int radius = PriveTalkConstants.MAXIMUM_SEARCH_DISTANCE;

    //age span seek bar views and values
    private ImageView ageSpanLeftThumb;
    private ImageView ageSpanRightThumb;
    private ImageView ageSpanBarTrack;
    private View ageSpanParentLayout;
    private PriveTalkTextView searchAges;
    private PercentRelativeLayout.LayoutParams ageSpanBarParams;
    private PercentLayoutHelper.PercentLayoutInfo ageSpanInfo;
    private int minAge = PriveTalkConstants.MINIMUM_AGE;
    private int maxAge = PriveTalkConstants.MAXIMUM_AGE;
    private float ageSpanParentLayoutWidth;
    private View durationDisable;

    //height span views and values
    private ImageView heightSpanLeftThumb;
    private ImageView heightSpanRightThumb;
    private ImageView heightSpanTrack;
    private PriveTalkTextView searchHeight;
    private PercentRelativeLayout.LayoutParams heightSpanBarParams;
    private PercentLayoutHelper.PercentLayoutInfo heightSpanInfo;
    private int minHeight = PriveTalkConstants.MINIMUM_HEGHT;
    private int maxHeight = PriveTalkConstants.MAXIMUM_HEIGHT;

    //weight span views and values
    private ImageView weightSpanLeftThumb;
    private ImageView weightSpanRightThumb;
    private ImageView weightSpanTrack;
    private PriveTalkTextView searchWeight;
    private PercentRelativeLayout.LayoutParams weightSpanBarParams;
    private PercentLayoutHelper.PercentLayoutInfo weightSpanInfo;
    private int minWeight = PriveTalkConstants.MINIMUM_WEIGHT;
    private int maxWeight = PriveTalkConstants.MAXIMUM_WEIGHT;


    //spinners (pop up dialog)
    private SearchFilterContainer bodyType;
    private SearchFilterContainer hairColor;
    private SearchFilterContainer eyeColor;
    private SearchFilterContainer educationLevel;
    private SearchFilterContainer smokingHabit;
    private SearchFilterContainer drinkingHabbit;
    private SearchFilterContainer zodiascSign;
    private SearchFilterContainer faith;

    //add languageCode views
    private LinearLayout addLanguage;
    private View addLanguageIcon;

    //ADD INTEREST VIEWS
    private LinearLayout addInterest;
    private View addInterstIcon;

    //location buttons
    private View gpsButton;
    private ViewSwitcher gpsButtonContainer;
    private PriveTalkEditText searchLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Location mLastLocation;
    private CurrentUser currentUser;
    private AsyncTask<Void, Void, String> getLocationAdditionalInfo;
    private View distanceParentLayout;
    private boolean isDialogVisible = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interests = getResources().getStringArray(R.array.interests_array);
        LocationUtilities.saveSelectedLocation(0f, 0f);
        LocationUtilities.setSelectedCountryCode("");
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showYouMustBeRoyalUser();
        }
    };

    private void showYouMustBeRoyalUser() {

        if (isDialogVisible)
            return;

        isDialogVisible = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(PriveTalkApplication.getInstance(), R.style.AppDialogTheme);

        builder.setMessage(R.string.royal_user_plans_search_filters);
        builder.setPositiveButton(getString(R.string.checkPlans), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDialogVisible = false;
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID);
            }
        });

        builder.setNegativeButton(getString(R.string.later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDialogVisible = false;
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        alertDialog.getWindow().setType(LAYOUT_FLAG);

        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getActivity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, 1234);
                isDialogVisible=false;
                return;
            }
        }
        alertDialog.show();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_search_filter, container, false);

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();

        googleApiStuff();

        initViews();

        getValues();


        if (currentUser.lookingFor == 0) {
            maleSelection(true);
            femaleSelection(true);
        } else if (currentUser.lookingFor == 1)
            maleSelection(true);
        else
            femaleSelection(true);

        return rootView;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.search_filter_action_bar);
    }


    //initialize google location api
    private void googleApiStuff() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initViews() {
        //parent layout (ScrollView)
        myScrollView = (MyScrollView) rootView.findViewById(R.id.myScrollView);

        maleIcon = (ImageView) rootView.findViewById(R.id.searchMale);
        femaleIcon = (ImageView) rootView.findViewById(R.id.searchFemale);
        doneButton = rootView.findViewById(R.id.doneButton);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCommunity();
            }
        });

        maleIcon.setTag(false);
        femaleIcon.setTag(false);

        maleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleSelection(!(boolean) maleIcon.getTag());
            }
        });

        femaleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleSelection(!(boolean) femaleIcon.getTag());
            }
        });



        /*
        Distance Seek Bar. Views Here:
        Bar(Track)
        Thumb
        Distance Text View
         */
        durationDisable = rootView.findViewById(R.id.durationDisable);
        distanceParentLayout = rootView.findViewById(R.id.distanceParentLayout);
        distanceBarTrack = (ImageView) rootView.findViewById(R.id.distanceBar);
        distanceThumb = (ImageView) rootView.findViewById(R.id.distanceThumb);
        searchWithinDistance = (PriveTalkTextView) rootView.findViewById(R.id.searchWithin);
        searchWithinDistance.setText(getString(R.string.search_filter_within)
                + "{b}"
                + String.valueOf(radius)
                + " km{/b}");
        distanceBarParams = (PercentRelativeLayout.LayoutParams) distanceBarTrack.getLayoutParams();
        distanceInfo = distanceBarParams.getPercentLayoutInfo();
        distanceInfo.rightMarginPercent = 0;

        distanceThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200);
//                view.setAlpha(0.9f);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                //Calculating the percent width(= new length/total length)
                int location[] = new int[2];
                durationDisable.getLocationOnScreen(location);
                distanceInfo.widthPercent = ((mDownRawX + deltaX - location[0]) / (float) distanceParentLayout.getWidth());
                if (distanceInfo.widthPercent < 0)
                    distanceInfo.widthPercent = 0;
//                else if (distanceInfo.widthPercent > distanceParentRatio)
//                    distanceInfo.widthPercent = distanceParentRatio;


                radius = PriveTalkConstants.MINIMUM_SEARCH_DISTANCE +
                        (int) ((PriveTalkConstants.MAXIMUM_SEARCH_DISTANCE -
                                PriveTalkConstants.MINIMUM_SEARCH_DISTANCE) * (distanceInfo.widthPercent / distanceParentRatio));
                //set distance text
                searchWithinDistance.setText(getString(R.string.search_filter_within)
                        + "{b}"
                        + String.valueOf(radius)
                        + " km{/b}");

                //set params
                distanceBarTrack.setLayoutParams(distanceBarParams);
            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
//                view.setAlpha(1.0f);
            }
        });


        /*
        Age Span Seek Bar. Views Here:
        Track
        Left Thumb
        Right Thumb
        Ages TextView
         */
        ageSpanParentLayout = rootView.findViewById(R.id.ageSpanParentLayout);
        ageSpanBarTrack = (ImageView) rootView.findViewById(R.id.ageSpanTrack);
        ageSpanLeftThumb = (ImageView) rootView.findViewById(R.id.ageSpanLeftThumb);
        ageSpanRightThumb = (ImageView) rootView.findViewById(R.id.ageSpanRightThumb);
        ageSpanBarParams = (PercentRelativeLayout.LayoutParams) ageSpanBarTrack.getLayoutParams();
        ageSpanInfo = ageSpanBarParams.getPercentLayoutInfo();
        searchAges = (PriveTalkTextView) rootView.findViewById(R.id.searchAges);
        setAge();
        ageSpanInfo.rightMarginPercent = 0;

        //left thumb
        ageSpanLeftThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                ageSpanInfo.leftMarginPercent = ((mDownRawX + deltaX - distanceParentLayoutLeft) / ageSpanParentLayoutWidth);
                ageSpanInfo.leftMarginPercent = (ageSpanInfo.leftMarginPercent < 0) ? 0 : ageSpanInfo.leftMarginPercent;
                ageSpanInfo.leftMarginPercent = (ageSpanInfo.leftMarginPercent > 1.0f) ? 1.0f : ageSpanInfo.leftMarginPercent;

                if ((ageSpanInfo.rightMarginPercent + ageSpanInfo.leftMarginPercent) > 1.0f)
                    ageSpanInfo.leftMarginPercent = 1.0f - ageSpanInfo.rightMarginPercent;

                if ((ageSpanInfo.rightMarginPercent + ageSpanInfo.leftMarginPercent) <= 1.0f) {
                    stopValue = deltaX;
                    ageSpanBarTrack.setLayoutParams(ageSpanBarParams);
                    ageSpanRightThumb.setEnabled(true);
                    minAge = (int) (PriveTalkConstants.MINIMUM_AGE + ((PriveTalkConstants.MAXIMUM_AGE - PriveTalkConstants.MINIMUM_AGE) * ageSpanInfo.leftMarginPercent));
                    setAge();
                } else {
                    ageSpanRightThumb.setEnabled(false);
                }

            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
                if (ageSpanInfo.rightMarginPercent == 0
                        && ageSpanInfo.leftMarginPercent > 0.9f) {
                    ageSpanLeftThumb.setEnabled(true);
                    ageSpanRightThumb.setEnabled(false);
                }

            }
        });

        //right thumb
        ageSpanRightThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                ageSpanInfo.rightMarginPercent = 1.0f - ((mDownRawX + deltaX - distanceParentLayoutLeft) / ageSpanParentLayoutWidth);

                ageSpanInfo.rightMarginPercent = (ageSpanInfo.rightMarginPercent < 0) ? 0 : ageSpanInfo.rightMarginPercent;
                ageSpanInfo.rightMarginPercent = (ageSpanInfo.rightMarginPercent > 1.0f) ? 1.0f : ageSpanInfo.rightMarginPercent;

                if ((ageSpanInfo.rightMarginPercent + ageSpanInfo.leftMarginPercent) > 1.0f)
                    ageSpanInfo.rightMarginPercent = 1.0f - ageSpanInfo.leftMarginPercent;


                if ((ageSpanInfo.rightMarginPercent + ageSpanInfo.leftMarginPercent) <= 1.0f) {
                    stopValue = deltaX;
                    ageSpanBarTrack.setLayoutParams(ageSpanBarParams);
                    ageSpanLeftThumb.setEnabled(true);
                    maxAge = (int) (PriveTalkConstants.MAXIMUM_AGE - ((PriveTalkConstants.MAXIMUM_AGE - PriveTalkConstants.MINIMUM_AGE) * ageSpanInfo.rightMarginPercent));
                    setAge();
                } else {
                    ageSpanLeftThumb.setEnabled(false);
                }


            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
            }
        });


        /*
        Height Span Bar
         */
        heightSpanTrack = (ImageView) rootView.findViewById(R.id.heightSpanTrack);
        heightSpanLeftThumb = (ImageView) rootView.findViewById(R.id.heightSpanLeftThumb);
        heightSpanRightThumb = (ImageView) rootView.findViewById(R.id.heightSpanRightThumb);
        heightSpanBarParams = (PercentRelativeLayout.LayoutParams) heightSpanTrack.getLayoutParams();
        heightSpanInfo = heightSpanBarParams.getPercentLayoutInfo();
        searchHeight = (PriveTalkTextView) rootView.findViewById(R.id.heightSpan);
        setHeight();
        heightSpanInfo.rightMarginPercent = 0;

        //height span left thumb

//        if (!currentUser.royal_user)
//            heightSpanLeftThumb.setOnClickListener(onClickListener);
//        else
        heightSpanLeftThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleY(1.4f).scaleX(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                if (!currentUser.royal_user) {
                    showYouMustBeRoyalUser();
                    return;
                }
                heightSpanInfo.leftMarginPercent = ((mDownRawX + deltaX - distanceParentLayoutLeft) / ageSpanParentLayoutWidth);

                heightSpanInfo.leftMarginPercent = (heightSpanInfo.leftMarginPercent < 0) ? 0 : heightSpanInfo.leftMarginPercent;
                heightSpanInfo.leftMarginPercent = (heightSpanInfo.leftMarginPercent > 1.0f) ? 1.0f : heightSpanInfo.leftMarginPercent;

                if ((heightSpanInfo.leftMarginPercent + heightSpanInfo.rightMarginPercent) > 1.0f)
                    heightSpanInfo.leftMarginPercent = 1.0f - heightSpanInfo.rightMarginPercent;

                if ((heightSpanInfo.leftMarginPercent + heightSpanInfo.rightMarginPercent) <= 1.0f) {
                    heightSpanTrack.setLayoutParams(heightSpanBarParams);
                    heightSpanRightThumb.setEnabled(true);
                    minHeight = (int) (PriveTalkConstants.MINIMUM_HEGHT + ((PriveTalkConstants.MAXIMUM_HEIGHT - PriveTalkConstants.MINIMUM_HEGHT) * heightSpanInfo.leftMarginPercent));
                    setHeight();
                } else {
                    heightSpanRightThumb.setEnabled(false);
                }
            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleY(1.0f).scaleX(1.0f).setDuration(200);

                if (heightSpanInfo.rightMarginPercent == 0
                        && heightSpanInfo.leftMarginPercent > 0.9f) {
                    heightSpanLeftThumb.setEnabled(true);
                    heightSpanRightThumb.setEnabled(false);
                }
            }
        });

//        if (!currentUser.royal_user)
//            heightSpanRightThumb.setOnClickListener(onClickListener);
//        else
        heightSpanRightThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleY(1.4f).scaleX(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {

                if (!currentUser.royal_user) {
                    showYouMustBeRoyalUser();
                    return;
                }

                heightSpanInfo.rightMarginPercent = 1.0f - ((mDownRawX + deltaX - distanceParentLayoutLeft) / ageSpanParentLayoutWidth);

                heightSpanInfo.rightMarginPercent = (heightSpanInfo.rightMarginPercent < 0) ? 0 : heightSpanInfo.rightMarginPercent;
                heightSpanInfo.rightMarginPercent = (heightSpanInfo.rightMarginPercent > 1.0f) ? 1.0f : heightSpanInfo.rightMarginPercent;

                if ((heightSpanInfo.leftMarginPercent + heightSpanInfo.rightMarginPercent) > 1.0f)
                    heightSpanInfo.rightMarginPercent = 1.0f - heightSpanInfo.leftMarginPercent;

                if ((heightSpanInfo.leftMarginPercent + heightSpanInfo.rightMarginPercent) <= 1.0f) {
                    heightSpanTrack.setLayoutParams(heightSpanBarParams);
                    heightSpanLeftThumb.setEnabled(true);
                    maxHeight = (int) (PriveTalkConstants.MAXIMUM_HEIGHT - ((PriveTalkConstants.MAXIMUM_HEIGHT - PriveTalkConstants.MINIMUM_HEGHT) * heightSpanInfo.rightMarginPercent));
                    setHeight();
                } else {
                    heightSpanLeftThumb.setEnabled(false);
                }
            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleY(1.0f).scaleX(1.0f).setDuration(200);
            }
        });



        /*
        Weight Span bar views
         */
        weightSpanTrack = (ImageView) rootView.findViewById(R.id.weightSpanTrack);
        weightSpanLeftThumb = (ImageView) rootView.findViewById(R.id.weightSpanLeftThumb);
        weightSpanRightThumb = (ImageView) rootView.findViewById(R.id.weightSpanRightThumb);
        weightSpanBarParams = (PercentRelativeLayout.LayoutParams) weightSpanTrack.getLayoutParams();
        weightSpanInfo = weightSpanBarParams.getPercentLayoutInfo();
        searchWeight = (PriveTalkTextView) rootView.findViewById(R.id.weightSpan);
        setWeight();
        weightSpanInfo.rightMarginPercent = 0;


        weightSpanLeftThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                if (!currentUser.royal_user) {
                    showYouMustBeRoyalUser();
                    return;
                }
                weightSpanInfo.leftMarginPercent = ((mDownRawX + deltaX - distanceParentLayoutLeft) / ageSpanParentLayoutWidth);

                weightSpanInfo.leftMarginPercent = (weightSpanInfo.leftMarginPercent < 0) ? 0 : weightSpanInfo.leftMarginPercent;
                weightSpanInfo.leftMarginPercent = (weightSpanInfo.leftMarginPercent > 1.0f) ? 1.0f : weightSpanInfo.leftMarginPercent;

                if ((weightSpanInfo.leftMarginPercent + weightSpanInfo.rightMarginPercent) > 1.0f)
                    weightSpanInfo.leftMarginPercent = 1.0f - weightSpanInfo.rightMarginPercent;


                if ((weightSpanInfo.leftMarginPercent + weightSpanInfo.rightMarginPercent) <= 1.0f) {
                    weightSpanTrack.setLayoutParams(weightSpanBarParams);
                    weightSpanRightThumb.setEnabled(true);
                    minWeight = (int) (PriveTalkConstants.MINIMUM_WEIGHT + ((PriveTalkConstants.MAXIMUM_WEIGHT - PriveTalkConstants.MINIMUM_WEIGHT) * weightSpanInfo.leftMarginPercent));
                    setWeight();
                } else {
                    weightSpanRightThumb.setEnabled(false);
                }
            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);

                if (weightSpanInfo.rightMarginPercent == 0
                        && weightSpanInfo.leftMarginPercent > 0.9f) {
                    weightSpanLeftThumb.setEnabled(true);
                    weightSpanRightThumb.setEnabled(false);
                }
            }
        });


        weightSpanRightThumb.setOnTouchListener(new TranslationTouchListener() {
            @Override
            public void OnViewPressed(View view) {
                myScrollView.setScrollEnabled(false);
                view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                if (!currentUser.royal_user) {
                    showYouMustBeRoyalUser();
                    return;
                }
                weightSpanInfo.rightMarginPercent = 1.0f - ((mDownRawX + deltaX - distanceParentLayoutLeft) / ageSpanParentLayoutWidth);

                weightSpanInfo.rightMarginPercent = (weightSpanInfo.rightMarginPercent < 0) ? 0 : weightSpanInfo.rightMarginPercent;
                weightSpanInfo.rightMarginPercent = (weightSpanInfo.rightMarginPercent > 1.0f) ? 1.0f : weightSpanInfo.rightMarginPercent;


                if ((weightSpanInfo.leftMarginPercent + weightSpanInfo.rightMarginPercent) > 1.0f)
                    weightSpanInfo.rightMarginPercent = 1.0f - weightSpanInfo.leftMarginPercent;


                if ((weightSpanInfo.leftMarginPercent + weightSpanInfo.rightMarginPercent) <= 1.0f) {
                    weightSpanTrack.setLayoutParams(weightSpanBarParams);
                    weightSpanLeftThumb.setEnabled(true);
                    maxWeight = (int) (PriveTalkConstants.MAXIMUM_WEIGHT - ((PriveTalkConstants.MAXIMUM_WEIGHT - PriveTalkConstants.MINIMUM_WEIGHT) * weightSpanInfo.rightMarginPercent));
                    setWeight();
                } else {
                    weightSpanLeftThumb.setEnabled(false);
                }
            }

            @Override
            public void OnRelease(View view) {
                myScrollView.setScrollEnabled(true);
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
            }
        });


        /*
        ALL SPINNER DIALOGS
         */

        //get views
        bodyType = (SearchFilterContainer) rootView.findViewById(R.id.bodyType);
        bodyType.setTitle(R.string.body_type_filter);
        hairColor = (SearchFilterContainer) rootView.findViewById(R.id.hairColor);
        hairColor.setTitle(R.string.hair_colour);
        eyeColor = (SearchFilterContainer) rootView.findViewById(R.id.eyesColor);
        eyeColor.setTitle(R.string.eyes_colour);
        educationLevel = (SearchFilterContainer) rootView.findViewById(R.id.educationLevel);
        educationLevel.setTitle(R.string.education_level);
        smokingHabit = (SearchFilterContainer) rootView.findViewById(R.id.smokingHabit);
        smokingHabit.setTitle(R.string.smoking_habit);
        drinkingHabbit = (SearchFilterContainer) rootView.findViewById(R.id.drinkinHabit);
        drinkingHabbit.setTitle(R.string.drinking_habit);
        zodiascSign = (SearchFilterContainer) rootView.findViewById(R.id.zodiacSign);
        zodiascSign.setTitle(R.string.zodiac_sign);
        faith = (SearchFilterContainer) rootView.findViewById(R.id.addFaith);
        faith.setTitle(R.string.faith_bold);

        if (!currentUser.royal_user)
            bodyType.setOnClickListener(onClickListener);
        else
            bodyType.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.BODY_TYPES], "")))
                            .setTitle(getString(R.string.pick_body_type))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    bodyType.setSelectedFilter(position);
                                    bodyType.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });

        if (!currentUser.royal_user)
            hairColor.setOnClickListener(onClickListener);
        else
            hairColor.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.HAIR_COLORS], "")))
                            .setTitle(getString(R.string.colour_of_hair))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    hairColor.setSelectedFilter(position);
                                    hairColor.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });


        if (!currentUser.royal_user)
            eyeColor.setOnClickListener(onClickListener);
        else
            eyeColor.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.EYE_COLOR], "")))
                            .setTitle(getString(R.string.colour_of_eyes2))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    eyeColor.setSelectedFilter(position);
                                    eyeColor.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });


        if (!currentUser.royal_user)
            educationLevel.setOnClickListener(onClickListener);
        else
            educationLevel.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.EDUCATION_LEVEL], "")))
                            .setTitle(getString(R.string.education_level2))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    educationLevel.setSelectedFilter(position);
                                    educationLevel.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });

        if (!currentUser.royal_user)
            smokingHabit.setOnClickListener(onClickListener);
        else
            smokingHabit.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.SMOKING_HABITS], "")))
                            .setTitle(getString(R.string.smoking_habit2))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    smokingHabit.setSelectedFilter(position);
                                    smokingHabit.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });

        if (!currentUser.royal_user)
            drinkingHabbit.setOnClickListener(onClickListener);
        else
            drinkingHabbit.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.DRINKING_HABITS], "")))
                            .setTitle(getString(R.string.drinkin_habit))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    drinkingHabbit.setSelectedFilter(position);
                                    drinkingHabbit.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });

        if (!currentUser.royal_user)
            zodiascSign.setOnClickListener(onClickListener);
        else
            zodiascSign.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.ZODIAC_SIGNS], "")))
                            .setTitle(getString(R.string.zodiac_sign2))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    zodiascSign.setSelectedFilter(position);
                                    zodiascSign.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });


        if (!currentUser.royal_user)
            faith.setOnClickListener(onClickListener);
        else
            faith.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setSelectionArray(dialogValues = getArrayFromAttrList(AttributesDatasource.getInstance(getContext()).
                                    getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.FAITH], "")))
                            .setTitle(getString(R.string.faith))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    faith.setSelectedFilter(position);
                                    faith.setFilterText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            });


        addLanguage = (LinearLayout) rootView.findViewById(R.id.addLanguage);
        addLanguageIcon = rootView.findViewById(R.id.addLanguageIcon);

        if (!currentUser.royal_user)
            addLanguage.setOnClickListener(onClickListener);
        else
            addLanguageIcon.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    final String[] temp = new String[1];
                    final String[] tempType = new String[1];
                    final ArrayList<AttributesObject> langsList = AttributesDatasource.getInstance(getContext()).
                            getAttributeObjectList(PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.LANGUAGES], "");

                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setTitle(getString(R.string.pick_language))
                            .setSelectionArray(dialogValues = getArrayFromAttrList(langsList))
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    temp[0] = dialogValues[position - 1];
                                    tempType[0] = langsList.get(position - 1).value;
                                    addNewLanguage(addLanguage, temp[0], tempType[0]);
                                }
                            }).show();
                }
            });


        addInterest = (LinearLayout) rootView.findViewById(R.id.addInterest);
        addInterstIcon = rootView.findViewById(R.id.addInterestIcon);
        if (!currentUser.royal_user)
            addInterest.setOnClickListener(onClickListener);
        else
            addInterstIcon.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    final String[] temp = new String[1];
                    final String[] selection = new String[1];

                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setTitle(getString(R.string.choose_interest))
                            .setSelectionArray(interests)
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    temp[0] = interests[position - 1];
                                    switch (position) {
                                        case 1:
                                            selection[0] = PriveTalkConstants.Interests.ACTIVITY;
                                            break;
                                        case 2:
                                            selection[0] = PriveTalkConstants.Interests.LITERATURE;
                                            break;
                                        case 3:
                                            selection[0] = PriveTalkConstants.Interests.MUSIC;
                                            break;
                                        case 4:
                                            selection[0] = PriveTalkConstants.Interests.PLACES;
                                            break;
                                        case 5:
                                            selection[0] = PriveTalkConstants.Interests.MOVIES;
                                            break;
                                    }
                                }
                            }).setOnEndAnimationListener(new PriveTalkPickerDialog.OnAnimationEndListener() {
                        @Override
                        public void withEndAnimation() {
                            dialogValues = getArrayFromInterestList(InterestsDatasource.getInstance(getContext()).getAllInterestsForTypeNotUserAdded(selection[0], ""));

                            if (dialogValues.length > 0) {
                                new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                                        .setSelectionArray(dialogValues)
                                        .setTitle(getString(R.string.chosen_level))
                                        .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                            @Override
                                            public void onDonePressed(int position) {
                                                addNewInterest(addInterest, temp[0], getString(R.string.choose_interest), dialogValues[position - 1], selection[0]);
                                            }
                                        }).show();
                            }
                        }
                    }).show();
                }
            });


        /*
        LOCATION & GPS BUTTON
         */
        gpsButtonContainer = (ViewSwitcher) rootView.findViewById(R.id.gpsButton);
        gpsButton = gpsButtonContainer.getChildAt(BUTTON);
        searchLocation = (PriveTalkEditText) rootView.findViewById(R.id.searchLocation);
        searchLocation.setFocusable(false);
        searchLocation.setFocusableInTouchMode(false);
        searchLocation.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(view.getContext(), true, PriveTalkConstants.MAP_FRAGMENT_ID);
            }
        });

        gpsButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!network_enabled && !gps_enabled) {
                    buildAlertMessageNoGps();
                } else {
                    gpsButtonContainer.setDisplayedChild(PROGRESS);
                    if (mGoogleApiClient.isConnected()) getLocationName();
                    else mGoogleApiClient.connect();
                }
            }
        });
    }


    //method to add new view in LinearLayout
    private void addNewLanguage(LinearLayout parentView, String title, String tempType) {
        final View newRow = LayoutInflater.from(getContext()).inflate(R.layout.add_language_or_faith_view, parentView, false);

        ((PriveTalkTextView) newRow.findViewById(R.id.newRowTitle)).setText(title);

        newRow.findViewById(R.id.options).setVisibility(View.GONE);

        parentView.addView(newRow, parentView.getChildCount());

        newRow.setTag(R.id.lang_tag, tempType);

        newRow.findViewById(R.id.removeView).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(final View view, MotionEvent event) {
                addLanguage.removeView(newRow);
            }
        });
    }


    //method to add new view in LinearLayout
    private void addNewInterest(LinearLayout parentView, String title, final String subDialogTitle, final String level, String selection) {
        final View newRow = LayoutInflater.from(getContext()).inflate(R.layout.add_language_or_faith_view, parentView, false);

        ((PriveTalkTextView) newRow.findViewById(R.id.newRowTitle)).setText(title);
        ((PriveTalkTextView) newRow.findViewById(R.id.newRowLevel)).setText(level);
        newRow.findViewById(R.id.options).setTag(R.id.interest_type_tag, selection);
        newRow.findViewById(R.id.options).setVisibility(View.VISIBLE);

        parentView.addView(newRow, parentView.getChildCount());

        newRow.findViewById(R.id.options).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(final View view, MotionEvent event) {
                dialogValues = getArrayFromInterestList(InterestsDatasource.getInstance(getContext()).getAllInterestsForType((String) view.getTag(R.id.interest_type_tag), ""));
                if (dialogValues.length > 0) {
                    new PriveTalkPickerDialog(getActivity(), (RelativeLayout) rootView)
                            .setTitle(subDialogTitle)
                            .setSelectionArray(dialogValues)
                            .setActionDoneListener(new PriveTalkPickerDialog.ActionDoneListener() {
                                @Override
                                public void onDonePressed(int position) {
                                    ((PriveTalkTextView) view.findViewById(R.id.newRowLevel)).setText(dialogValues[position - 1]);
                                }
                            }).show();
                }
            }
        });

        newRow.findViewById(R.id.removeView).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                addInterest.removeView(newRow);
            }
        });
    }


    /*
    Fixed size values used in layout. Used for animation calculations
     */
    private void getValues() {
        //values for distance Seek Bar
        distanceTrackLength = getResources().getDimensionPixelSize(R.dimen.distance_bar_width) - getResources().getDimensionPixelSize(R.dimen.distance_margin);
        distanceParentLayoutWidth = getResources().getDimensionPixelSize(R.dimen.distance_bar_width);
        distanceParentLayoutLeft = getResources().getDimensionPixelSize(R.dimen.search_filter_padding);
//        distanceParentRatio = (distanceTrackLength / distanceParentLayoutWidth);
        distanceParentRatio = 1;


        //values for ageSpan Seek Bar
        ageSpanParentLayout.post(new Runnable() {
            @Override
            public void run() {
                ageSpanParentLayoutWidth = ageSpanParentLayout.getWidth();
            }
        });
        minAge = PriveTalkConstants.MINIMUM_AGE;
        maxAge = PriveTalkConstants.MAXIMUM_AGE;

        //values for Height Seek bar
        maxHeight = PriveTalkConstants.MAXIMUM_HEIGHT;
        minHeight = PriveTalkConstants.MINIMUM_HEGHT;

        //values for Weight Seek bar
        minWeight = PriveTalkConstants.MINIMUM_WEIGHT;
        maxWeight = PriveTalkConstants.MAXIMUM_WEIGHT;
    }


    //a dialog informing user that gps is not enabled and drives user to gps/location settings
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.enable_gps_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    //get location name by coordinates
    private void getLocationName() {

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (mLastLocation != null) {
                    String locationToPrint = "";
                    float longitude = (float) mLastLocation.getLongitude();
                    float latitude = (float) mLastLocation.getLatitude();

                    LocationUtilities.saveSelectedLocation(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());

                    try {
                        List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 10);
                        if (null != listAddresses && listAddresses.size() > 0) {

                            LocationUtilities.setSelectedCountryCode(listAddresses.get(0).getCountryCode());

                            for (Address address : listAddresses) {
                                String adminArea = address.getAdminArea();
                                String countryName = address.getCountryName();
                                String cityNameForUSA = address.getLocality();

                                if (countryName.equals("United States") || address.getCountryCode().equals("US")) {
                                    Log.d("USA", "USA");

                                    if (cityNameForUSA != null) {
                                        return cityNameForUSA;
                                    }
                                }

                                if (adminArea != null) {
                                    return adminArea;
                                } else if (countryName != null) {
                                    if (countryName.equals("United States") || address.getCountryCode().equals("US"))
                                        locationToPrint = cityNameForUSA;
                                    else
                                        locationToPrint = countryName;
                                }
                            }
                            return locationToPrint;
                        }
                    } catch (IOException e) {
//                        getLocationName();
                        e.printStackTrace();
                    }
                    Log.d("testLocation", " longitude " + longitude);
                    Log.d("testLocation", " latitude " + latitude);
                    Log.d("testLocation", " locationToPrint " + locationToPrint);
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                gpsButtonContainer.setDisplayedChild(BUTTON);
                if (searchLocation == null)
                    return;
                searchLocation.setText(s);
            }
        }.execute();
    }


    /*
    Create a new jsonobject depending on user status (royal/not royal)
    save object(search filter) to preferences and close fragment
     */
    private void searchCommunity() {
        JSONObject obj = new JSONObject();

        try {
            if (((boolean) maleIcon.getTag() && (boolean) femaleIcon.getTag()) ||
                    (!(boolean) maleIcon.getTag() && !(boolean) femaleIcon.getTag()))
                obj.put("looking_for", 0);
            else
                obj.put("looking_for", ((boolean) maleIcon.getTag()) ? 1 : 2);


            boolean isLocationSet = LocationUtilities.getSelectedLongitude() != 0f && LocationUtilities.getSelectedLatitude() != 0f;

            JSONObject location = new JSONObject();

            String lng = String.valueOf(LocationUtilities.getSelectedLongitude());
            String lat = String.valueOf(LocationUtilities.getSelectedLatitude());

            try {
                lng = (String.format(Locale.UK, "%.8f", lng));
                lat = (String.format(Locale.UK, "%.8f", lat));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("Community", " long " + lng);
            Log.d("Community", " lat " + lat);

            String iso = LocationUtilities.getSelectedCountryCode();

            if (iso == null || iso.isEmpty())
                iso = LocationUtilities.getCountryCode();

            location.put("longitude", isLocationSet ? lng : LocationUtilities.getLongitude());
            location.put("latitude", isLocationSet ? lat : LocationUtilities.getLatitude());
            location.put("iso_country_code", iso );
            location.put("radius", radius);


            Log.d("Community", location.toString());
            obj.put("location", location);

            JSONObject age = new JSONObject();
            age.put("min_age", minAge);
            age.put("max_age", maxAge);

            obj.put("age", age);

            if (currentUser.royal_user) {
                JSONObject height = new JSONObject();
                height.put("min_height", minHeight);
                height.put("max_height", maxHeight);
                if (minHeight != PriveTalkConstants.MINIMUM_HEGHT || maxHeight != PriveTalkConstants.MAXIMUM_HEIGHT)
                    obj.put("height", height);

                JSONObject weight = new JSONObject();
                weight.put("min_weight", minWeight);
                weight.put("max_weight", maxWeight);
                if (minWeight != PriveTalkConstants.MINIMUM_WEIGHT || maxWeight != PriveTalkConstants.MAXIMUM_WEIGHT)
                    obj.put("weight", weight);

                if (bodyType.selectedFilter != -1)
                    obj.put("body_type", bodyType.selectedFilter);
                if (hairColor.selectedFilter != -1)
                    obj.put("hair_color", hairColor.selectedFilter);
                if (eyeColor.selectedFilter != -1)
                    obj.put("eyes_color", eyeColor.selectedFilter);
                if (educationLevel.selectedFilter != -1)
                    obj.put("education_status", educationLevel.selectedFilter);
                if (smokingHabit.selectedFilter != -1)
                    obj.put("smoking_status", smokingHabit.selectedFilter);
                if (drinkingHabbit.selectedFilter != -1)
                    obj.put("drinking_status", drinkingHabbit.selectedFilter);
                if (zodiascSign.selectedFilter != -1)
                    obj.put("zodiac_sign", zodiascSign.selectedFilter);

                JSONObject religion = new JSONObject();

                if (faith.selectedFilter != -1) {
                    religion.put("religion", faith.selectedFilter);
                    obj.put("faith", religion);
                }

//                System.out.println("Faith obj: " + faith);

                JSONArray languages = new JSONArray();

                if (addLanguage.getChildCount() > 1) {
                    for (int i = 1; i < addLanguage.getChildCount(); i++) {
                        JSONObject lang = new JSONObject();
                        if (addLanguage.getChildAt(i).getTag(R.id.lang_tag) != null) {
                            lang.put("language", (String) addLanguage.getChildAt(i).getTag(R.id.lang_tag));
                            languages.put(lang);
                        }
                    }
                    obj.put("languages", languages);
                }

                JSONArray interests = new JSONArray();

                if (addInterest.getChildCount() > 1) {
                    for (int i = 1; i < addInterest.getChildCount(); i++) {
                        JSONObject inter = new JSONObject();
                        if (addInterest.getChildAt(i).findViewById(R.id.options).getTag(R.id.interest_type_tag) != null) {
                            inter.put("type", (String) addInterest.getChildAt(i).findViewById(R.id.options).getTag(R.id.interest_type_tag));
                            inter.put("title", ((PriveTalkTextView) addInterest.getChildAt(i).findViewById(R.id.newRowLevel)).getText().toString());
                            interests.put(inter);
                        }
                    }
                    obj.put("interests", interests);
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

//        System.out.println("The json obj: " + obj);

        getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putString(PriveTalkConstants.KEY_SEARCH_FILTER, obj.toString()).apply();
        getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(PriveTalkConstants.KEY_SHOULD_UPDATE_COMMUNITY, true).apply();
        getActivity().onBackPressed();
    }


    //get string array from attributes list
    private String[] getArrayFromAttrList(ArrayList<AttributesObject> objects) {
        String[] result = new String[objects.size()];

        int i = 0;
        for (AttributesObject object : objects) {
            result[i] = object.display;
            i++;
        }

        return result;
    }

    private String[] getArrayFromInterestList(ArrayList<InterestObject> objects) {

        String[] result = new String[objects.size()];

        int i = 0;
        for (InterestObject object : objects) {
            result[i++] = object.title;
        }

        return result;
    }


    @Override
    public void onPause() {
        super.onPause();

        if (getLocationAdditionalInfo != null)
            getLocationAdditionalInfo.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAdditionalInfo();
    }

    //implemented methods for location services
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        getLocationName();
    }

    private void maleSelection(boolean selection) {
        if ((!selection && !(boolean) femaleIcon.getTag())) {
            Toast.makeText(getContext(), getString(R.string.must_select_one_gender), Toast.LENGTH_SHORT).show();
            return;
        }
        maleIcon.setTag(selection);
        maleIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), selection ? R.drawable.male_icon_selected : R.drawable.male_icon));
    }

    private void femaleSelection(boolean selection) {
        if ((!selection && !(boolean) maleIcon.getTag())) {
            Toast.makeText(getContext(), getString(R.string.must_select_one_gender), Toast.LENGTH_SHORT).show();
            return;
        }
        femaleIcon.setTag(selection);
        femaleIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), selection ? R.drawable.female_icon_selected : R.drawable.female_icon));
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void getAdditionalInfo() {

        getLocationAdditionalInfo = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String string) {
                searchLocation.setText(string);
            }

            @Override
            protected String doInBackground(Void... params) {

                float lat = LocationUtilities.getSelectedLatitude();
                float lng = LocationUtilities.getSelectedLongitude();
                String locationToPrint = "";

                if (lat == 0 && lng == 0)
                    return "";

                Geocoder geocoder = new Geocoder(PriveTalkApplication.getInstance(), Locale.getDefault());

                // lat,lng, your current location
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(lat, lng, 10);
                    if (null != listAddresses && listAddresses.size() > 0) {
                        LocationUtilities.saveSelectedLocation(lat, lng);
                        LocationUtilities.setSelectedCountryCode(listAddresses.get(0).getCountryCode());
                        Log.d("CommunityJSON", " " + listAddresses.get(0).getCountryCode());

                        for (Address address : listAddresses) {

                            String adminArea = address.getAdminArea();
                            String countryName = address.getCountryName();
                            String cityNameForUSA = address.getLocality();

                            if (countryName.equals("United States") || address.getCountryCode().equals("US")) {
                                Log.d("USA", "USA");

                                if (cityNameForUSA != null) {
                                    return cityNameForUSA;
                                }
                            }

                            if (adminArea != null) {
                                return adminArea;
                            } else if (countryName != null) {
                                if (countryName.equals("United States") || address.getCountryCode().equals("US"))
                                    locationToPrint = cityNameForUSA;
                                else
                                    locationToPrint = countryName;
                            }
                        }
                        return locationToPrint;
                    }
                } catch (IOException e) {
//                        getLocationName();
                    e.printStackTrace();
                }

                return "";
            }
        }.execute();
    }


    //use this to update text for weight
    private void setWeight() {
        searchWeight.setText(getString(R.string.weight_from) + "{b} "
                + Math.round(minWeight * PriveTalkConstants.ONE_KILOGRAM_TO_LBS) + " lb " + "(" + minWeight + " kg" + ")" + "{/b} "
                + getString(R.string.age_to) + " {b}"
                + Math.round(maxWeight * PriveTalkConstants.ONE_KILOGRAM_TO_LBS) + " lb " + "(" + maxWeight + " kg" + ")" + "{/b}"
                + (maxWeight == PriveTalkConstants.MAXIMUM_WEIGHT ? "+" : ""));
    }

    //use this to update text for height
    private void setHeight() {
        searchHeight.setText(getString(R.string.height_from) + "{b} "
                + PriveTalkUtilities.centimetersToFeetAndInches(minHeight) + " (" + minHeight + " cm" + ")" + "{/b} "
                + getString(R.string.age_to) + " {b}"
                + PriveTalkUtilities.centimetersToFeetAndInches(maxHeight) + " (" + maxHeight + " cm" + ")" + "{/b}"
                + (maxHeight == PriveTalkConstants.MAXIMUM_HEIGHT ? "+" : ""));
    }

    //use this to update text for age
    private void setAge() {
        searchAges.setText(getString(R.string.age_from) + "{b} "
                + String.valueOf(minAge) + "{/b} "
                + getString(R.string.age_to) + " {b}"
                + String.valueOf(maxAge) + "{/b}"
                + (maxAge == PriveTalkConstants.MAXIMUM_AGE ? "+" : ""));
    }

}
