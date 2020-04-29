package com.privetalk.app.mainflow.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CommunityUsersDatasourse;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.objects.CommunityUsersObject;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.CurrentUserDetails;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.database.objects.LanguageObject;
import com.privetalk.app.database.objects.MutualFriendsObject;
import com.privetalk.app.database.objects.UserObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.DrawerUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FlameImageView;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.LocationUtilities;
import com.privetalk.app.utilities.PopupCommonFriends;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.ProfileAwardsContainer;
import com.privetalk.app.utilities.ProfilePersonalInfoContainer;
import com.privetalk.app.utilities.SmoothScrollLinearLayoutManager;
import com.privetalk.app.utilities.VerificationTickView;
import com.privetalk.app.utilities.VolleySingleton;
import com.privetalk.app.utilities.dialogs.CommonInterestsPopUp;
import com.privetalk.app.utilities.dialogs.ReportDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zachariashad on 22/02/16.
 */
public class OtherUsersProfileInfoFragment extends FragmentWithTitle {

    private View hotWheelRightClicks;

    public enum RECEIVER_ACTIONS {COMMON_FRIENDS}

    private View rootView;
    private View progressBar;
    private int otherUsedID;
    private UserObject otherUserObject;

    //views
    private PriveTalkTextView interestsTextView;
    private PriveTalkTextView tvVerifiedUser;
    private PriveTalkTextView profileName;
    private PriveTalkTextView profileAge;
    private PriveTalkTextView userDistance;
    private ImageView tickVerifiedProfilePicture;
    private ImageView tickVerifiedSocialMedia;
    private ImageView tickRoyalUser;
    private PriveTalkTextView commonFriendsCount;
    private PriveTalkTextView commonInterestsCount;
    private RecyclerView smallPicturesRecyclerView;
    private LinearLayout.LayoutParams photosRecyclerViewParams;
    private FlameImageView flameImageView;
    private ImageView sendMessage;
    private ImageView favoriteStar;
    private ImageView sendGift;
    private RelativeLayout layVerifiedUser;
    private ProfilePersonalInfoContainer locationContainer;
    private ProfilePersonalInfoContainer ageContainer;
    private ProfilePersonalInfoContainer relationshipContainer;
    private ProfilePersonalInfoContainer sexualityContainer;
    private ProfilePersonalInfoContainer smokingContainer;
    private ProfilePersonalInfoContainer drinkinContainer;
    private ProfilePersonalInfoContainer educationContainer;
    private ProfilePersonalInfoContainer workContainer;
    private ProfilePersonalInfoContainer languagesContainer;
    private ProfilePersonalInfoContainer relegionContainer;
    private ProfilePersonalInfoContainer zodiacContainer;
    private ProfilePersonalInfoContainer activitiesContainer;
    private ProfilePersonalInfoContainer musicContainer;
    private ProfilePersonalInfoContainer moviesContainer;
    private ProfilePersonalInfoContainer literatureContainer;
    private ProfilePersonalInfoContainer placesContainer;
    private ProfilePersonalInfoContainer bodyTypeContainer;
    private PriveTalkTextView aboutMe;
    private ImageView userStatus;
    private ViewPager photoPreviewPager;
    private View reportOrBlockUser;

    //other
    private int greenColor;
    private Drawable online;
    private Drawable offline;
    private int imageRadius;
    private int imageCenterX;
    private int imageCenterY;
    private int blueCircleWidth;
    private int actionBarAndStatusBarSize;
    private int pagerMargin;
    private CurrentUser currentUser;
    private HashMap<String, ArrayList<InterestObject>> commonInterests;


    private JsonObjectRequest fetchOtherUserInfo;
    private AsyncTask<Void, Void, Void> parseUserProfileDetails;
    private ArrayList<MutualFriendsObject> mutualFriendsObjects;
    private JsonObjectRequest visitUser;
    private JsonObjectRequest postLocation;
    private CommunityUsersObject obj;
    private LayoutInflater inflater;
    private ProfileAwardsContainer popularAwardsContainer;
    private ProfileAwardsContainer friendlyAwardsContainer;
    private ProfileAwardsContainer iceBreakerAwardsContainer;
    private ProfileAwardsContainer impressionVoterAwardsContainer;
    private ProfileAwardsContainer angelsAwardsContainer;
    private ScrollView parentScrollView;
    private ImageView imageCold, imageHot;
    private Boolean isHot = false, isCold = false;
    private JsonObjectRequest sendVoteRequest;
    private boolean hasProfilePicture;

    private BroadcastReceiver utilitiesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            RECEIVER_ACTIONS action = (RECEIVER_ACTIONS) bundle.getSerializable("action");

            switch (action) {
                case COMMON_FRIENDS:
                    mutualFriendsObjects = (ArrayList<MutualFriendsObject>) bundle.getSerializable("common_friends");
//                    commonFriendsCount.setVisibility(mutualFriendsObjects.size() == 0 ? View.INVISIBLE : View.VISIBLE);
                    commonFriendsCount.setText(String.valueOf(mutualFriendsObjects.size()));
                    break;
            }
        }
    };

    @Override
    protected String getActionBarTitle() {
        obj = CommunityUsersDatasourse.getInstance(getContext()).getUserByID(otherUsedID);
        if (obj == null || obj.userName == null)
            return "";
        return obj.userName.split(" ")[0] + ", " + obj.userAge;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            otherUsedID = bundle.getInt(PriveTalkConstants.KEY_OTHER_USER_ID);
            setOtherUserVisited(String.valueOf(otherUsedID));
        }

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
        if (currentUser == null)
            currentUser = new CurrentUser();
        if (currentUser.currentUserDetails == null)
            currentUser.currentUserDetails = new CurrentUserDetails();
        if (currentUser.currentUserDetails.interests == null)
            currentUser.currentUserDetails.interests = new HashMap<>();

        if (CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() == null)
            hasProfilePicture = false;
        else {
            String mainPhoto = CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto().photo;
            hasProfilePicture = (mainPhoto != null && !mainPhoto.isEmpty());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.inflater = inflater;

        ProfilePersonalInfoContainer.inflaterCounter = 0;

        rootView = inflater.inflate(R.layout.fragment_view_other_user_profile, container, false);

        getViews();

        getOtherUserInfo();

        return rootView;
    }


    private void setPhotosRecyclerViewWidth() {
        int count = otherUserObject.profilePhotosList.size();

        int smallPhotosWidth = getResources().getDimensionPixelSize(R.dimen.small_photos_width);

        //set recyclerview width depending photos count
        if (count == 0)
            photosRecyclerViewParams.width = 0;
        else if (count == 1)
            photosRecyclerViewParams.width = smallPhotosWidth;
        else if (count == 2)
            photosRecyclerViewParams.width = 2 * smallPhotosWidth;
        else if (count == 3)
            photosRecyclerViewParams.width = 3 * smallPhotosWidth;
        else
            photosRecyclerViewParams.width = 4 * smallPhotosWidth;


        smallPicturesRecyclerView.setLayoutParams(photosRecyclerViewParams);

    }


    @Override
    public void onPause() {

        if (fetchOtherUserInfo != null)
            fetchOtherUserInfo.cancel();

        if (parseUserProfileDetails != null)
            parseUserProfileDetails.cancel(true);

        if (visitUser != null)
            visitUser.cancel();

        if (postLocation != null)
            postLocation.cancel();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(utilitiesReceiver);

        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(utilitiesReceiver, new IntentFilter(PriveTalkConstants.BROADCAST_OTHER_USERS_RECEIVER));
    }


    private void getViews() {

        parentScrollView = (ScrollView) rootView.findViewById(R.id.otherUserScrollview);
        progressBar = rootView.findViewById(R.id.progressBar);
        profileName = (PriveTalkTextView) rootView.findViewById(R.id.profileName);
        profileAge = (PriveTalkTextView) rootView.findViewById(R.id.profileAge);
        userDistance = (PriveTalkTextView) rootView.findViewById(R.id.userDistance);
        tickVerifiedProfilePicture = (ImageView) rootView.findViewById(R.id.tickVerifiedProfilePicture);
        tickVerifiedSocialMedia = (ImageView) rootView.findViewById(R.id.tickVerifiedSocialMedia);
        tickRoyalUser = (ImageView) rootView.findViewById(R.id.tickRoyalUser);
        commonFriendsCount = (PriveTalkTextView) rootView.findViewById(R.id.commonFriendsCount);
        commonInterestsCount = (PriveTalkTextView) rootView.findViewById(R.id.commonInterestsCount);
        smallPicturesRecyclerView = (RecyclerView) rootView.findViewById(R.id.smallPhotosRecyclerView);
        photosRecyclerViewParams = (LinearLayout.LayoutParams) smallPicturesRecyclerView.getLayoutParams();
        flameImageView = (FlameImageView) rootView.findViewById(R.id.flameImageView);
        sendMessage = (ImageView) rootView.findViewById(R.id.sendMessage);
        favoriteStar = (ImageView) rootView.findViewById(R.id.sendStar);
        imageCold = (ImageView) rootView.findViewById(R.id.iconCold);
        imageHot = (ImageView) rootView.findViewById(R.id.iconHot);
        sendGift = (ImageView) rootView.findViewById(R.id.sendGift);
        locationContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileLocationContainer);
        ageContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileAgeContainer);
        relationshipContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileRelationshipContainer);
        sexualityContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileSexualityContainer);
        smokingContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileSmokingContainer);
        drinkinContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileDrinkingContainer);
        educationContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileEducationContainer);
        workContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileWorkContainer);
        languagesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileLanguageContainer);
        relegionContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileReligionContainer);
        zodiacContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileZodiacContainer);
        activitiesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileActivitiesContainer);
        musicContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileMusicContainer);
        moviesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileMoviesContainer);
        literatureContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileLiteratureContainer);
        placesContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profilePlacesContainer);
        bodyTypeContainer = (ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileBodyTypeContainer);
        aboutMe = (PriveTalkTextView) rootView.findViewById(R.id.profileTextAboutMe);
        userStatus = (ImageView) rootView.findViewById(R.id.userStatus);
        layVerifiedUser = (RelativeLayout) rootView.findViewById(R.id.layVerified);
        tvVerifiedUser = (PriveTalkTextView) rootView.findViewById(R.id.tvVeriied);
        interestsTextView = (PriveTalkTextView) rootView.findViewById(R.id.profilePersonalIterestsStatic);
        photoPreviewPager = (ViewPager) rootView.findViewById(R.id.photoViewpager);
        reportOrBlockUser = rootView.findViewById(R.id.reportOrBlockUserText);

        reportOrBlockUser.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                new ReportDialog(getContext(), (RelativeLayout) rootView, inflater, String.valueOf(otherUsedID)).isFromChat(false).show();
            }
        });

        popularAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.popularAwardsContainer);
        friendlyAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.friendlyAwardsContainer);
        iceBreakerAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.iceBreakerAwardsContainer);
        impressionVoterAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.impressionVoterAwardsContainer);
        angelsAwardsContainer = (ProfileAwardsContainer) rootView.findViewById(R.id.angelAwardsContainer);

        greenColor = ContextCompat.getColor(getContext(), R.color.verification_green);
        online = ContextCompat.getDrawable(getContext(), R.drawable.green_circle);
        offline = ContextCompat.getDrawable(getContext(), R.drawable.oval_gray_border);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageCenterX = size.x / 2;

        Rect r = new Rect();
        View myView = getActivity().getWindow().getDecorView();
        myView.getWindowVisibleDisplayFrame(r);

        actionBarAndStatusBarSize = getResources().getDimensionPixelSize(R.dimen.action_bar) + r.top;

        blueCircleWidth = getResources().getDimensionPixelOffset(R.dimen.blue_circle_border_width);

        imageRadius = getResources().getDimensionPixelOffset(R.dimen.photo_view_pager_width) / 2;
        pagerMargin = getResources().getDimensionPixelSize(R.dimen.photo_view_pager_width_top_margin);

    }

    private void showInfo() {

        progressBar.setVisibility(View.GONE);

        flameImageView.changeHotness(otherUserObject.hotnessPercentage);

        /*sendMessage.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.CHAT_FRAGMENT);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                Bundle bundle = new Bundle();
                ConversationObject conversationObject = new ConversationObject();
                conversationObject.partnerID = otherUsedID;
                conversationObject.partnerName = otherUserObject.name;
                conversationObject.isSenderRoyal = otherUserObject.royalUser;
                conversationObject.partnerAvatarImg = otherUserObject.mainProfilePhoto;
                conversationObject.senderAge = otherUserObject.age;

                bundle.putSerializable("partnerObject", conversationObject);
                intent.putExtra("bundle", bundle);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });
*/
       /* sendGift.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.CHAT_FRAGMENT);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                Bundle bundle = new Bundle();
                ConversationObject conversationObject = new ConversationObject();
                conversationObject.partnerID = otherUsedID;
                conversationObject.partnerName = otherUserObject.name;
                conversationObject.isSenderRoyal = otherUserObject.royalUser;
                conversationObject.partnerAvatarImg = otherUserObject.mainProfilePhoto;
                conversationObject.senderAge = otherUserObject.age;

                bundle.putSerializable("partnerObject", conversationObject);
                bundle.putBoolean("is_gift", true);
                intent.putExtra("bundle", bundle);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);

            }
        });*/

        imageCold.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (hasProfilePicture) {
                    isCold=true;
                    isHot=false;
                    sendVote(false, otherUsedID);
                } else
                    showAccessDeniedDialog();
            }
        });

        imageHot.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (hasProfilePicture) {
                    isHot = true;
                    isCold=false;
                    sendVote(true, otherUsedID);
                } else {
                    showNeedToBeRoyalUserAlert();
                }
            }
        });

        favoriteStar.setColorFilter(ContextCompat.getColor(getContext(),
                otherUserObject.isFavorite ? R.color.star_button_color : R.color.blue_border_color),
                PorterDuff.Mode.SRC_IN);

        imageHot.setColorFilter(ContextCompat.getColor(getContext(),
                otherUserObject.isFavorite ? R.color.star_button_color : R.color.blue_border_color),
                PorterDuff.Mode.SRC_IN);

        favoriteStar.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                addOrRemoveFavorite(String.valueOf(otherUsedID));
            }
        });


        Intent intent = new Intent(PriveTalkConstants.CHANGE_ACTIONBAR_TITLE);
        intent.putExtra(PriveTalkConstants.ACTION_BAR_TITLE, (otherUserObject.name.isEmpty()) ?
                (R.string.user_info) : (otherUserObject.name.split(" ")[0] + ", " + String.valueOf(otherUserObject.age)));
        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcastSync(intent);


        userDistance.setText(getDistance());

        photoPreviewPager.setAdapter(new ProfilePhotoAdapter());

        if (otherUserObject.profilePhotosList.size() > 0) {
            if (otherUserObject.profilePhotosList.get(0).isVerifiedPhoto) {
                layVerifiedUser.setVisibility(View.VISIBLE);
                tvVerifiedUser.setText("Verified");
            } else {
                layVerifiedUser.setVisibility(View.GONE);
            }
        }

        photoPreviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (otherUserObject.profilePhotosList.get(position).isVerifiedPhoto) {
                    layVerifiedUser.setVisibility(View.VISIBLE);
                    tvVerifiedUser.setText("Verified");
                } else {
                    layVerifiedUser.setVisibility(View.GONE);

                }
                Toast.makeText(getActivity(), "" + otherUserObject.profilePhotosList.get(position).isVerifiedPhoto, Toast.LENGTH_SHORT).show();

                smallPicturesRecyclerView.smoothScrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        flameImageView = (FlameImageView) rootView.findViewById(R.id.flameImageView);
        flameImageView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                parentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });


        rootView.findViewById(R.id.commonFriendsIcon).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (mutualFriendsObjects == null | mutualFriendsObjects.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.no_common_friends), Toast.LENGTH_SHORT).show();
                    return;
                }

                final View inflatedView = LayoutInflater.from(getContext()).inflate(R.layout.layout_common_friends, null);
                inflatedView.post(new Runnable() {
                    @Override
                    public void run() {
                        PopupCommonFriends commonFriends = new PopupCommonFriends(inflatedView,
                                getResources().getDimensionPixelSize(R.dimen.popup_common_friends_width),
                                getResources().getDimensionPixelSize(R.dimen.popup_common_friends_height),
                                true);
                        commonFriends.initiliaze(getContext(), rootView, mutualFriendsObjects);
                    }
                });
            }
        });


        rootView.findViewById(R.id.commonInterestsIcon).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (commonInterests == null || InterestObject.getInterestsCount(commonInterests) == 0) {
                    Toast.makeText(getContext(), getString(R.string.no_common_interests), Toast.LENGTH_SHORT).show();
                    return;
                }
                final View inflatedView = LayoutInflater.from(getContext()).inflate(R.layout.layout_common_interests, null);
                inflatedView.post(new Runnable() {
                    @Override
                    public void run() {
                        new CommonInterestsPopUp(inflatedView,
                                getResources().getDimensionPixelSize(R.dimen.popup_common_interests_width),
                                getResources().getDimensionPixelSize(R.dimen.popup_common_interests_height), true)
                                .initiliaze(getContext(), rootView, commonInterests);

                    }
                });
            }
        });


        smallPicturesRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setPhotosRecyclerViewWidth();
        smallPicturesRecyclerView.setAdapter(new SmallPhotosAdapter());

        userStatus.setImageDrawable(otherUserObject.isOnline ? online : offline);
        tickVerifiedProfilePicture.setColorFilter(otherUserObject.photosVerified ? greenColor : Color.WHITE, PorterDuff.Mode.SRC_IN);
        tickVerifiedSocialMedia.setColorFilter(otherUserObject.socialVerified ? greenColor : Color.WHITE, PorterDuff.Mode.SRC_IN);
        tickRoyalUser.setColorFilter(otherUserObject.royalUser ? greenColor : Color.WHITE, PorterDuff.Mode.SRC_IN);

        profileName.setText(otherUserObject.name.split(" ")[0]);
        profileAge.setText(String.valueOf(otherUserObject.age));
        locationContainer.setText(otherUserObject.location);
        ageContainer.setText(String.valueOf(otherUserObject.age));
        relationshipContainer.setText(otherUserObject.info.relationshipStatus.display);
        sexualityContainer.setText(otherUserObject.info.sexualityStatus.display);
        bodyTypeContainer.setText(otherUserObject.info.bodyType.display);

        ((ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileHeightContainer))
                .setText((otherUserObject.info.height == 0) ? getString(R.string.unspecified) : String.valueOf(otherUserObject.info.height) + " " + getString(R.string.cm));

        ((ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileWeightContainer))
                .setText((otherUserObject.info.weight == 0) ? getString(R.string.unspecified) : String.valueOf(otherUserObject.info.weight) + " " + getString(R.string.kg));

        ((ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileHairContainer))
                .setText(otherUserObject.info.hairColor.display);

        ((ProfilePersonalInfoContainer) rootView.findViewById(R.id.profileEyesContainer))
                .setText(otherUserObject.info.eyesColor.display);


        smokingContainer.setText(otherUserObject.info.smokingStatus.display);
        drinkinContainer.setText(otherUserObject.info.drinkinStatus.display);
        educationContainer.setText(otherUserObject.info.educationStatus.display);
        String work = InterestObject.getStringFromList(otherUserObject.interests.get("o"));
        workContainer.setText(work.isEmpty() ? getString(R.string.unspecified) : work);

        //languages
        languagesContainer.setText(LanguageObject.getStringFromList(otherUserObject.languagesList));

        //faith
        relegionContainer.setText(otherUserObject.faith.religion.display +
                (otherUserObject.faith.religion.value.equals("17") ? "" : " (" + otherUserObject.faith.faithLevel.display + ")"));

        //zodiac
        zodiacContainer.setText(otherUserObject.info.zodiac.display);

        //interests count
        interestsTextView.setText(getString(R.string.interests_sket) + "(" + String.valueOf(InterestObject.getInterestsCount(otherUserObject.interests)) + " " + getString(R.string.total) + ")");

        //interests
        activitiesContainer.setText(InterestObject.getStringFromList(otherUserObject.interests.get("a")));
        musicContainer.setText(InterestObject.getStringFromList(otherUserObject.interests.get("m")));
        moviesContainer.setText(InterestObject.getStringFromList(otherUserObject.interests.get("mo")));
        literatureContainer.setText(InterestObject.getStringFromList(otherUserObject.interests.get("l")));
        placesContainer.setText(InterestObject.getStringFromList(otherUserObject.interests.get("p")));

        //about me stringie
        aboutMe.setText(otherUserObject.info.about);

        popularAwardsContainer.assignValues(otherUserObject, "popular_score");
        friendlyAwardsContainer.assignValues(otherUserObject, "friendly_score");
        iceBreakerAwardsContainer.assignValues(otherUserObject, "ice_breaker_score");
        impressionVoterAwardsContainer.assignValues(otherUserObject, "impression_voter_score");
        angelsAwardsContainer.assignValues(otherUserObject, "angel_score");

//        commonInterestsCount.setVisibility(InterestObject.getInterestsCount(commonInterests) == 0 ? View.INVISIBLE : View.VISIBLE);
        commonInterestsCount.setText(String.valueOf(InterestObject.getInterestsCount(commonInterests)));

        hotWheelRightClicks = rootView.findViewById(R.id.hotWheelrightTicks);
        hotWheelRightClicks.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                final View inflatedView = inflater.inflate(R.layout.otherprofile_verification, null);

                inflatedView.post(new Runnable() {
                    @Override
                    public void run() {

                        final PopupWindow popupWindow = new PopupWindow(inflatedView,
                                getResources().getDimensionPixelSize(R.dimen.popup_verification_width),
                                getResources().getDimensionPixelSize(R.dimen.popup_verification_height),
                                true);

                        popupWindow.setTouchable(true);
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        popupWindow.setOutsideTouchable(true);
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

                        popupProfilePictureTick.setColorFilter(otherUserObject.photosVerified ? greenColor : Color.GRAY, PorterDuff.Mode.SRC_IN);
                        popupSocialMediaTick.setColorFilter(otherUserObject.socialVerified ? greenColor : Color.GRAY, PorterDuff.Mode.SRC_IN);
                        popupRoyalUserTick.setColorFilter(otherUserObject.royalUser ? greenColor : Color.GRAY, PorterDuff.Mode.SRC_IN);


                        popupWindow.showAtLocation(rootView, Gravity.LEFT | Gravity.TOP,
                                (hotWheelRightClicks.getRight() - getResources().getDimensionPixelSize(R.dimen.popup_verification_width)),
                                (int) hotWheelRightClicks.getY());

                    }
                });
            }
        });
    }

    private void sendVote(boolean is_hot, final int partner_id) {
        if (partner_id == -1)
            return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("partner_id", partner_id);
            jsonObject.put("is_hot", is_hot);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendVoteRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, String.format(Links.CREATE_MATCH, partner_id), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


               /* if (isHot) {
                    imageHot.setImageResource(R.drawable.alpha_flame);
                } else {
                    imageHot.setImageResource(R.drawable.flames_icon);
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error vote" + error.networkResponse != null ? new String(error.networkResponse.data) : "null");
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(sendVoteRequest);

    }

    private void showNeedToBeRoyalUserAlert() {

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.not_royal_user))
                .setMessage(R.string.royal_user_plans_flames_ignited)
                .setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID);
                    }
                })
                .setNegativeButton(getString(R.string.later), null)
                .create().show();
    }

    private void getOtherUserInfo() {

        if (otherUsedID > 0) {

            String URL = Links.GET_COMMUNITY + String.valueOf(otherUsedID) + "/";

            fetchOtherUserInfo = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(final JSONObject response) {

                    parseUserProfileDetails = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            otherUserObject = new UserObject(response); // get data
                            commonInterests = InterestObject.getCommonInterests(currentUser.currentUserDetails.interests, otherUserObject.interests);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (otherUserObject != null) {
                                showInfo();
                                PriveTalkUtilities.getCommonFacebookFriends(otherUserObject.fbID, PriveTalkConstants.BROADCAST_OTHER_USERS_RECEIVER);
                            } else
                                showDialog();
                        }
                    }.execute();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showDialog();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    return super.parseNetworkResponse(response);
                }

                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                    return headers;
                }
            };
            VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(fetchOtherUserInfo);
        } else {
            showDialog();
        }
    }


    private void setOtherUserVisited(String id) {

        String visitUrl = Links.VISIT_USER + id;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("partner_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        visitUser = new JsonObjectRequest(Request.Method.POST, visitUrl, jsonObject,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {

                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(visitUser);
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setCancelable(false)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    }
                }).setTitle(R.string.error).setMessage(R.string.problem_loading_user_info);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private class ProfilePhotoAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView convertView = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.circle_imageview, container, false);
            if (otherUserObject.profilePhotosList.size() > 0) {

                if (!otherUserObject.profilePhotosList.get(position).square_photo.contains("https://privetalkdev.s3.amazonaws.com/")) {
                    otherUserObject.profilePhotosList.get(position).square_photo = "https://privetalkdev.s3.amazonaws.com/" + otherUserObject.profilePhotosList.get(position).square_photo;
                }

                Log.d("testingphotouser", " square_photo " + otherUserObject.profilePhotosList.get(position).square_photo);

                Glide.with(PriveTalkApplication.getInstance()).load(otherUserObject.profilePhotosList.get(position).square_photo).into(convertView);


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
                                imageCenterY = pagerMargin + imageRadius - rootView.findViewById(R.id.otherUserScrollview).getScrollY();
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
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("other_user_photos", otherUserObject.profilePhotosList);
                                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_FULL_SCREEN_PHOTOS, bundle);
                                }
                                break;
                            }
                        }
                        return true;
                    }
                });
            } else {
                Glide.with(PriveTalkApplication.getInstance()).load(R.drawable.dummy_img).into(convertView);
                Log.d("zeniosdevil", "string url : " + " dummy_img");
            }


            container.addView(convertView);

            return convertView;
        }

        @Override
        public int getCount() {
            return otherUserObject.profilePhotosList.size() == 0 ? 1 : otherUserObject.profilePhotosList.size();
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

    private class SmallPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            SmallPhotosViewHolder sh = new SmallPhotosViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_small_circle_imageview, parent, false));

            sh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoPreviewPager.setCurrentItem((int) v.getTag(R.id.position_tag));
                    smallPicturesRecyclerView.smoothScrollToPosition((int) v.getTag(R.id.position_tag));
                }
            });

            return sh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SmallPhotosViewHolder holder1 = (SmallPhotosViewHolder) holder;

            Glide.with(PriveTalkApplication.getInstance()).load(otherUserObject.profilePhotosList.get(position).square_thumb).into(holder1.circleImageView);

            Log.d("testingphotouser", " square_thumb " + otherUserObject.profilePhotosList.get(position).square_thumb);

            Log.d("zeniosdevil", "string url : square_thumb " + otherUserObject.profilePhotosList.get(position).square_thumb);

            holder1.view.setTag(R.id.position_tag, position);
        }

        @Override
        public int getItemCount() {
            return otherUserObject.profilePhotosList.size();
        }

        private class SmallPhotosViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView circleImageView;
            private View view;

            public SmallPhotosViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                circleImageView = (CircleImageView) itemView.findViewById(R.id.smallProfileCircleImageview);
            }
        }
    }


    private void addOrRemoveFavorite(String userID) {

        favoriteStar.setEnabled(false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("favorite", !otherUserObject.isFavorite);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        postLocation = new JsonObjectRequest(Request.Method.POST, Links.FAVORITE_USER + userID, obj,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        favoriteStar.setEnabled(true);
                        otherUserObject.isFavorite = response.optBoolean("favorite");
                        if (response.optBoolean("success")) {

                            Toast.makeText(getContext(),
                                    otherUserObject.isFavorite ? getString(R.string.added_to_favorites) : getString(R.string.removed_favorite),
                                    Toast.LENGTH_SHORT).show();

                            favoriteStar.setColorFilter(ContextCompat.getColor(getContext(),
                                    otherUserObject.isFavorite ? R.color.star_button_color : R.color.blue_border_color),
                                    PorterDuff.Mode.SRC_IN);
                        } else {
                            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                favoriteStar.setEnabled(true);
                if (error.networkResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        Toast.makeText(PriveTalkApplication.getInstance(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                netWorkResponse = response;
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(postLocation);

    }

    private String getDistance() {
        float lat = LocationUtilities.getLatitude();
        float lng = LocationUtilities.getLongitude();

        if (lat == 0f && lng == 0f) {
            userDistance.setVisibility(View.GONE);
            return "";
        }

        Location locationA = new Location("point A");
        if (otherUserObject.realLocation != null
                && otherUserObject.realLocation.latitude != null
                && otherUserObject.realLocation.longitute != null) {
            locationA.setLatitude(Double.parseDouble(otherUserObject.realLocation.latitude));
            locationA.setLongitude(Double.parseDouble(otherUserObject.realLocation.longitute));
            Location locationB = new Location("point B");
            locationB.setLatitude((double) lat);
            locationB.setLongitude((double) lng);
            int distance = (int) locationA.distanceTo(locationB);

            return (distance > 1000) ? (String.valueOf(distance / 1000) + "\nkm") : (String.valueOf(distance) + "\nm");
        } else
            return "n/a";

    }

    private void showAccessDeniedDialog() {

       /* if (lockDialog)
            return;

        lockDialog = true;*/

        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.to_use_hot_cold)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //lockDialog = false;

                       // Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_DRAWER_FRAGMENT);
                        //intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.PROFILE.ordinal());
                        //LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               // lockDialog = false;
            }
        }).create().show();
    }


}
