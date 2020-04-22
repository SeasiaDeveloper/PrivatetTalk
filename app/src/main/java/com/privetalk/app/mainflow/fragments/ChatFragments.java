package com.privetalk.app.mainflow.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.percentlayout.widget.PercentLayoutHelper;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.ETagDatasource;
import com.privetalk.app.database.datasource.GiftsDatasource;
import com.privetalk.app.database.datasource.MessageDatasource;
import com.privetalk.app.database.datasource.TimeStepsDatasource;
import com.privetalk.app.database.objects.ConversationObject;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.GiftObject;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.database.objects.MessageObject;
import com.privetalk.app.database.objects.MutualFriendsObject;
import com.privetalk.app.database.objects.TimeStepsObject;
import com.privetalk.app.database.objects.UserObject;
import com.privetalk.app.mainflow.activities.CameraViewActivity;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.ChatAdapter;
import com.privetalk.app.mainflow.adapters.GiftsAdapter;
import com.privetalk.app.utilities.BitmapUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.JsonObjectRequestWithResponse;
import com.privetalk.app.utilities.KeyboardListenerPriveTalkEditText;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.TranslationTouchListener;
import com.privetalk.app.utilities.VolleySingleton;
import com.privetalk.app.utilities.dialogs.CommonFriendsDialog;
import com.privetalk.app.utilities.dialogs.CommonInterestsDialog;
import com.privetalk.app.utilities.dialogs.PurchaseDialog;
import com.privetalk.app.utilities.dialogs.ReportDialog;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by zachariashad on 10/02/16.
 */
public class ChatFragments extends FragmentWithTitle {


    private static final int PROGRESSBAR = 1;
    private static final int MESSAGE_ICON = 0;

    private static final int VIEW_CHAT = 0;
    private static final int VIEW_CAMERA_IMG = 1;

    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_GALLERY = 2;

    private View limitChatProgressView;

    private enum GIFTSACTIONS {SHOW_WITHOUT_ANIMATION, SHOW_GIFTS, HIDE_GIFTS}

    public static final String BACK_BUTTON_PRESSED = "chat-fragment-back-button-pressed";
    public static final String CHAT_HANDLE_EVENTS_RECEIVER = "chat-handle-events-receiver";
    public static final String MESSAGE_EXPIRED = "meesage-expired";
    public static final String COMMON_FRIENDS_DIALOG = "common-friends";
    public static final String COMMON_INTEREST_DIALOG = "common-interest";
    public static final String SHOW_GIFTS = "show-gifts";
    public static final String REFRESH_USER_DATA = "refresh-user";
    public static final String SHOW_FULL_SCREEN_IMAGE = "show-full-image";
//    public static final String

    private static final String FIRST_PAGE = "1";
    private static final int DEFAULT_MSG_LIFESPAN = 604800;
    private static final String VOTE_UP = "up";
    private static final String VOTE_DOWN = "down";
    private static final String MESSAGE_REQUEST_TAG = "message_request";

    private static final int ALPHA_DURATION = 200;
    private static final int GET_FB_ID = 0;
    private static final int GET_BALANCE = 1;
    private static final int GET_OTHER_USER_INFO = 2;
    private static final int GALLERY_IMAGE_REQUEST = 10;
    private static final int CAMERA_REQUEST = 11;
    public static final int FULL_SCREEN_REQUEST = 12;
    private static final int TEXT_MESSAGE = 0;
    private static final int IMG_MESSAGE = 1;
    private static final int GIFT_MESSAGE = 2;

    private View rootView;
    private View giftsLayout;
    private View chatOptions;
    private View revealGiftsButton;
    private View revealChatOptionsButton;
    private View chatBar;
    private ViewSwitcher votesBar;
    private View expirationTimeLayout;
    private View revealExpirationTimeButton;
    private View zeniosDevil;
    private View sendMessageButton;
    private View dislikeButton;
    private View likeButton;
    private View closeGifts;
    private View chatTempImageParentView;
    private ImageView chatMessageImageView;
    private ImageView expirationTimeSeekBarTrack;
    private ImageView expirationTimeSeekBarThumb;
    private PriveTalkTextView myCoins;
    private KeyboardListenerPriveTalkEditText messageEditText;
    private PercentRelativeLayout.LayoutParams expirationTimeSeekBarLayoutParams;
    private PercentLayoutHelper.PercentLayoutInfo expirationTimeSeekBarLayoutInfo;
    private PriveTalkTextView chatExpirationTimeText;
    private RecyclerView giftsRecyclerView;
    private GiftsAdapter giftsAdapter;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout.LayoutParams giftsLayoutParams;
    private ImageView pickGalleryPictureButton;
    private ImageView cameraPhotoButton;
    private View chatAddMore;
    private ViewSwitcher sendMessageViewSwitcher;
    private ViewSwitcher mainViewSwitcher;

    private int coinsBalance;
    //    Dialogs
    private ReportDialog reportDialog;
    private CommonFriendsDialog commonFriendsDialog;
    private CommonInterestsDialog commonInterestsDialog;
    private AlertDialog notRoyalUserDialog;
    private Handler mHandler;
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private int giftsLayoutHeight;
    private int expirationTimeLayoutWidth;
    private int expirationTimeLayoutMargin;
    private int expirationTimeSeekBarMargin;
    private float expirationTimeSeekBarParentWidthRatio;
    private boolean isGiftsLayoutVisible;
    private boolean isChatOptionsVisible;
    private boolean isExpirationTimeLayoutVisible;
    private boolean isKeyboardOpen = false;
    private int screenHeight;
    private float timeStepThreshold;
    private String imgAbsolutePath = null;

    private ConversationObject conversationObject;
    private CurrentUser currentUser;
    private UserObject otherUserObject;
    private HashMap<String, String> headers;
    private List<TimeStepsObject> timeStepsObjects;
    private List<MutualFriendsObject> mutualFriendsObjects;
    private HashMap<String, ArrayList<InterestObject>> commonInterest;
    private Bitmap imageBitmap;
    private Bitmap tempBitmap;
    private int messageType;
    private boolean isGift;
    private View progressBar;
    private boolean firstLoad = true;
    private TextView voteYourImpressionText;

    /*
    All request
     */
    private JsonObjectRequestWithResponse chatMessagesRequest;
    private JsonObjectRequest sendMessageRequest;
    private JsonArrayRequest getGiftsRequest;
    private JsonObjectRequest markAsReadRequest;
    private JsonObjectRequest voteRequest;
    private JsonObjectRequest getRequest;

    /*
    AsynchTasks
     */
    private AsyncTask<Void, Void, Void> messagesParser;
    private AsyncTask<Void, Void, Void> giftsParser;
    private AsyncTask<Void, Void, Void> parseUserProfileDetails;
    private AsyncTask<Void, List<MutualFriendsObject>, List<MutualFriendsObject>> parseMutualFriends;
    private AsyncTask<Void, Void, String> convertImageBitmap;


    private BroadcastReceiver onBackButtonPressed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isGiftsLayoutVisible && !isKeyboardOpen) {
                KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_VIEWS, getActivity(), rootView);
                changeGiftsLayout(GIFTSACTIONS.HIDE_GIFTS);
                isGiftsLayoutVisible = false;
            } else if (reportDialog != null && reportDialog.isVisible) {
                reportDialog.dismiss();
            } else if (commonFriendsDialog != null && commonFriendsDialog.isVisible) {
                commonFriendsDialog.dismiss();
            } else if (commonInterestsDialog != null && commonInterestsDialog.isVisible) {
                commonInterestsDialog.dismiss();
            } else if (isKeyboardOpen) {
                KeyboardUtilities.closeKeyboard(getActivity(), messageEditText);
                closeKeyboard();
            } else if (getActivity() != null)
                getActivity().onBackPressed();

        }
    };


    private BroadcastReceiver chatHandleEventsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String eventType = intent.getStringExtra(PriveTalkConstants.KEY_EVENT_TYPE);

            if (eventType == null)
                return;

            switch (eventType) {
                case PriveTalkConstants.BROADCAST_MESSAGE_RECEIVED: {
                    if (conversationObject.partnerID == intent.getIntExtra("user_id", conversationObject.partnerID) ||
                            conversationObject.partnerID == intent.getIntExtra("user_id2", conversationObject.partnerID)) {
                        downloadMessages(FIRST_PAGE, true);
                    }
                }
                break;

                case PriveTalkConstants.BROADCAST_GIFT_PRESSED: {
                    messageType = GIFT_MESSAGE;
//                    messageEditText.setVisibility(View.GONE);
                    chatTempImageParentView.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(GiftsDatasource.getInstance(getContext()).getGiftObjectById(intent.getIntExtra("gift_id", 0)).thumb).into(chatMessageImageView);
                    chatMessageImageView.setTag(R.id.id_tag, intent.getIntExtra("gift_id", 0));
                }
                break;

                case PriveTalkConstants.BROADCAST_CHAT_ADAPTER_CLICK: {
                    if (otherUserObject != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("other_user_photos", otherUserObject.profilePhotosList);
                        PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_FULL_SCREEN_PHOTOS, bundle);
                    }
                }
                break;

                case MESSAGE_EXPIRED:
                    MessageDatasource.getInstance(getContext()).setExpiredMessages();
                    chatAdapter.refreshData(false);
                    break;

                case COMMON_FRIENDS_DIALOG: {
                    if (mutualFriendsObjects != null && mutualFriendsObjects.size() > 0) {
                        commonFriendsDialog = new CommonFriendsDialog(getActivity(), (RelativeLayout) rootView).setDataset(mutualFriendsObjects).show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_common_friends), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

                case COMMON_INTEREST_DIALOG: {
                    if (commonInterest == null || InterestObject.getInterestsCount(commonInterest) == 0)
                        Toast.makeText(getContext(), getString(R.string.no_common_interests), Toast.LENGTH_SHORT).show();
                    else
                        commonInterestsDialog = new CommonInterestsDialog(getActivity(), (RelativeLayout) rootView).setDataSet(commonInterest).show();
                }
                break;

                case SHOW_GIFTS: {
                    giftsRecyclerView.setVisibility(View.VISIBLE);
                    KeyboardUtilities.closeKeyboard(getActivity(), rootView);
                    changeGiftsLayout(GIFTSACTIONS.SHOW_GIFTS);
                    isGiftsLayoutVisible = true;
                    isKeyboardOpen = false;
                }
                break;

                case REFRESH_USER_DATA:
                    currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
                    break;

            }
        }
    };


    private BroadcastReceiver timeStepsReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getContext().getSharedPreferences(PriveTalkConstants.PREFERENCES, 0).edit().putLong("time_step_last_updated", System.currentTimeMillis()).commit();
            timeStepsObjects.clear();
            timeStepsObjects.addAll(TimeStepsDatasource.getInstance(getContext()).getTimeSteps());
        }
    };


    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        Bundle bundle = getArguments().getBundle("bundle");
        if (bundle != null) {
            conversationObject = (ConversationObject) bundle.getSerializable("partnerObject");
            isGift = bundle.getBoolean("is_gift", false);
        }

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();

        PriveTalkUtilities.getUserInfoFromServer();

        timeStepsObjects = TimeStepsDatasource.getInstance(getContext()).getTimeSteps();

        long timeStamp = getContext().getSharedPreferences(PriveTalkConstants.PREFERENCES, 0).getLong("time_step_last_updated", 0);

        long timeStamp2 = getContext().getSharedPreferences(PriveTalkConstants.PREFERENCES, 0).getLong(PriveTalkConstants.KEY_CONVERSATION_COST_UPDATE, 0);

        //fetch time steps only once a day.
        if ((System.currentTimeMillis() - timeStamp) > 86400000 || TimeStepsDatasource.getInstance(getContext()).getTimeSteps().isEmpty())
            PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.TIME_STEPS, PriveTalkConstants.BROADCAST_TIMESTEPS_DOWNLOADED, null, new JSONObject());


        if ((System.currentTimeMillis() - timeStamp2) > 86400000 || preferences.getString(PriveTalkConstants.KEY_CONVERSATION_COST, "").isEmpty())
            getChatCost();
    }


    @Override
    protected String getActionBarTitle() {
        return (conversationObject == null || conversationObject.partnerName == null) ? "" : conversationObject.partnerName.split(" ")[0];
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainViewSwitcher = (ViewSwitcher) inflater.inflate(R.layout.fragment_chat, container, false);

        rootView = mainViewSwitcher.getChildAt(VIEW_CHAT);

        if (savedInstanceState != null) {
            imgAbsolutePath = savedInstanceState.getString(PriveTalkConstants.KEY_IMAGE_ABSOLUTE_PATH);
            if (imgAbsolutePath != null) {
                showCapturedImage(imgAbsolutePath);
                imgAbsolutePath = null;
            }
        }

        limitChatProgressView = rootView.findViewById(R.id.progressBarChatLayout);

        mHandler = new Handler();

        initViews();

        sendMessageViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.sendMessageViewSwitcher);

        getDimensions();

        //show gifts
        if (isGift) {
            changeGiftsLayout(GIFTSACTIONS.SHOW_WITHOUT_ANIMATION);
            isKeyboardOpen = false;
            isGiftsLayoutVisible = true;
        }

        getGifts(FIRST_PAGE);
        newGetRequest(GET_BALANCE);
        newGetRequest(GET_OTHER_USER_INFO);

        return mainViewSwitcher;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PriveTalkConstants.KEY_IMAGE_ABSOLUTE_PATH, imgAbsolutePath);
        super.onSaveInstanceState(outState);
    }

    private void lockMessageImagePreview(boolean lock) {
        if (lock) {
            chatTempImageParentView.setEnabled(false);
            chatTempImageParentView.setClickable(false);
            chatTempImageParentView.setAlpha(0.7f);
            chatTempImageParentView.findViewById(R.id.removeImage).setVisibility(View.GONE);
        } else {
            chatTempImageParentView.setEnabled(true);
            chatTempImageParentView.setClickable(true);
            chatTempImageParentView.setAlpha(1.0f);
            chatTempImageParentView.findViewById(R.id.removeImage).setVisibility(View.VISIBLE);
        }
    }


    private void lockSendMessage() {
        sendMessageViewSwitcher.setDisplayedChild(PROGRESSBAR);
    }

    private void unlockSendMessage() {
        sendMessageViewSwitcher.setDisplayedChild(MESSAGE_ICON);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(timeStepsReceived, new IntentFilter(PriveTalkConstants.BROADCAST_TIMESTEPS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(onBackButtonPressed, new IntentFilter(BACK_BUTTON_PRESSED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(chatHandleEventsReceiver, new IntentFilter(CHAT_HANDLE_EVENTS_RECEIVER));
    }


    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(timeStepsReceived);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onBackButtonPressed);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(chatHandleEventsReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();


        if (firstLoad)
            progressBar.setVisibility(View.VISIBLE);

        downloadMessages(FIRST_PAGE, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(PriveTalkConstants.CURRENT_CHAT_GUY, String.valueOf(conversationObject.partnerID)).commit();

        if (!KeyboardUtilities.keyboardMeasured() && !isGift) {
            messageEditText.setFocusableInTouchMode(true);
            messageEditText.requestFocus();
            final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
            measureKeyboard();
        }

    }

    @Override
    public void onPause() {

        if (chatMessagesRequest != null)
            chatMessagesRequest.cancel();

        if (sendMessageRequest != null)
            sendMessageRequest.cancel();

        if (messagesParser != null)
            messagesParser.cancel(true);

        if (getGiftsRequest != null)
            getGiftsRequest.cancel();

        if (giftsParser != null)
            giftsParser.cancel(true);

        if (markAsReadRequest != null)
            markAsReadRequest.cancel();

        if (voteRequest != null)
            voteRequest.cancel();

        if (getRequest != null)
            getRequest.cancel();

        if (parseMutualFriends != null)
            parseMutualFriends.cancel(true);

        if (parseUserProfileDetails != null)
            parseUserProfileDetails.cancel(true);

        if (convertImageBitmap != null)
            convertImageBitmap.cancel(true);

        if (notRoyalUserDialog != null)
            notRoyalUserDialog.dismiss();


        mHandler.removeCallbacksAndMessages(null);

        KeyboardUtilities.closeKeyboard(getActivity(), rootView);

        isKeyboardOpen = false;

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PriveTalkConstants.CURRENT_CHAT_GUY, "").commit();

        MessageDatasource.getInstance(getContext()).deleteOldMessages();

        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

                Toast.makeText(getContext(), getString(R.string.error_getting_img), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                imgAbsolutePath = imageFile.getAbsolutePath();
                showCapturedImage(imageFile.getAbsolutePath());

            }
        });

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case FULL_SCREEN_REQUEST:
                    MessageDatasource.getInstance(getContext()).setExpiredMessages();
                    chatAdapter.refreshData(true);
                    break;
            }
        }
    }

    private void initViews() {

        progressBar = rootView.findViewById(R.id.progressBar);

        closeGifts = rootView.findViewById(R.id.closeGifts);

        closeGifts.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
//                handleGiftsLayoutClick(view);
                onMessageEditTextClicked();
                messageEditText.performClick();
                messageEditText.requestFocus();
                KeyboardUtilities.showKeyboard(getActivity(), messageEditText);
            }
        });

        //get all views
        giftsLayout = rootView.findViewById(R.id.giftsLayout);
        chatAddMore = rootView.findViewById(R.id.chatAddMore);
        chatOptions = rootView.findViewById(R.id.chatOptions);
        revealGiftsButton = rootView.findViewById(R.id.revealGiftsButton);
        revealChatOptionsButton = rootView.findViewById(R.id.revealChatOptionsButton);
        chatBar = rootView.findViewById(R.id.chatBar);
        voteYourImpressionText = (TextView) rootView.findViewById(R.id.vote_your_impressionText);

        votesBar = (ViewSwitcher) rootView.findViewById(R.id.votesBar);

        votesBar.setVisibility(MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID) ? View.VISIBLE : View.GONE);
        voteYourImpressionText.setVisibility(MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID) ? View.VISIBLE : View.GONE);

        messageEditText = (KeyboardListenerPriveTalkEditText) rootView.findViewById(R.id.chatMessageEditText);
        zeniosDevil = rootView.findViewById(R.id.zeniosDevil);
        chatRecyclerView = (RecyclerView) rootView.findViewById(R.id.chatRecyclerView);
        sendMessageButton = rootView.findViewById(R.id.sendMessage);
        chatExpirationTimeText = (PriveTalkTextView) rootView.findViewById(R.id.chatExpirationTimeText);
        giftsRecyclerView = (RecyclerView) rootView.findViewById(R.id.giftsRecyclerView);
        dislikeButton = rootView.findViewById(R.id.dislikeButton);
        likeButton = rootView.findViewById(R.id.likeButton);
        myCoins = (PriveTalkTextView) rootView.findViewById(R.id.myCoins);
        chatMessageImageView = (ImageView) rootView.findViewById(R.id.chatMessageImageView);
        pickGalleryPictureButton = (ImageView) rootView.findViewById(R.id.pickGalleyImageButton);
        chatTempImageParentView = rootView.findViewById(R.id.chatTempImageView);
        cameraPhotoButton = (ImageView) rootView.findViewById(R.id.cameraPhotoButton);

        giftsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        giftsAdapter = new GiftsAdapter(getContext());
        giftsRecyclerView.setAdapter(giftsAdapter);
        giftsRecyclerView.setVisibility(View.GONE);

        //seek bar views
        expirationTimeLayout = rootView.findViewById(R.id.expirationTimeLayout);
        revealExpirationTimeButton = rootView.findViewById(R.id.revealExpirationTimeButton);
        expirationTimeSeekBarTrack = (ImageView) rootView.findViewById(R.id.chatExpirationTimeBarTrack);
        expirationTimeSeekBarThumb = (ImageView) rootView.findViewById(R.id.chatExpirationTimeBarThumb);
        expirationTimeSeekBarLayoutParams = (PercentRelativeLayout.LayoutParams) expirationTimeSeekBarTrack.getLayoutParams();
        expirationTimeSeekBarLayoutInfo = expirationTimeSeekBarLayoutParams.getPercentLayoutInfo();

        chatOptions.setVisibility(View.GONE);
        expirationTimeLayout.setVisibility(View.GONE);

        //when clicked the gifts menu slides in from bottom
        revealGiftsButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                handleGiftsLayoutClick(view);
            }
        });

        //when clicked the chat options appears with alpha animation
        revealChatOptionsButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                showOrHideChatOptions();
            }
        });


        chatTempImageParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatTempImageParentView.setVisibility(View.GONE);
                messageEditText.setVisibility(View.VISIBLE);
                sendMessageButton.setEnabled(true);
                sendMessageButton.setClickable(true);
                messageType = TEXT_MESSAGE;
                imageBitmap = null;
                unlockSendMessage();
            }
        });

        cameraPhotoButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                EasyImage.openCamera(ChatFragments.this, TYPE_CAMERA);
            }
        });


        //when clicked expiration time seek bar is revealed
        revealExpirationTimeButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (isExpirationTimeLayoutVisible) {
                    expirationTimeLayout.animate().alpha(0.0f).setDuration(ALPHA_DURATION);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            expirationTimeLayout.setVisibility(View.GONE);
                        }
                    }, ALPHA_DURATION);
                    isExpirationTimeLayoutVisible = false;
                } else {
                    expirationTimeLayout.setVisibility(View.VISIBLE);
                    expirationTimeLayout.animate().alpha(1.0f);
                    isExpirationTimeLayoutVisible = true;
                }
            }
        });


        /*
        Expiration Time Seek Bar Set
         */
        expirationTimeSeekBarThumb.setOnTouchListener(getSeekBarTouchListener());


        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isChatOptionsVisible)
                        showOrHideChatOptions();

                    onMessageEditTextClicked();
                }
            }
        });


        pickGalleryPictureButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                EasyImage.openGallery(ChatFragments.this, TYPE_GALLERY);
            }
        });


        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChatOptionsVisible)
                    showOrHideChatOptions();
                onMessageEditTextClicked();
            }
        });


        messageEditText.setBackPressedListener(new KeyboardListenerPriveTalkEditText.BackPressedListener() {
            @Override
            public void onImeBack(KeyboardListenerPriveTalkEditText editText) {
                if (isKeyboardOpen)
                    closeKeyboard();
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isChatOptionsVisible)
                    showOrHideChatOptions();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        zeniosDevil.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (isKeyboardOpen) {
                    KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_NOTHING, getActivity(), rootView);
                    KeyboardUtilities.closeKeyboard(getActivity(), view);
                    isKeyboardOpen = false;
                }

                reportDialog = new ReportDialog(getActivity(), (RelativeLayout) rootView, LayoutInflater.from(getContext()), String.valueOf(conversationObject.partnerID)).isFromChat(true);
                reportDialog.show();
            }
        });


        //initialize chat recycler view
        chatAdapter = new ChatAdapter(chatRecyclerView, this, conversationObject.partnerID);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendMessageButton.setOnTouchListener(new FadeOnTouchListener() {
                                                 @Override
                                                 public void onClick(View view, MotionEvent event) {


                                                     if (CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() == null) {

                                                         new AlertDialog.Builder(getContext()).setTitle(getString(R.string.profile_picture))
                                                                 .setMessage(getString(R.string.no_profile_photo_message))
                                                                 .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         dialog.dismiss();
                                                                         PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.PROFILE_FRAGMENT);
                                                                     }
                                                                 })
                                                                 .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         dialog.dismiss();
                                                                     }
                                                                 }).create().show();

                                                     } else if (currentUser.verified_photo_front.equals("null")
                                                             && currentUser.verified_photo_left.equals("null")
                                                             && currentUser.verified_photo_right.equals("null")
                                                             && currentUser.photos_verified_rejected == 0
                                                             && !currentUser.photos_verified) {

                                                         AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.verify_only)).
                                                                 setMessage(getString(R.string.not_verified_message)).
                                                                 setPositiveButton(getString(R.string.verify_only), new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         Intent intent = new Intent(getContext(), CameraViewActivity.class);
                                                                         startActivity(intent);
                                                                     }
                                                                 }).setNegativeButton(getString(R.string.later_only), new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 dialog.dismiss();
                                                             }
                                                         });

                                                         notRoyalUserDialog = builder.create();
                                                         notRoyalUserDialog.show();

                                                     } else if (!currentUser.verified_photo_front.equals("null")
                                                             && !currentUser.verified_photo_left.equals("null")
                                                             && !currentUser.verified_photo_right.equals("null")
                                                             && currentUser.photos_verified_rejected == 0
                                                             && !currentUser.photos_verified) {
                                                         new AlertDialog.Builder(getContext())
                                                                 .setTitle(R.string.verification_pending)
                                                                 .setMessage(R.string.please_wait_until_review_your_photos)
                                                                 .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         dialog.dismiss();
                                                                     }
                                                                 }).create().show();
                                                     } else if (!currentUser.verified_photo_front.equals("null")
                                                             && !currentUser.verified_photo_left.equals("null")
                                                             && !currentUser.verified_photo_right.equals("null")
                                                             && currentUser.photos_verified_rejected == 1
                                                             && !currentUser.photos_verified) {

                                                         AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.verify_only)).
                                                                 setMessage(getString(R.string.not_verified_message)).
                                                                 setPositiveButton(getString(R.string.verify_only), new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         Intent intent = new Intent(getContext(), CameraViewActivity.class);
                                                                         startActivity(intent);
                                                                     }
                                                                 }).setNegativeButton(getString(R.string.later_only), new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 dialog.dismiss();
                                                             }
                                                         });

                                                         notRoyalUserDialog = builder.create();
                                                         notRoyalUserDialog.show();

                                                     } else {
                                                         if (messageType == TEXT_MESSAGE && messageEditText.getText().toString().isEmpty())
                                                             return;

                                                         sendPartnerMessage(messageType, imageBitmap);
                                                     }
                                                 }
                                             }


        );

        dislikeButton.setOnTouchListener(new FadeOnTouchListener() {
                                             @Override
                                             public void onClick(View view, MotionEvent event) {
                                                 votesBar.setDisplayedChild(1);
                                                 voteUpOrDown(VOTE_DOWN);
                                             }
                                         }

        );

        likeButton.setOnTouchListener(new FadeOnTouchListener() {
                                          @Override
                                          public void onClick(View view, MotionEvent event) {
                                              votesBar.setDisplayedChild(1);
                                              voteUpOrDown(VOTE_UP);
                                          }
                                      }

        );

        chatAddMore.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ADD_MORE_PT_COINS_FRAGMENT);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);
                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });
    }


    private void closeKeyboard() {
        isKeyboardOpen = false;
        if (isGiftsLayoutVisible) {
            chatOptions.setTranslationY(0);
            chatBar.setTranslationY(0);

//            if (conversationObject != null && !MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID));
            votesBar.setTranslationY(0);
            voteYourImpressionText.setTranslationY(0);


            expirationTimeLayout.setTranslationY(0);
            ((RelativeLayout.LayoutParams) chatRecyclerView.getLayoutParams()).bottomMargin = 0;
            giftsLayout.setTranslationY(0);
            chatRecyclerView.requestLayout();
            isGiftsLayoutVisible = false;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_VIEWS, getActivity(), rootView);
            }
        }, 200);
    }

    /*
    Method to get all needed dimensions
    */
    private void getDimensions() {
        //screen dimensions
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        if (preferences.getInt(PriveTalkConstants.KEYBOARD_HEIGHT, 0) == 0) {
            giftsLayoutHeight = getResources().getDimensionPixelSize(R.dimen.chat_gifts_layout);
            preferencesEditor.putInt(PriveTalkConstants.KEYBOARD_HEIGHT, giftsLayoutHeight);
            preferencesEditor.commit();
        } else
            giftsLayoutHeight = preferences.getInt(PriveTalkConstants.KEYBOARD_HEIGHT, 0);

        //other values
        expirationTimeLayoutWidth = size.x - (2 * getResources().getDimensionPixelSize(R.dimen.expiration_time_layout_margin));
        int expirationTimeSeekBarWidth = expirationTimeLayoutWidth - (2 * getResources().getDimensionPixelSize(R.dimen.expiration_time_seek_bar_margin));
        expirationTimeSeekBarParentWidthRatio = (float) expirationTimeSeekBarWidth / expirationTimeLayoutWidth;
        expirationTimeLayoutMargin = getResources().getDimensionPixelSize(R.dimen.expiration_time_layout_margin);
        expirationTimeSeekBarMargin = getResources().getDimensionPixelSize(R.dimen.expiration_time_seek_bar_margin);

        screenHeight = size.y;

        giftsLayoutParams = (RelativeLayout.LayoutParams) giftsLayout.getLayoutParams();

        giftsLayoutParams.height = giftsLayoutHeight;
        giftsLayoutParams.bottomMargin = -giftsLayoutHeight;

        expirationTimeSeekBarLayoutInfo.widthPercent = expirationTimeSeekBarParentWidthRatio;
        expirationTimeSeekBarTrack.setLayoutParams(expirationTimeSeekBarLayoutParams);

        timeStepThreshold = expirationTimeSeekBarParentWidthRatio / timeStepsObjects.size();
    }


    //Method called when message editText is focused or clicked
    private void onMessageEditTextClicked() {
        isKeyboardOpen = true;

        if (isGiftsLayoutVisible) {
            KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_NOTHING, getActivity(), rootView); //dont adjust views with keyboard open
        } else {
            KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_VIEWS, getActivity(), rootView);

            measureKeyboard();
        }

    }


    private void measureKeyboard() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect r = new Rect();
                View myView = getActivity().getWindow().getDecorView();
                myView.getWindowVisibleDisplayFrame(r);

                int currentKeyboardHeight = screenHeight - r.height() - r.top;

                if (currentKeyboardHeight > 0) {
                    giftsLayoutHeight = currentKeyboardHeight;
                    preferencesEditor.putInt(PriveTalkConstants.KEYBOARD_HEIGHT, giftsLayoutHeight).commit();
                    KeyboardUtilities.setKeyboardMeasured(true);
                }

                giftsLayoutParams.height = giftsLayoutHeight;
                giftsLayoutParams.bottomMargin = -giftsLayoutHeight;
//                giftsLayout.setLayoutParams(giftsLayoutParams);
            }
        }, 500);
    }


    /*
    Called to send a new message to other user
     */
    private synchronized void sendPartnerMessage(int messageType, final Bitmap bmp) {

        lockSendMessage();

        switch (messageType) {
            case TEXT_MESSAGE: {
                JSONObject obj = new JSONObject();
                int position = -1;
                if (expirationTimeSeekBarThumb.getTag(R.id.position_tag) != null)
                    position = (int) expirationTimeSeekBarThumb.getTag(R.id.position_tag);
                if (messageEditText.getText() != null && !messageEditText.getText().toString().isEmpty()) {
                    try {
                        obj.put("receiver", conversationObject.partnerID);
                        obj.put("data", messageEditText.getText().toString());
                        obj.put("lifespan", (position >= 0 && position < timeStepsObjects.size()) ? timeStepsObjects.get(position).seconds : DEFAULT_MSG_LIFESPAN);
                        obj.put("mtype", "txt");
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    executeSendMessageRequest(obj);
                }
            }
            break;

            case IMG_MESSAGE:
                lockMessageImagePreview(true);

                convertImageBitmap = new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {

                        int compressionQualityPercentage = 75; //initialize with 100% quality

                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, compressionQualityPercentage, stream);
                        byte[] bitmapByteArray = stream.toByteArray();


                        return "data:image/jpeg;base64," + Base64.encodeToString(bitmapByteArray, Base64.DEFAULT);
                    }

                    @Override
                    protected void onPostExecute(String pictureData) {
                        JSONObject obj = new JSONObject();
                        int position = -1;
                        if (expirationTimeSeekBarThumb.getTag(R.id.position_tag) != null)
                            position = (int) expirationTimeSeekBarThumb.getTag(R.id.position_tag);

                        try {
                            obj.put("receiver", conversationObject.partnerID);
                            obj.put("data", "");
                            obj.put("lifespan", (position >= 0) ? timeStepsObjects.get(position).seconds : DEFAULT_MSG_LIFESPAN);
                            obj.put("mtype", "img");
                            obj.put("image", pictureData);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            return;
                        }
                        executeSendMessageRequest(obj);
                    }
                }.execute();
                break;

            case GIFT_MESSAGE: {
                lockMessageImagePreview(true);
                JSONObject obj = new JSONObject();
                int position = -1;
                if (expirationTimeSeekBarThumb.getTag(R.id.position_tag) != null)
                    position = (int) expirationTimeSeekBarThumb.getTag(R.id.position_tag);
                try {
                    obj.put("receiver", conversationObject.partnerID);
                    obj.put("data", chatMessageImageView.getTag(R.id.id_tag));
                    obj.put("lifespan", (position >= 0) ? timeStepsObjects.get(position).seconds : DEFAULT_MSG_LIFESPAN);
                    obj.put("mtype", "gift");
                } catch (JSONException ex) {
                    ex.printStackTrace();

                    return;
                }
                executeSendMessageRequest(obj);
            }
            break;
        }
    }


    private synchronized void executeSendMessageRequest(JSONObject obj) {

        messageEditText.setAlpha(0.7f);

        sendMessageRequest = new JsonObjectRequest(Request.Method.POST, Links.CONVERSATIONS + String.valueOf(conversationObject.partnerID) + "/", obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        messageEditText.setEnabled(true);
                        messageEditText.setAlpha(1f);
                        chatTempImageParentView.setVisibility(View.GONE);
                        lockMessageImagePreview(false);
                        messageEditText.setVisibility(View.VISIBLE);

                        switch (messageType) {
                            case TEXT_MESSAGE:
                                messageEditText.setText("");
                                break;

                            case IMG_MESSAGE:
                                imageBitmap = null;
                                if (!messageEditText.getText().toString().isEmpty())
                                    sendPartnerMessage(TEXT_MESSAGE, null);
                                break;

                            case GIFT_MESSAGE:
                                newGetRequest(GET_BALANCE);
                                if (!messageEditText.getText().toString().isEmpty()) {
                                    sendPartnerMessage(TEXT_MESSAGE, null);
                                }
                                break;
                        }

                        messageType = TEXT_MESSAGE;

                        downloadMessages(FIRST_PAGE, true);

                        unlockSendMessage();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        messageEditText.setEnabled(true);
                        messageEditText.setAlpha(1f);
                        unlockSendMessage();
                        lockMessageImagePreview(false);

                        if (error.networkResponse != null) {

                            if (error.networkResponse.statusCode == HttpStatus.SC_PAYMENT_REQUIRED) {

                                KeyboardUtilities.closeKeyboard(getActivity(), rootView);

                                try {
                                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));

                                    if (jsonObject.optString("error_code").equals(PriveTalkConstants.KEY_MUST_BE_ROYAL)) {
                                        showNotRoyalDialog(jsonObject.optString("error_message"));
                                        return;
                                    }

                                    String chatCost = preferences.getString(PriveTalkConstants.KEY_CONVERSATION_COST, "");

                                    String message;

                                    if (chatCost.isEmpty()) {
                                        JSONObject object = new JSONObject(new String(error.networkResponse.data));
                                        message = object.optString("error_message");
                                    } else {
                                        JSONObject object = new JSONObject(chatCost);

                                        message = getString(R.string.you_have_reach_daily_limit)
                                                + String.valueOf(object.optInt("extra_conversations_coin_value"))
                                                + getString(R.string.daily_limit_2)
                                                + String.valueOf(object.optString("extra_conversations"))
                                                + " " + getString(R.string.daily_limit_3);
                                    }


                                    new PurchaseDialog(getActivity(), (RelativeLayout) rootView)
                                            .setMessage(message)
                                            .setPurchaceListener(new PurchaseDialog.PurchaceListener() {
                                                @Override
                                                public void onUsePressed() {
                                                    limitChatProgressView.setVisibility(View.VISIBLE);

                                                    JsonObjectRequest getMoteChats = new JsonObjectRequest(Request.Method.POST, Links.EXTRA_CHATS, new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            limitChatProgressView.setVisibility(View.GONE);
                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                                            builder1.setMessage(getString(R.string.succesfully_added_chats))
                                                                    .setNeutralButton(getString(R.string.okay), null).create().show();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                            limitChatProgressView.setVisibility(View.GONE);

                                                            try {
                                                                String errorData = new String(error.networkResponse.data);
                                                                JSONObject obj = new JSONObject(errorData);
                                                                Toast.makeText(getContext(), obj.optString("detail"), Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
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

                                                    VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getMoteChats);
                                                }

                                                @Override
                                                public void onPurchacePressed() {
                                                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.ADD_MORE_PT_COINS_FRAGMENT);
                                                }
                                            }).show();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                return;
                            } else {
                                try {
                                    String errorData = new String(error.networkResponse.data);
                                    JSONObject obj = new JSONObject(errorData);
                                    Toast.makeText(getContext(), obj.optString("detail"), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                }
                return super.parseNetworkError(volleyError);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                return headers;
            }
        };

        if (messageType == IMG_MESSAGE) {
            DefaultRetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            sendMessageRequest.setRetryPolicy(policy);
        }
        sendMessageRequest.setTag(MESSAGE_REQUEST_TAG);

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(sendMessageRequest);
    }


    /*
    private method to download chat messages on background
     */
    private void downloadMessages(final String page, final boolean fetchOnlyLatest) {

        headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

        chatMessagesRequest = new JsonObjectRequestWithResponse(Request.Method.GET,
                (page.equals(FIRST_PAGE)) ? (Links.CONVERSATIONS + String.valueOf(conversationObject.partnerID)) : page,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (firstLoad) {
                            progressBar.setVisibility(View.GONE);
                            firstLoad = false;
                        }


                        int statusCode = response.optInt("statusCode");
                        final JSONObject data = response.optJSONObject("data");

                        if (statusCode == 304)
                            return;

                        messagesParser = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {

                                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                                CurrentUser currentUser = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo();

                                int userId = currentUser == null ? 0 : currentUser.userID;

                                if (page.equals(FIRST_PAGE) && !fetchOnlyLatest)
                                    MessageDatasource.getInstance(getContext()).deleteMessages(userId, conversationObject.partnerID);

                                JSONArray array = data.optJSONArray("results");
                                List<MessageObject> objects = new ArrayList<>();


                                for (int i = 0; i < array.length(); i++)
                                    objects.add(new MessageObject(array.optJSONObject(i), userId));

                                MessageDatasource.getInstance(getContext()).saveMessages(objects);

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {

                                if (page.equals(FIRST_PAGE))
                                    markMessageAsRead(conversationObject.partnerID);

                                String nextPage = data.optString("next");
                                if (nextPage != null && !nextPage.isEmpty() && !nextPage.equals("null") && !fetchOnlyLatest)
                                    downloadMessages(nextPage, false);
                                else
                                {
                                    chatAdapter.refreshData(true);
                                    votesBar.setVisibility(MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID) ? View.VISIBLE : View.GONE);
                                    voteYourImpressionText.setVisibility(MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID) ? View.VISIBLE : View.GONE);

//                                    if (MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID))
//                                        votesBar.setDisplayedChild(0);
                                }

                                votesBar.setVisibility(MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID) ? View.VISIBLE : View.GONE);
                                voteYourImpressionText.setVisibility(MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID) ? View.VISIBLE : View.GONE);
//                                if (MessageDatasource.getInstance(getContext()).checkIfCanVote(conversationObject.partnerID))
//                                    votesBar.setDisplayedChild(0);

                            }

                        }.execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (firstLoad) {
                            progressBar.setVisibility(View.GONE);
                            firstLoad = false;
                        }

                        if (error.networkResponse != null) {

                            if (error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND)
                                return;
                            try {
                                String errorData = new String(error.networkResponse.data);
                                JSONObject obj = new JSONObject(errorData);
                                Toast.makeText(getContext(), obj.optString("details"), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                }

                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(chatMessagesRequest);
    }


    /*
    Method to mark messages as read. called when the first page(most recent) of messages is downloaded
     */
    private void markMessageAsRead(int partnerID) {

        markAsReadRequest = new JsonObjectRequest(Request.Method.POST, Links.getMarkAsRead(partnerID),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(markAsReadRequest);
    }


    /*
       Method to vote the other user
       direction can only be one of "up" or "down"
     */
    private void voteUpOrDown(final String direction) {

        JSONObject obj = new JSONObject();

        try {
            obj.put("direction", direction);
        } catch (JSONException ex) {
            ex.printStackTrace();

        }
        voteRequest = new JsonObjectRequest(Request.Method.POST, Links.getVoteUrl(conversationObject.partnerID), obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optBoolean("success")) {

                            if (response.optString("vote").equals(VOTE_UP))
                                Toast.makeText(getContext(), getString(R.string.you_have_voted_up), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), getString(R.string.you_have_voted_down), Toast.LENGTH_SHORT).show();

                            votesBar.setVisibility(View.GONE);
                            voteYourImpressionText.setVisibility(View.GONE);


                            MessageDatasource.getInstance(PriveTalkApplication.getInstance()).setUserVoted(conversationObject.partnerID);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                votesBar.showPrevious();
                voteYourImpressionText.setVisibility(View.VISIBLE);
                if (error.networkResponse != null) {
                    String data = new String(error.networkResponse.data);
//                    Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                }
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(voteRequest);
    }


    /*
    Method to get gifts from server
     */
    private void getGifts(final String page) {


        headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

        getGiftsRequest = new JsonArrayRequest(Request.Method.GET, Links.GIFTS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        giftsParser = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                List<GiftObject> giftObjects = new ArrayList<>();

                                for (int i = 0; i < response.length(); i++)
                                    giftObjects.add(new GiftObject(response.optJSONObject(i), i));
                                if (page.equals("1") && giftObjects.size() > 0)
                                    GiftsDatasource.getInstance(getContext()).deleteGifts();
                                GiftsDatasource.getInstance(getContext()).saveGifts(giftObjects);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                giftsAdapter.refreshData();
                            }
                        }.execute();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                }

                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getGiftsRequest);
    }


    private synchronized void newGetRequest(final int request) {
        String URL = "";


        switch (request) {
            case GET_BALANCE:
                URL = Links.COINS_BALANCE;
                break;
            case GET_OTHER_USER_INFO:
                URL = Links.GET_COMMUNITY + String.valueOf(conversationObject.partnerID);
                break;
        }

//        System.out.println(request + " URl: " + URL);

        getRequest = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                switch (request) {
                    case GET_BALANCE:
                        coinsBalance = response.optInt("coins");
                        myCoins.setText(String.valueOf(coinsBalance) + "{b}c{/b}");
                        break;
                    case GET_OTHER_USER_INFO:
                        parseOtherUserDetails(response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String data = new String(error.networkResponse.data);
                    if (!data.isEmpty())
                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null)
                    switch (request) {
                        case GET_BALANCE:
                            break;
                    }
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
        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getRequest);
    }


    /*
    Method called when gifts layout view is clicked
     */
    private void handleGiftsLayoutClick(View view) {
        KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_NOTHING, getActivity(), rootView);

        if (isKeyboardOpen) {
            giftsRecyclerView.setVisibility(View.VISIBLE);
            changeGiftsLayout(GIFTSACTIONS.SHOW_WITHOUT_ANIMATION);
            KeyboardUtilities.closeKeyboard(getActivity(), view);
            isKeyboardOpen = false;
            isGiftsLayoutVisible = true;
        } else if (isGiftsLayoutVisible) {
            //remove giftsLayout with animation
            giftsRecyclerView.setVisibility(View.GONE);
            changeGiftsLayout(GIFTSACTIONS.HIDE_GIFTS);
            isGiftsLayoutVisible = false;
        } else {
            giftsRecyclerView.setVisibility(View.VISIBLE);
            changeGiftsLayout(GIFTSACTIONS.SHOW_GIFTS);
            isGiftsLayoutVisible = true;
        }
    }


    /*
    Method called when chat options icon is checked
     */
    private void showOrHideChatOptions() {
        if (isChatOptionsVisible) {
            chatOptions.animate().alpha(0.0f).setDuration(ALPHA_DURATION);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chatOptions.setVisibility(View.GONE);
                }
            }, ALPHA_DURATION);

            if (isExpirationTimeLayoutVisible) {
                expirationTimeLayout.animate().alpha(0.0f).setDuration(ALPHA_DURATION);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        expirationTimeLayout.setVisibility(View.GONE);
                    }
                }, ALPHA_DURATION);
                isExpirationTimeLayoutVisible = false;
            }
            isChatOptionsVisible = false;
        } else {
            chatOptions.setVisibility(View.VISIBLE);
            chatOptions.animate().alpha(1.0f).setDuration(ALPHA_DURATION);
            isChatOptionsVisible = true;
        }
    }


    private void changeGiftsLayout(GIFTSACTIONS action) {

        switch (action) {
            case SHOW_WITHOUT_ANIMATION: {
                chatOptions.setTranslationY(-giftsLayoutHeight);
                chatBar.setTranslationY(-giftsLayoutHeight);

                votesBar.setTranslationY(-giftsLayoutHeight);
                voteYourImpressionText.setTranslationY(-giftsLayoutHeight);

                expirationTimeLayout.setTranslationY(-giftsLayoutHeight);
                ((RelativeLayout.LayoutParams) chatRecyclerView.getLayoutParams()).bottomMargin = giftsLayoutHeight;

                giftsLayout.setTranslationY(-giftsLayoutHeight);
                giftsLayout.requestLayout();
            }
            break;

            case HIDE_GIFTS: {
                //remove giftsLayout with animation
                giftsLayout.invalidate();
                chatOptions.animate().translationY(0).setDuration(200);
                giftsLayout.animate().translationY(0).setDuration(200);
                chatBar.animate().translationY(0).setDuration(200);
                votesBar.animate().translationY(0).setDuration(200);
                voteYourImpressionText.animate().translationY(0).setDuration(200);

                expirationTimeLayout.animate().translationY(0).setDuration(200);
                ((RelativeLayout.LayoutParams) chatRecyclerView.getLayoutParams()).bottomMargin = 0;
                chatRecyclerView.requestLayout();
                isGiftsLayoutVisible = false;
            }
            break;

            case SHOW_GIFTS: {
                //appear giftsLayout with animation
                giftsLayout.animate().translationY(-giftsLayoutHeight).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(200);
                chatOptions.animate().translationY(-giftsLayoutHeight).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(200);
                chatBar.animate().translationY(-giftsLayoutHeight).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(200);
                votesBar.animate().translationY(-giftsLayoutHeight).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(200);
                voteYourImpressionText.animate().translationY(-giftsLayoutHeight).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(200);
                expirationTimeLayout.animate().translationY(-giftsLayoutHeight).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(200);
                ((RelativeLayout.LayoutParams) chatRecyclerView.getLayoutParams()).bottomMargin = giftsLayoutHeight;

                giftsLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        giftsLayout.requestLayout();
                    }
                }, 200);
            }
            break;
        }
    }


    /*
    Get Common Facebook Friends
     */
    private void getCommonFacebookFriends(String fbID) {
        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getContext().getApplicationContext());

        Bundle params = new Bundle();
        params.putString("fields", "context.fields(mutual_friends)");

        if (fbID != null)
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    fbID,
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(final GraphResponse response) {
//                            System.out.println("Common Friends Response: " + response);
                            parseMutualFriends = new AsyncTask<Void, List<MutualFriendsObject>, List<MutualFriendsObject>>() {
                                @Override
                                protected List<MutualFriendsObject> doInBackground(Void... params) {
                                    List<MutualFriendsObject> mutualFriends = new ArrayList<>();
                                    if (response.getJSONObject() != null) {
                                        try {
                                            JSONArray array = response.getJSONObject().optJSONObject("context").optJSONObject("mutual_friends").optJSONArray("data");
                                            for (int i = 0; i < array.length(); i++)
                                                mutualFriends.add(new MutualFriendsObject(array.optJSONObject(i)));
                                        } catch (NullPointerException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                    return mutualFriends;
                                }

                                @Override
                                protected void onPostExecute(List<MutualFriendsObject> aVoid) {
                                    if (!isCancelled()) {
                                        chatAdapter.commonFriendsCount = aVoid.size();
                                        chatAdapter.notifyDataSetChanged();
                                        mutualFriendsObjects = aVoid;
                                    }
                                }
                            }.execute();
                        }
                    }
            ).executeAsync();
    }


    private void parseOtherUserDetails(final JSONObject response) {
        parseUserProfileDetails = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                otherUserObject = new UserObject(response);
                commonInterest = InterestObject.getCommonInterests(otherUserObject.interests, currentUser.currentUserDetails.interests);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                chatAdapter.setOtherUserObject(otherUserObject);
                getCommonFacebookFriends(otherUserObject.fbID);
                chatAdapter.commonInterestsCount = InterestObject.getInterestsCount(commonInterest);
                chatAdapter.notifyDataSetChanged();
                Intent intent = new Intent(PriveTalkConstants.CHANGE_ACTIONBAR_TITLE);
                intent.putExtra(PriveTalkConstants.ACTION_BAR_TITLE, otherUserObject.name.split(" ")[0]);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        }.execute();
    }


    private void showNotRoyalDialog(String message) {

        if (notRoyalUserDialog != null && notRoyalUserDialog.isShowing())
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.not_royal_user))
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.wait, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.royal_user_plans_action_bar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                        intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ROYAL_USER_PLANS_FRAGMENT_ID);
                        intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);
                        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
                    }
                });

        notRoyalUserDialog = builder.create();
        notRoyalUserDialog.show();
    }


    /*
    Returns touch listener for seekbar thumb
     */
    private TranslationTouchListener getSeekBarTouchListener() {
        return new TranslationTouchListener() {
            float newValue = 0;
            int position;

            @Override
            public void OnViewPressed(View view) {
                view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(200);
            }

            @Override
            public void OnMove(View view, float mDownRawX, float mDownRawY, float deltaX, float deltaY) {
                //Calculating the percent width(= new length/total length)
                newValue = ((mDownRawX + deltaX - expirationTimeLayoutMargin - expirationTimeSeekBarMargin) / expirationTimeLayoutWidth);
                newValue = (newValue < 0) ? 0 : newValue;
                newValue = (newValue > expirationTimeSeekBarParentWidthRatio) ? expirationTimeSeekBarParentWidthRatio : newValue;

                //if dx equals threshold(total X/timestepsSize) then calculate the move
                if (Math.abs(expirationTimeSeekBarLayoutInfo.widthPercent - newValue) > timeStepThreshold) {
                    position = Math.round(newValue / timeStepThreshold);
                    expirationTimeSeekBarLayoutInfo.widthPercent = position * timeStepThreshold;
                }

                expirationTimeSeekBarThumb.setTag(R.id.position_tag, position - 1);

                if (!currentUser.royal_user) {
                    showNotRoyalDialog(getString(R.string.you_have_to_be_a_royal_user_message));
                    return;
                }

                if (position != 0) {
                    int totalSeconds = timeStepsObjects.get(position - 1).seconds;
                    int days = totalSeconds / 86400;
                    totalSeconds = (totalSeconds - (days * 86400));
                    int hours = totalSeconds / 3600;
                    totalSeconds = (totalSeconds - (hours * 3600));
                    int min = totalSeconds / 60;
                    totalSeconds = (totalSeconds - (min * 60));

                    String expirationTimeEstimated = String.valueOf(days) + getString(R.string.day_letter) +
                            ((hours < 10) ? "0" : "") + String.valueOf(hours) + getString(R.string.hour_letter) +
                            ((min < 10) ? "0" : "") + String.valueOf(min) + getString(R.string.minute_letter) +
                            ((totalSeconds < 10) ? "0" : "") + String.valueOf(totalSeconds) + getString(R.string.second_letter);

                    String expirationTimeEstimatedHint = getString(R.string.expires_in) + " "
                            + (days > 0 ? String.valueOf(days) + " " + (getString(R.string.message_time_day) + ((days == 1 ? getString(R.string.message_time_day_end) : getString(R.string.message_time_days_end)) + " ")) : "")
                            + (hours > 0 ? String.valueOf(hours)  + (getString(R.string.message_hour) + ((hours == 1 ? getString(R.string.message_time_day_end) : getString(R.string.message_time_days_end)) + " ")): "")
                            + (min > 0 ? String.valueOf(min)  + (getString(R.string.message_minute) + ((min == 1 ? getString(R.string.message_time_minute_end) : getString(R.string.message_time_minutes_end)) + " ")) : "")
                            + (totalSeconds > 0 ? String.valueOf(totalSeconds) + getString(R.string.seconds_message) : "");


                    chatExpirationTimeText.setText(expirationTimeEstimated);
                    messageEditText.setHint(expirationTimeEstimatedHint);
//                    messageEditText.clearFocus();
                }
                expirationTimeSeekBarTrack.setLayoutParams(expirationTimeSeekBarLayoutParams);
            }

            @Override
            public void OnRelease(View view) {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
            }

        };
    }


    private void getChatCost() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Links.CONVERSATION_COST, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                preferences.edit().putLong(PriveTalkConstants.KEY_CONVERSATION_COST_UPDATE, System.currentTimeMillis()).commit();
                preferences.edit().putString(PriveTalkConstants.KEY_CONVERSATION_COST, response.toString()).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
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
        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(jsonObjectRequest);
    }


    private void showCapturedImage(final String imgAbsolutePath1) {
        try {
            mainViewSwitcher.setDisplayedChild(VIEW_CAMERA_IMG);
            View rotateRight = mainViewSwitcher.findViewById(R.id.rotateRight);
            final View cropDoneView = mainViewSwitcher.findViewById(R.id.cropDone);
            View cropCancelledView = mainViewSwitcher.findViewById(R.id.cropCancel);
            final ImageView cropImageView = (ImageView) mainViewSwitcher.findViewById(R.id.cropImageView);

            imageBitmap = BitmapUtilities.autoRotateAndScaleBitmap(imgAbsolutePath1);

            if (imageBitmap == null)
                imageBitmap = BitmapUtilities.getScaledBitmapIfLarge(BitmapFactory.decodeFile(imgAbsolutePath1));


            cropImageView.setImageBitmap(imageBitmap);

            rotateRight.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                    cropImageView.setImageBitmap(imageBitmap);
                }
            });

            cropDoneView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    imgAbsolutePath = null;
                    imageBitmap = BitmapUtilities.getSmallScaledBitmap(imageBitmap);
                    mainViewSwitcher.setDisplayedChild(VIEW_CHAT);
                    chatTempImageParentView.setVisibility(View.VISIBLE);
                    chatMessageImageView.setImageBitmap(imageBitmap);
                    messageType = IMG_MESSAGE;
                }
            });

            cropCancelledView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    imgAbsolutePath = null;
                    mainViewSwitcher.setDisplayedChild(VIEW_CHAT);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
