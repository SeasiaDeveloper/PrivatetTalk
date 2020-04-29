package com.privetalk.app.mainflow.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.isseiaoki.simplecropview.CropImageView;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.database.objects.LanguageObject;
import com.privetalk.app.mainflow.activities.CameraViewActivity;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.activities.TransparentActivity;
import com.privetalk.app.mainflow.fragments.profile_edit.AboutMeEditFragment;
import com.privetalk.app.mainflow.fragments.profile_edit.PersonalInfoEditParentFragment;
import com.privetalk.app.utilities.BitmapUtilities;
import com.privetalk.app.utilities.DrawerUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FlameImageView;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.ProfileAwardsContainer;
import com.privetalk.app.utilities.ProfilePersonalInfoContainer;
import com.privetalk.app.utilities.SmoothScrollLinearLayoutManager;
import com.privetalk.app.utilities.VerificationTickView;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.privetalk.app.mainflow.activities.MainActivity.ADD_TO_BACKSTUCK;
import static com.privetalk.app.mainflow.activities.MainActivity.BROADCAST_CHANGE_DRAWER_FRAGMENT;
import static com.privetalk.app.mainflow.activities.MainActivity.BROADCAST_CHANGE_FRAGMENT;
import static com.privetalk.app.mainflow.activities.MainActivity.FRAGMENT_TO_CHANGE;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class ProfileFragment extends FragmentWithTitle {

    private static final SimpleDateFormat fromFormat = new SimpleDateFormat("dd, MMM yyyy - HH:mm:ssz", Locale.US);
    private static final SimpleDateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss", Locale.US);
    private static final String PREFER_NOT_TO_SAY = "17";

    private ViewSwitcher rootView;
    private View editProfileView;
    private RecyclerView photosRecyclerView;
    private LinearLayout.LayoutParams photosRecyclerViewParams;
    private SmallPhotosAdapter smallPhotosAdapter;
    private ProfilePhotoAdapter profilePhotoAdapter;
    private Handler mHadler;
    private boolean fragmentPaused = true;

    private View progressBar;
    private boolean hiddenModeChanged = false;
    private ViewPager photoViewPager;
    private LayoutInflater inflater;
    private String title;

    private CurrentUser currentUser;

    private TextView coinsTextView;
    private TextView nameTextView;
    private TextView ageTextView;
    private View hotWheelRightClicks;
    private VerificationTickView verificationProfilePictureTick;
    private VerificationTickView verificationSocialMediaTick;
    private VerificationTickView verificationRoyalUserTick;

    private ProfilePersonalInfoContainer locationContainer;
    private ProfilePersonalInfoContainer ageContainer;
    private ProfilePersonalInfoContainer relationshipContainer;
    private ProfilePersonalInfoContainer sexualityContainer;
    private ProfilePersonalInfoContainer heightContainer;
    private ProfilePersonalInfoContainer weightContainer;
    private ProfilePersonalInfoContainer hairCointainer;
    private ProfilePersonalInfoContainer eyesContainer;
    private ProfilePersonalInfoContainer bodyTypeContainer;
    private ProfilePersonalInfoContainer zodiacContainer;
    private ProfilePersonalInfoContainer smokingContainer;
    private ProfilePersonalInfoContainer drinkingContainer;
    private ProfilePersonalInfoContainer educationContainer;
    private ProfilePersonalInfoContainer workContainer;
    private ProfilePersonalInfoContainer languagesContainer;
    private ProfilePersonalInfoContainer religionContainer;

    private ProfilePersonalInfoContainer activitiesContainer;
    private ProfilePersonalInfoContainer musicContainer;
    private ProfilePersonalInfoContainer moviesContainer;
    private ProfilePersonalInfoContainer literatureContainer;
    private ProfilePersonalInfoContainer placesContainer;

    private ProfileAwardsContainer popularAwardsContainer;
    private ProfileAwardsContainer friendlyAwardsContainer;
    private ProfileAwardsContainer iceBreakerAwardsContainer;
    private ProfileAwardsContainer impressionVoterAwardsContainer;
    private ProfileAwardsContainer angelsAwardsContainer;
    private PriveTalkTextView awardsThisWeeksFreeCoins;
    private PriveTalkTextView popularAwardsThisWeekPercentage;
    private ProgressBar purchaseBonusProgressBar;
    private PriveTalkTextView lastWeeksPurchaseBonus;
    private ProgressBar lastWeeksPurchaseBonusProgress;
    private View usePurchaseButtonButton;
    private ImageView visibleInvisibleButton;
    private ImageView becomeRoyalUserButon;
    private FlameImageView flameImageView;
    private ScrollView parentScrollView;
    private ArrayList<CurrentUserPhotoObject> currentUserPhotoObjectArrayList;
    private PriveTalkEditText privetalkEditText;
    private View aboutMeButton;
    private PriveTalkTextView lastLoginTextView;

    private int imageCenterX;
    private int actionBarAndStatusBarSize;
    private int blueCircleWidth;
    private int imageRadius;
    private int pagerMargin;
    private String imgAbsolutePath = null;
    private int colorGreen;
    private boolean shouldDisableProgressBar;

    private ImageView freeCoinsGreek, freeCoins, bonusGreek, bonus, bonusEftomas, bonusEftomasGreek, coinsImageView;

    private BroadcastReceiver profilePhotosReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra(PriveTalkConstants.KEY_FAIL, false))
                loadPhotos();
        }
    };


    private BroadcastReceiver photoUploadListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            else
                shouldDisableProgressBar = true;
        }
    };

    private BroadcastReceiver currentUserUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("PROFILE FRAGMENT", "Current user updated");
            currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
            if (currentUser == null)
                return;
            currentUser.currentUserDetails = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails();
            loadDataToViews();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHadler = new Handler();

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }

        currentUserPhotoObjectArrayList = new ArrayList<>();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageCenterX = size.x / 2;

        Rect r = new Rect();
        View myView = getActivity().getWindow().getDecorView();
        myView.getWindowVisibleDisplayFrame(r);

        actionBarAndStatusBarSize = getResources().getDimensionPixelSize(R.dimen.action_bar) + r.top;

        blueCircleWidth = getResources().getDimensionPixelOffset(R.dimen.blue_circle_border_width);

        imageRadius = getResources().getDimensionPixelSize(R.dimen.photo_view_pager_width) / 2;
        pagerMargin = getResources().getDimensionPixelSize(R.dimen.photo_view_pager_width_top_margin);

        Log.d("PROFILE: ", "ON CREATE");

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
        currentUser.currentUserDetails = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails();
        ProfilePersonalInfoContainer.inflaterCounter = 0;
        rootView = (ViewSwitcher) inflater.inflate(R.layout.fragment_profile, container, false);

        if (savedInstanceState != null) {
            imgAbsolutePath = savedInstanceState.getString(PriveTalkConstants.KEY_IMAGE_ABSOLUTE_PATH);
            if (imgAbsolutePath != null) {
                showCropImageLayout(imgAbsolutePath);
                imgAbsolutePath = null;
            }
        }

        freeCoins = (ImageView) rootView.findViewById(R.id.free_coins);
        freeCoinsGreek = (ImageView) rootView.findViewById(R.id.free_coins_greek);
        bonus = (ImageView) rootView.findViewById(R.id.bonus);
        bonusGreek = (ImageView) rootView.findViewById(R.id.bonus_greek);
        bonusEftomas = (ImageView) rootView.findViewById(R.id.bonus_eftomas);
        bonusEftomasGreek = (ImageView) rootView.findViewById(R.id.bonus_eftomas_greek);

        if (Locale.getDefault().getLanguage().equals("el")) {
            freeCoins.setVisibility(View.GONE);
            freeCoinsGreek.setVisibility(View.VISIBLE);
            bonus.setVisibility(View.GONE);
            bonusGreek.setVisibility(View.VISIBLE);
            bonusEftomas.setVisibility(View.GONE);
            bonusEftomasGreek.setVisibility(View.VISIBLE);
        } else {
            freeCoins.setVisibility(View.VISIBLE);
            freeCoinsGreek.setVisibility(View.GONE);
            bonus.setVisibility(View.VISIBLE);
            bonusGreek.setVisibility(View.GONE);
            bonusEftomas.setVisibility(View.VISIBLE);
            bonusEftomasGreek.setVisibility(View.GONE);
        }

        initViews();
        PriveTalkUtilities.getAttributes();
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(currentUserUpdated, new IntentFilter(PriveTalkConstants.BROADCAST_CURRENT_USER_UPDATED));
        mHadler.post(new Runnable() {
            @Override
            public void run() {
                loadDataToViews();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(PriveTalkConstants.BROADCAST_PHOTOS_DOWNLOADED);
        intentFilter.addAction(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED);
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(profilePhotosReceiver, intentFilter);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(photoUploadListener, new IntentFilter(PriveTalkConstants.BROADCAST_PHOTO_UPLOADED));
    }

    @Override
    public void onResume() {
        PersonalInfoEditParentFragment.infoChanged = false;
        AboutMeEditFragment.infochanged = false;
        fragmentPaused = false;
        loadPhotos();
        super.onResume();
        parentScrollView.smoothScrollTo(0, 0);
        if (shouldDisableProgressBar) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            shouldDisableProgressBar = false;
        }
        if (shouldShowHowToVerifyAlert())
            showHowToVerifyAlert();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHadler != null)
            mHadler.removeCallbacksAndMessages(null);
        fragmentPaused = true;
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(profilePhotosReceiver);
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(photoUploadListener);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(currentUserUpdated);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PriveTalkConstants.ACTION_BAR_TITLE, title);
        outState.putString(PriveTalkConstants.KEY_IMAGE_ABSOLUTE_PATH, imgAbsolutePath);
        super.onSaveInstanceState(outState);
    }


    /*
    returns true only the first time the user visits his profile (profile fragment)
     */
    private boolean shouldShowHowToVerifyAlert() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(PriveTalkConstants.KEY_SHOW_HOW_TO_VERIFY, true);
    }

    /*
    disables how to verify alert
     */
    private void disableShowHowToVerifyAlert() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PriveTalkConstants.KEY_SHOW_HOW_TO_VERIFY, false).apply();
    }


    /*
    shows alert dialog to  user with instructions how to verify profile. when ok button is pressed the alert is disabled (disableShowHowToVerifyAlert())
     */
    private void showHowToVerifyAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.how_to_verify_your_profile)
                .setMessage(R.string.tap_the_three_chekmarks_verification_message)
                .setCancelable(false)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        disableShowHowToVerifyAlert();
                        showVerificationsPopUp();
                    }
                })
                .create().show();
    }


    private void initViews() {
        progressBar = rootView.findViewById(R.id.progressBar);

        parentScrollView = (ScrollView) rootView.findViewById(R.id.parentScrollView);

        editProfileView = rootView.findViewById(R.id.profileTopEdit);

        editProfileView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                Bundle bundle = new Bundle();
                bundle.putSerializable(CurrentUser.class.getName(), currentUser);

                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.PERSONALEDIT_FRAGMENT_ID, PersonalInfoEditParentFragment.PARENT_TYPE_PROFILE_EDIT, bundle);

            }
        });

        rootView.findViewById(R.id.profileBottomEdit).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
//                interestWasOpen = true;
                Bundle bundle = new Bundle();
                bundle.putSerializable(CurrentUser.class.getName(), currentUser);
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.PERSONALEDIT_FRAGMENT_ID, PersonalInfoEditParentFragment.PARENT_TYPEACTIVITIES, bundle);
            }
        });

        photosRecyclerView = (RecyclerView) rootView.findViewById(R.id.smallPhotosRecyclerView);

        //remove animations when adding/removing items
        RecyclerView.ItemAnimator animator = photosRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        photosRecyclerViewParams = (LinearLayout.LayoutParams) photosRecyclerView.getLayoutParams();

        setPhotosRecyclerViewWidth();

        photosRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        photoViewPager = (ViewPager) rootView.findViewById(R.id.photoViewpager);
        photoViewPager.setAdapter(profilePhotoAdapter = new ProfilePhotoAdapter());
        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                photosRecyclerView.smoothScrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rootView.findViewById(R.id.profileSettings).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.SETTINGS_FRAGMENT);
            }
        });


        rootView.findViewById(R.id.addNewPersonalPhoto).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                EasyImage.openChooserWithGallery(ProfileFragment.this, getString(R.string.select_photo), 0);
            }
        });

        becomeRoyalUserButon = (ImageView) rootView.findViewById(R.id.becomeRoyalUserButton);
        coinsImageView = (ImageView) rootView.findViewById(R.id.profilePTicon);
        coinsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BROADCAST_CHANGE_DRAWER_FRAGMENT);
                intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, 12);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });

        becomeRoyalUserButon.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (currentUser.royal_user)
                    new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.already_royal_user))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                else
                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID);
            }
        });

        flameImageView = (FlameImageView) rootView.findViewById(R.id.flameImageView);
        flameImageView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                parentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });


        aboutMeButton = rootView.findViewById(R.id.profileEdittextEditButton);
        privetalkEditText = (PriveTalkEditText) rootView.findViewById(R.id.profileEditText);
        coinsTextView = (TextView) rootView.findViewById(R.id.profileNumberOfCoins);
        nameTextView = (TextView) rootView.findViewById(R.id.profileName);
        ageTextView = (TextView) rootView.findViewById(R.id.profileAge);
        lastLoginTextView = (PriveTalkTextView) rootView.findViewById(R.id.lastLogin);

        locationContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileLocationContainer);
        ageContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileAgeContainer);
        relationshipContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileRelationshipContainer);
        sexualityContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileSexualityContainer);
        heightContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileHeightContainer);
        weightContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileWeightContainer);
        hairCointainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileHairContainer);
        eyesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileEyesContainer);
        bodyTypeContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileBodyTypeContainer);
        zodiacContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileZodiacContainer);
        smokingContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileSmokingContainer);
        drinkingContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileDrinkingContainer);
        educationContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileEducationContainer);
        workContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileWorkContainer);
        languagesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileLanguageContainer);
        religionContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileReligionContainer);

        activitiesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileActivitiesContainer);
        musicContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileMusicContainer);
        moviesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileMoviesContainer);
        literatureContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileLiteratureContainer);
        placesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profilePlacesContainer);

        verificationProfilePictureTick = (VerificationTickView) rootView.findViewById(R.id.profilePictureVerificationTick);
        verificationSocialMediaTick = (VerificationTickView) rootView.findViewById(R.id.socialMediaVerificationTick);
        verificationRoyalUserTick = (VerificationTickView) rootView.findViewById(R.id.royalUserVerificationTick);

        awardsThisWeeksFreeCoins = (PriveTalkTextView) rootView.findViewById(R.id.freeCoinsText);
        popularAwardsThisWeekPercentage = (PriveTalkTextView) rootView.findViewById(R.id.purchaseBonusPercentage);
        purchaseBonusProgressBar = (ProgressBar) rootView.findViewById(R.id.purchaseBonusProgressBar);
        lastWeeksPurchaseBonus = (PriveTalkTextView) rootView.findViewById(R.id.lastWeekPurchaseBonusText);
        lastWeeksPurchaseBonusProgress = (ProgressBar) rootView.findViewById(R.id.latestPurchaseBonusProgressBar);

        popularAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.popularAwardsContainer);
        friendlyAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.friendlyAwardsContainer);
        iceBreakerAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.iceBreakerAwardsContainer);
        impressionVoterAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.impressionVoterAwardsContainer);
        angelsAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.angelAwardsContainer);

        visibleInvisibleButton = (ImageView) rootView.findViewById(R.id.visibleInvisibleButton);

        usePurchaseButtonButton = rootView.findViewById(R.id.purchaseBonusUse);
        usePurchaseButtonButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                Intent intent = new Intent(BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(FRAGMENT_TO_CHANGE, PriveTalkConstants.ADD_MORE_PT_COINS_FRAGMENT);
                intent.putExtra(ADD_TO_BACKSTUCK, true);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });

        hotWheelRightClicks = rootView.findViewById(R.id.hotWheelrightTicks);
        hotWheelRightClicks.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                showVerificationsPopUp();
            }
        });
    }


    /*
    call this method to show the pop up dialog with the users verifications
     */
    private void showVerificationsPopUp() {

        final View inflatedView = inflater.inflate(R.layout.layout_verification, null);

        final PopupWindow popupWindow = new PopupWindow(inflatedView,
                getResources().getDimensionPixelSize(R.dimen.popup_verification_width),
                getResources().getDimensionPixelSize(R.dimen.popup_verification_height),
                true);

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimationStyle);

        View closeButton = inflatedView.findViewById(R.id.verification_x_button);
        closeButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                popupWindow.dismiss();
            }
        });
        final VerificationTickView popupProfilePictureTick = (VerificationTickView) inflatedView.findViewById(R.id.verificationProfilePictureTick);
        final VerificationTickView popupSocialMediaTick = (VerificationTickView) inflatedView.findViewById(R.id.verificationSocialMediaTick);
        final VerificationTickView popupRoyalUserTick = (VerificationTickView) inflatedView.findViewById(R.id.verificationRoyalUserTick);

//                        popupProfilePictureTick.setEnabled(currentUser.photos_verified && CurrentUserPhotosDatasource.getInstance(getContext()).hasVerfiedPhoto());
        popupProfilePictureTick.setEnabled(currentUser.photos_verified);
        popupSocialMediaTick.setEnabled(currentUser.social_verified);
        popupRoyalUserTick.setEnabled(currentUser.royal_user);

        final View popupProfilePictureButton = inflatedView.findViewById(R.id.verififyProfilePictureButton);
        final View popupSocialMediaButton = inflatedView.findViewById(R.id.verificationSocialButton);
        final View popupRoyalUserButton = inflatedView.findViewById(R.id.verificationRoyalUserButton);

//                        popupProfilePictureButton.setEnabled(!(currentUser.photos_verified && CurrentUserPhotosDatasource.getInstance(getContext()).hasVerfiedPhoto()));
        popupProfilePictureButton.setEnabled(!currentUser.photos_verified);
        popupSocialMediaButton.setEnabled(!currentUser.social_verified); //!currentUser.social_verified
        popupRoyalUserButton.setEnabled(!currentUser.royal_user);

        FadeOnTouchListener listener = new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                if (view.equals(popupProfilePictureButton)) {
                    if (CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() == null) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.profile_picture_verification)
                                .setMessage(getString(R.string.must_upload_profile_picture))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        Intent intent = new Intent(getActivity(), CameraViewActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
                    }
                } else if (view.equals(popupSocialMediaButton)) {
                    Intent intent = new Intent(getActivity(), TransparentActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                } else if (view.equals(popupRoyalUserButton)) {
                    Intent intent = new Intent(BROADCAST_CHANGE_DRAWER_FRAGMENT);
                    intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.BE_A_ROYAL_USER.ordinal());

                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    popupWindow.dismiss();
                }
            }
        };

        popupProfilePictureButton.setOnTouchListener(listener);
        popupSocialMediaButton.setOnTouchListener(listener);
        popupRoyalUserButton.setOnTouchListener(listener);

        popupWindow.showAtLocation(rootView, Gravity.LEFT | Gravity.TOP,
                (hotWheelRightClicks.getRight() - getResources().getDimensionPixelSize(R.dimen.popup_verification_width)),
                (int) hotWheelRightClicks.getY());


    }


    private void loadDataToViews() {

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();

        if (fragmentPaused)
            return;

        if (hiddenModeChanged) {
            hiddenModeChanged = false;
            Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
            intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.SHOW_VISIBILITY_CHANGED);
            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
        }

        coinsTextView.setText(String.valueOf(currentUser.coins));

        Log.d("coinsofUserProfile", String.valueOf(currentUser.coins));

        nameTextView.setText(currentUser.name == null ? getString(R.string.not_yet_set) : currentUser.name.split(" ")[0]);
        ageTextView.setText(currentUser.birthday == 0 ? getString(R.string.not_yet_set) : PriveTalkUtilities.getAge(currentUser.birthday));
        locationContainer.setText(currentUser.location == null ? getString(R.string.not_yet_set) : currentUser.location);
        ageContainer.setText(currentUser.birthday == 0 ? getString(R.string.not_yet_set) : PriveTalkUtilities.getAge(currentUser.birthday));

        try {
            Date currentDate = fromFormat.parse(currentUser.lastLogin);
            lastLoginTextView.setText(toFormat.format(currentDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        colorGreen = ContextCompat.getColor(getContext(), R.color.tick_green);

        verificationProfilePictureTick.setColorFilter(currentUser.photos_verified ? colorGreen : Color.WHITE);
//        verificationProfilePictureTick.setColorFilter(currentUser.photos_verified && CurrentUserPhotosDatasource.getInstance(getContext()).hasVerfiedPhoto() ? colorGreen : Color.WHITE);
        verificationSocialMediaTick.setColorFilter(currentUser.social_verified ? colorGreen : Color.WHITE);
        verificationRoyalUserTick.setColorFilter(currentUser.royal_user ? colorGreen : Color.WHITE);

        awardsThisWeeksFreeCoins.setText(String.valueOf(currentUser.this_week_free_coins));
        popularAwardsThisWeekPercentage.setText(String.valueOf(currentUser.this_week_purchase_bonus) + "%");
        purchaseBonusProgressBar.setProgress(currentUser.this_week_purchase_bonus);

        lastWeeksPurchaseBonus.setText(String.valueOf(currentUser.last_week_purchase_bonus) + "%");
        lastWeeksPurchaseBonusProgress.setProgress(currentUser.last_week_purchase_bonus);

        flameImageView.changeHotness((float) currentUser.hotness_percentage);

        popularAwardsContainer.assignValues(currentUser, "popular_score");
        friendlyAwardsContainer.assignValues(currentUser, "friendly_score");
        iceBreakerAwardsContainer.assignValues(currentUser, "ice_breaker_score");
        impressionVoterAwardsContainer.assignValues(currentUser, "impression_voter_score");
        angelsAwardsContainer.assignValues(currentUser, "angel_score");

        aboutMeButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
//                aboutMeWasOpen = true;
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.ABOUT_ME_FRAGMENT_ID);
            }
        });

        if (currentUser.royal_user)
            becomeRoyalUserButon.setImageResource(R.drawable.is_royal_user);

        visibleInvisibleButton.setImageResource(currentUser.hidden_mode_on ? R.drawable.invisible_button : R.drawable.visible_icon);

        visibleInvisibleButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                if (currentUser.hidden_mode_on) {
                    setVisibilityRequest(true);
                } else {
                    if (currentUser.royal_user) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.hide_profile))
                                .setMessage(R.string.hide_profile_message)
                                .setPositiveButton(getString(R.string.okay).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setVisibilityRequest(currentUser.hidden_mode_on);

                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel).toUpperCase(), null)
                                .create().show();

                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.not_royal_user))
                                .setMessage(R.string.royal_user_plans_hidden_community)
                                .setPositiveButton(getString(R.string.yes_string).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID);
                                    }
                                })
                                .setNegativeButton(getString(R.string.later).toUpperCase(), null)
                                .create().show();
                    }
                }
            }
        });


        if (currentUser.currentUserDetails != null) {
            if (currentUser.currentUserDetails.relationship_status != null)
                relationshipContainer.setText(String.valueOf(currentUser.currentUserDetails.relationship_status.display));
            if (currentUser.currentUserDetails.sexuality_status != null)
                sexualityContainer.setText(String.valueOf(currentUser.currentUserDetails.sexuality_status.display));
            if (currentUser.currentUserDetails.height != 0) {
                String heightDisplay = PriveTalkUtilities.centimetersToFeetAndInches(currentUser.currentUserDetails.height) + " (" + currentUser.currentUserDetails.height + " cm" + ")";
                heightContainer.setText(heightDisplay);
            }

            if (currentUser.currentUserDetails.weight != 0) {
                String weightDisplay = (int) (currentUser.currentUserDetails.weight * 2.2f) + " lb " + "(" + currentUser.currentUserDetails.weight + " kg" + ")";
                weightContainer.setText(weightDisplay);
            }
            if (currentUser.currentUserDetails.hair_color != null)
                hairCointainer.setText(currentUser.currentUserDetails.hair_color.display);
            if (currentUser.currentUserDetails.eyes_color != null)
                eyesContainer.setText(currentUser.currentUserDetails.eyes_color.display);
            if (currentUser.currentUserDetails.body_type != null)
                bodyTypeContainer.setText(currentUser.currentUserDetails.body_type.display);
            if (currentUser.currentUserDetails.zodiac != null) {
                zodiacContainer.setText(currentUser.currentUserDetails.zodiac.display);
                zodiacContainer.setImageResource(PriveTalkUtilities.getZodiacResourceId(Integer.parseInt(currentUser.currentUserDetails.zodiac.value)));
            }
            if (currentUser.currentUserDetails.smoking_status != null)
                smokingContainer.setText(currentUser.currentUserDetails.smoking_status.display);
            if (currentUser.currentUserDetails.drinking_status != null)
                drinkingContainer.setText(currentUser.currentUserDetails.drinking_status.display);
            if (currentUser.currentUserDetails.education_status != null)
                educationContainer.setText(currentUser.currentUserDetails.education_status.display);
//            if (currentUser.currentUserDetails.work != null)
//                workContainer.setText(currentUser.currentUserDetails.work.title);
            if (currentUser.currentUserDetails.faith != null) {
                religionContainer.setText(currentUser.currentUserDetails.faith.religion.display +
                        (currentUser.currentUserDetails.faith.religion.value.equals(PREFER_NOT_TO_SAY) ? "" :
                                "(" + currentUser.currentUserDetails.faith.faithLevel.display + ")"));
            }
            if (currentUser.currentUserDetails.languageObjects != null) {
                languagesContainer.setMaxLines(100);
                languagesContainer.setText(LanguageObject.getStringFromList(currentUser.currentUserDetails.languageObjects));
            }
            if (currentUser.currentUserDetails.interests != null) {

                activitiesContainer.setText(InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("a")));
                musicContainer.setText(InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("m")));
                moviesContainer.setText(InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("mo")));
                literatureContainer.setText(InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("l")));
                placesContainer.setText(InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("p")));
                if (currentUser.currentUserDetails.interests.get("o").size() == 0)
                    workContainer.setText(getString(R.string.unspecified));
                else
                    workContainer.setText(currentUser.currentUserDetails.interests.get("o").get(0).title);
            }

            if (currentUser.currentUserDetails.about != null)
                privetalkEditText.setText(currentUser.currentUserDetails.about);
        }
    }

    private void setVisibilityRequest(boolean setVisible) {

        Toast.makeText(getContext(), getString(R.string.attemptin_to_change), Toast.LENGTH_SHORT).show();

        final String link;

        if (setVisible) {
            Log.d("VISIBILITY", "SET_VISIBLE");
            link = Links.SET_VISIBLE;
        } else {
            Log.d("VISIBILITY", "SET_HIDDEN");
            link = Links.SET_HIDDEN;
        }

        JsonObjectRequest visibilityRequest = new JsonObjectRequest(Request.Method.POST, link, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("Visiblity", response.toString());

                CurrentUser currentUser2 = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
                currentUser2.hidden_mode_on = link.equals(Links.SET_HIDDEN);

                CurrentUserDatasource.getInstance(getContext()).saveCurrentUser(currentUser2);

                hiddenModeChanged = true;

                PriveTalkUtilities.getUserInfoFromServer();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(visibilityRequest);
    }


    @Override
    protected String getActionBarTitle() {
        return title;
    }

    private void loadPhotos() {
        if (fragmentPaused) return;
        currentUserPhotoObjectArrayList.clear();
        currentUserPhotoObjectArrayList.addAll(CurrentUserPhotosDatasource.getInstance(getContext()).getCurrentUserPhotos());
        setPhotosRecyclerViewWidth();
        photoViewPager.setAdapter(new ProfilePhotoAdapter());
        photosRecyclerView.setAdapter(smallPhotosAdapter = new SmallPhotosAdapter());
    }


    private void setPhotosRecyclerViewWidth() {
        if (isDetached())
            return;

        int count = currentUserPhotoObjectArrayList.size();

        int smallPhotosWidth = getResources().getDimensionPixelSize(R.dimen.small_photos_width);

        //set recyclerview width depending photos count
        if (count == 0)
            photosRecyclerViewParams.width = 0;
        else if (count == 1)
            photosRecyclerViewParams.width = smallPhotosWidth;
        else if (count == 2)
            photosRecyclerViewParams.width = 2 * smallPhotosWidth;
        else
            photosRecyclerViewParams.width = 3 * smallPhotosWidth;


        photosRecyclerView.setLayoutParams(photosRecyclerViewParams);

    }

    private class SmallPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new SmallPhotosViewHolder(inflater.inflate(R.layout.row_small_circle_imageview, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            SmallPhotosViewHolder holder1 = (SmallPhotosViewHolder) holder;
            Log.d("testingphoto", " square_thumb " + currentUserPhotoObjectArrayList.get(position).square_thumb);

            Glide.with(PriveTalkApplication.getInstance()).load(currentUserPhotoObjectArrayList.get(position).square_thumb).error(R.drawable.dummy_img).into(holder1.circleImageView);
        }


        @Override
        public int getItemCount() {
            return currentUserPhotoObjectArrayList.size();
        }

        private class SmallPhotosViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView circleImageView;

            public SmallPhotosViewHolder(View itemView) {
                super(itemView);

                circleImageView = (CircleImageView) itemView.findViewById(R.id.smallProfileCircleImageview);
            }
        }
    }


    private class ProfilePhotoAdapter extends PagerAdapter {

        private int imageCenterY;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView convertView = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.circle_imageview, container, false);

            if (currentUserPhotoObjectArrayList.size() > 0) {

                Log.d("testingphoto", " medium_square_thumb " + currentUserPhotoObjectArrayList.get(position).medium_square_thumb);

                Glide.with(PriveTalkApplication.getInstance()).load(currentUserPhotoObjectArrayList.get(position).medium_square_thumb).error(R.drawable.dummy_img).into(convertView);

                convertView.setOnTouchListener(new View.OnTouchListener() {
                    float mDownRawX;
                    float mDownRawY;
                    int distanceX;
                    int distanceY;
                    int distanceFromCenter;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        final int action = MotionEventCompat.getActionMasked(event);
                        switch (action) {
                            case MotionEvent.ACTION_DOWN: {
                                mDownRawX = event.getRawX();
                                mDownRawY = event.getRawY() - actionBarAndStatusBarSize;
                                imageCenterY = pagerMargin + imageRadius - rootView.getCurrentView().getScrollY();
                                break;
                            }

                            case MotionEvent.ACTION_UP: {
                                if (((int) mDownRawX) > imageCenterX)
                                    distanceX = (int) mDownRawX - imageCenterX;
                                else
                                    distanceX = imageCenterX - (int) mDownRawX;

                                if (((int) mDownRawY) > imageCenterY)
                                    distanceY = (int) mDownRawY - imageCenterY;
                                else
                                    distanceY = imageCenterY - (int) mDownRawY;

                                distanceFromCenter = ((int) Math.sqrt((distanceX * distanceX) + (distanceY * distanceY)));

                                if (distanceFromCenter < (imageRadius - blueCircleWidth)) {
                                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.FULL_SCREEN_PICTURES_FRAGMENT);
                                }
                                break;
                            }
                        }
                        return true;
                    }
                });
            } else
                Glide.with(PriveTalkApplication.getInstance()).load(R.drawable.dummy_img).into(convertView);

            container.addView(convertView);

            return convertView;
        }

        @Override
        public int getCount() {
            return currentUserPhotoObjectArrayList.size() == 0 ? 1 : currentUserPhotoObjectArrayList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {


            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {


                imgAbsolutePath = imageFile.getAbsolutePath();

                showCropImageLayout(imgAbsolutePath);

            }
        });
    }


    private void showCropImageLayout(final String imgAbsolutePath1) {

        try {
            final View cropDoneView = rootView.findViewById(R.id.cropDone);
            View cropCancelledView = rootView.findViewById(R.id.cropCancel);
            final CropImageView cropImageView = (CropImageView) rootView.findViewById(R.id.cropImageView);

            rootView.findViewById(R.id.rotateRight).setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                }
            });

            Bitmap bitmap = BitmapUtilities.autoRotateAndScaleBitmap(imgAbsolutePath1);


            if (bitmap == null)
                bitmap = BitmapUtilities.getScaledBitmapIfLarge(BitmapFactory.decodeFile(imgAbsolutePath1));

            cropImageView.setImageBitmap(bitmap);

            cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
            //  cropImageView.setCustomRatio(3,4);
            cropImageView.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW);
            cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);

            cropDoneView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    imgAbsolutePath = null;
                    progressBar.setVisibility(View.VISIBLE);

                    if (photosRecyclerView.getAdapter().getItemCount() == 0) {
                        rootView.showPrevious();
                        PriveTalkUtilities.uploadProfilePicture(cropImageView.getImageBitmap(), cropImageView.getCroppedBitmap(), true);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(getString(R.string.profile_photo));
                        builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rootView.showPrevious();
                                PriveTalkUtilities.uploadProfilePicture(cropImageView.getImageBitmap(), cropImageView.getCroppedBitmap(), true);
                            }
                        });
                        builder.setNegativeButton(getString(R.string.no_string), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rootView.showPrevious();
                                PriveTalkUtilities.uploadProfilePicture(cropImageView.getImageBitmap(), cropImageView.getCroppedBitmap(), false);
                            }
                        });
                        builder.create().show();
                    }
                }
            });

            cropCancelledView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    imgAbsolutePath = null;
                    rootView.showPrevious();
                }
            });

            rootView.showNext();

        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(getContext(), getContext().getString(R.string.loading_picture_failed), Toast.LENGTH_SHORT).show();
        }
    }

}
