package com.privetalk.app.mainflow.fragments;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.ConversationsDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.HotMatchesDatasource;
import com.privetalk.app.database.datasource.MessageDatasource;
import com.privetalk.app.database.objects.ConversationObject;
import com.privetalk.app.database.objects.HotMatchesObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.utilities.AppBarStateChangeListener;
import com.privetalk.app.utilities.BitmapUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.LongPressAndClickListener;
import com.privetalk.app.utilities.MyGridLayoutManager;
import com.privetalk.app.utilities.MyLinearLayoutManager;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class MessagesFragment extends FragmentWithTitle {

    private String title;
    private View rootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PromotedUsersAdapter mAdapter;

    private RecyclerView inboxRecyclerView;
    private MyLinearLayoutManager inboxLayoutManager;
    private InboxRecyclerAdapter inboxRecyclerAdapter;
    private int topRecyclerHeight = 0;
    private View progressBar;
    private boolean deleteConversationLock;
    private List<HotMatchesObject> hotMatchesObjectList;
    private View shadowView;

    private AppBarLayout inboxAppBar;

    private BroadcastReceiver promotedUsersDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.refreshData();
        }
    };

    private BroadcastReceiver conversationsDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            inboxRecyclerAdapter.refreshData();
        }
    };
    private JsonObjectRequest hideConversation;
    private Handler handler;
    private Runnable runnable;
    private View addmeView;
    //for Hot matches
    private GridRecyclerAdapter mAdapter2;
    private LinearLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView1;
    private Handler hand;
    public static Boolean CAN_USE_TOUCH = true;
    private CircularImageView bitmapImageView;
    private float initialY = 0;
    private float intitialX = 0;
    private View removeMatchBucket;
    private Animation bucketAnimation;

    //for Hot matches
    private BroadcastReceiver hotMatchesDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            if (hotMatchesObjectList == null)
                hotMatchesObjectList = new ArrayList<>();
            else
                hotMatchesObjectList.clear();

            hotMatchesObjectList.addAll(HotMatchesDatasource.getInstance(getContext()).getHotMatches());
            mAdapter2.notifyDataSetChanged();
        }
    };

    private class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder> {

        private int colorGreen;
        private int colorWhite;

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public CircleImageView userProfilePicture;
            public PriveTalkTextView userName;
            public ImageView verifiedPhotos, royalUser, socialVerified;
            public PriveTalkTextView userAge;
            public ImageView userStatus;
            public View gradientShadow;
            private View whiteFrame;
            public View royalUserIcon;

            public ViewHolder(View v) {
                super(v);
                view = v;
                whiteFrame = view.findViewById(R.id.whiteFrame);
                userProfilePicture = (CircleImageView) v.findViewById(R.id.hotmatchesProfilePicture);
                userName = (PriveTalkTextView) v.findViewById(R.id.hotmatchesUserName);
                verifiedPhotos = (ImageView) v.findViewById(R.id.hotmatchesUserTick1);
                royalUser = (ImageView) v.findViewById(R.id.hotmatchesUserTick3);
                socialVerified = (ImageView) v.findViewById(R.id.hotmatchesUserTick2);
                userAge = (PriveTalkTextView) v.findViewById(R.id.hotmatchesUserAge);
                userStatus = (ImageView) v.findViewById(R.id.hotmatchesUserStatus);
                gradientShadow = v.findViewById(R.id.gradientShadow);
                royalUserIcon = v.findViewById(R.id.hotmatchesCrow);
            }

        }


        public GridRecyclerAdapter() {
            colorGreen = ContextCompat.getColor(getContext(), R.color.tick_green);
            colorWhite = Color.WHITE;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public GridRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                    int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.matches_screen_hot_matches, parent, false);

            //set new Long press and click listener for my view
            v.setOnTouchListener(newLongPressAndClickListener());

            GridRecyclerAdapter.ViewHolder vh = new GridRecyclerAdapter.ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(GridRecyclerAdapter.ViewHolder holder, int position) {

            holder.view.setTag(R.id.user_id_tag, hotMatchesObjectList.get(position).matchID);
            holder.view.setTag(R.id.position_tag, position);

            holder.view.setVisibility(View.VISIBLE);
            holder.whiteFrame.setVisibility(View.VISIBLE);
            holder.gradientShadow.setVisibility(View.VISIBLE);
            holder.royalUserIcon.setVisibility(hotMatchesObjectList.get(position).royalUser ? View.VISIBLE : View.INVISIBLE);

            //photos verified
            holder.verifiedPhotos.setColorFilter(hotMatchesObjectList.get(position).photosVerified ? colorGreen : colorWhite);
            //royal user
            holder.royalUser.setColorFilter(hotMatchesObjectList.get(position).royalUser ? colorGreen : colorWhite);
            //social media verified
            holder.socialVerified.setColorFilter(hotMatchesObjectList.get(position).socialVerified ? colorGreen : colorWhite);

            Glide.with(getContext()).load(hotMatchesObjectList.get(position).profilePhoto.medium_square_thumb).error(R.drawable.dummy_img).dontAnimate().into(holder.userProfilePicture);

            holder.userName.setText(hotMatchesObjectList.get(position).name.split(" ")[0]);
            holder.userAge.setText(String.valueOf(hotMatchesObjectList.get(position).age));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return hotMatchesObjectList.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getString(R.string.messages);
        }

        mAdapter = new PromotedUsersAdapter(getContext());
        PriveTalkUtilities.fetchPromotedUsers();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (PriveTalkUtilities.checkIfPromotedUsersIsScrollable(mRecyclerView, mAdapter)) {
                    mRecyclerView.scrollBy(1, 0);
                }
                handler.postDelayed(this, 15);
            }
        };

        runnable = new Runnable() {
            @Override
            public void run() {
                if (PriveTalkUtilities.checkIfPromotedUsersIsScrollable(mRecyclerView1, mAdapter)) {
                    mRecyclerView1.scrollBy(1, 0);
                }
                handler.postDelayed(this, 15);
            }
        };
    }
    //for Hot matches
    private LongPressAndClickListener newLongPressAndClickListener() {
        return new LongPressAndClickListener(hand) {
            @Override
            public void OnClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("other_user_id", ((int) v.getTag(R.id.user_id_tag)));
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
            }

            @Override
            public void OnLongPress(View v, float downX, float downY) {

                //set touch boolean tag
                v.setTag(R.id.touch_tag, CAN_USE_TOUCH);

                if (CAN_USE_TOUCH)
                    CAN_USE_TOUCH = false;

                //chech if touch is possible
                if ((boolean) v.getTag(R.id.touch_tag)) {

                    shadowView.setVisibility(View.VISIBLE);

                    //disable recyclerview touch events
                    mRecyclerView1.setEnabled(false);
                    mRecyclerView1.setClickable(false);
                   // mGridLayoutManager.setCanScroll(false);


                    //get view to create new bitmap
                    tempView = v.findViewById(R.id.extraLayout);
                    gradientShadowView = v.findViewById(R.id.gradientShadow);
                    tempImageView = ((CircleImageView) v.findViewById(R.id.hotmatchesProfilePicture));
                    tempTextView = ((PriveTalkTextView) v.findViewById(R.id.hotmatchesUserName));


                    tempImageView.setImageBitmap(BitmapUtilities.getBitmapFromView(tempView));

                    tempTextView.setBackgroundColor(Color.TRANSPARENT);
                    gradientShadowView.setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.whiteFrame).setVisibility(View.INVISIBLE);

                    bitmapImageView = new CircularImageView(getContext());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(v.getWidth(),
                            v.findViewById(R.id.hotMatchesMainView).getHeight());


                    //calculate left margin
                    params.leftMargin = (v.getLeft());

                    //calculate top margin
                    params.topMargin = (v.getTop() <= 0 && v.getPaddingTop() > 0) ?
                            v.getTop() + topRecyclerHeight :
                            v.getTop() - topRecyclerHeight - getResources().getDimensionPixelSize(R.dimen.hot_matches_padding) + (v.getHeight() / 2);

                    //if view is partly hidden then show all
                    params.topMargin = (params.topMargin < 0) ? 0 : params.topMargin;

                    //set layout params to the new imageview
                    bitmapImageView.setLayoutParams(params);

                    //create a bitmap image of the selected view and set it to the new draggable image view
                    bitmapImageView.setImageBitmap(BitmapUtilities.getBitmapFromView(v.findViewById(R.id.hotMatchesMainView)));
                    bitmapImageView.setBorderRadius(0.0f);
                    bitmapImageView.setBorderColor(Color.TRANSPARENT);

                    //add to parent view
                    ((RelativeLayout) rootView).addView(bitmapImageView);


                    tempTextView.setBackgroundColor(Color.WHITE);

                    //initial view invisible
                    v.setVisibility(View.INVISIBLE);

                    bitmapImageView.post(new Runnable() {
                        @Override
                        public void run() {

                            //calculate DX & DY distances from the new created image view to the target view (bucket)
                            initialY = bitmapImageView.getY() + bitmapImageView.getHeight() / 2;
                            intitialX = bitmapImageView.getX() + bitmapImageView.getWidth() / 2;
                            targetPositionY = removeMatchBucket.getTop() + removeMatchBucket.getHeight() / 2;
                            targetPositionX = removeMatchBucket.getLeft() + removeMatchBucket.getWidth() / 2;

                            float DY;
                            float DX;

                            if (targetPositionY > initialY)
                                DY = targetPositionY - initialY;
                            else
                                DY = initialY - targetPositionY;

                            if (targetPositionX > intitialX)
                                DX = targetPositionX - intitialX;
                            else
                                DX = intitialX - targetPositionX;

                            //pithagorio - calculate distance
                            initialDistanceFromCarbageBucket = (float) Math.sqrt((DY * DY) + (DX * DX));
                        }
                    });

                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(200);

                    AnimatorSet set = new AnimatorSet(); //this is your bucketAnimation set.

                    ValueAnimator scaleUp = ValueAnimator.ofFloat(1.0f, 1.1f);
                    scaleUp.setDuration(100);
                    scaleUp.setInterpolator(new BounceInterpolator()); //remove this if you prefer default interpolator
                    scaleUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Float newValue = (Float) valueAnimator.getAnimatedValue();
                            bitmapImageView.setScaleY(newValue);
                            bitmapImageView.setScaleX(newValue);
                        }
                    });

                    set.play(scaleUp);
                    set.start();

                    removeMatchBucket.setVisibility(View.VISIBLE);
                    removeMatchBucket.startAnimation(bucketAnimation);
                }
            }

            @Override
            public void OnRelease(final View v, float downX, float downY) {


                if ((boolean) v.getTag(R.id.touch_tag)) {

                    shadowView.setVisibility(View.INVISIBLE);

                    if (canDelete) {
                       // hideMatch((int) v.getTag(R.id.user_id_tag), (int) v.getTag(R.id.position_tag), v); //TBD
                        ((RelativeLayout) rootView).removeView(bitmapImageView);
//
//                        mRecyclerView1.setEnabled(true);
//                        mRecyclerView1.setClickable(true);
//                        mGridLayoutManager.setCanScroll(true);
//
//                        hotMatchesObjectList.remove((int) v.getTag(R.id.position_tag));
//                        mAdapter2.notifyItemRemoved((int) v.getTag(R.id.position_tag));
//                        mAdapter2.notifyItemRangeChanged((int) v.getTag(R.id.position_tag), hotMatchesObjectList.size());
//
//                        Toast.makeText(getContext(), getString(R.string.deleted_match), Toast.LENGTH_SHORT).show();


                    } else {
                        bitmapImageView.animate().scaleX(1.0f).scaleY(1.0f).translationX(0).translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(250);
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out_2);
                        removeMatchBucket.setVisibility(View.VISIBLE);
                        removeMatchBucket.setAnimation(animation);
                        CAN_USE_TOUCH = true;
                        initialY = 0;
                        intitialX = 0;

                        hand.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                removeMatchBucket.setVisibility(View.GONE);
                            }
                        }, 300);

                        hand.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bitmapImageView.setVisibility(View.GONE);
                                ((RelativeLayout) rootView).removeView(bitmapImageView);
                                v.setVisibility(View.VISIBLE);

                                mRecyclerView1.setEnabled(true);
                                mRecyclerView1.setClickable(true);
                              //  mGridLayoutManager.setCanScroll(true);
                            }
                        }, 250);


                    }
//                        System.out.println("Item Released");

                    targetPositionY = 0;
                    targetPositionX = 0;
                    initialDistanceFromCarbageBucket = 0;
                }
            }

            @Override
            public void OnMove(View v, float downX, float downY, float deltaX, float deltaY) {

                if ((boolean) v.getTag(R.id.touch_tag)) {

                    bitmapImageView.setTranslationX(deltaX);
                    bitmapImageView.setTranslationY(deltaY);

                    float dy;
                    float dx;

                    float myY = bitmapImageView.getY() + bitmapImageView.getHeight() / 2;
                    float myX = bitmapImageView.getX() + bitmapImageView.getWidth() / 2;

                    if (targetPositionY > myY)
                        dy = targetPositionY - myY;
                    else
                        dy = myY - targetPositionY;

                    if (targetPositionX > myX)
                        dx = targetPositionX - myX;
                    else
                        dx = myX - targetPositionX;

                    //ipologizo arxiki apostasi kai apostasi kathos metakinite. meta diero
                    float hypotunousa = (float) Math.sqrt((dy * dy) + (dx * dx));

                    /*
                    move value is used to find how close the view is to the target:
                    value >= 1 => view is at it's initial place or more far away
                    value < 1 closer to target
                     */
                    moveValue = hypotunousa / initialDistanceFromCarbageBucket;

                    //if view x and y matches bucket height and width then canDelete = true
                    canDelete = ((myX >= removeMatchBucket.getLeft() && myX <= removeMatchBucket.getRight())
                            && (myY >= removeMatchBucket.getTop() && myY <= removeMatchBucket.getBottom()));

                    if (moveValue > 1)
                        moveValue = 1;
                    else if (moveValue < 0.4)
                        moveValue = 0.4f;


                    bitmapImageView.setAlpha(moveValue);
                    bitmapImageView.setScaleX(moveValue * 1.1f);
                    bitmapImageView.setScaleY(moveValue * 1.1f);
                }
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_prive_inbox, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        rootView.setScrollContainer(false);

        initViews();

        Glide.with(getContext())
                .load(CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() != null ?
                        CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto().square_thumb : "")
                .error(R.drawable.dummy_img).into((ImageView) rootView.findViewById(R.id.addMeImage));


        progressBar.setVisibility(View.VISIBLE);

        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.CONVERSATIONS, PriveTalkConstants.BROADCAST_CONVERSATIONS_DOWNLOADED, null, new JSONObject());

        return rootView;
    }


    @Override
    public void onResume() {
        handler.post(runnable);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(conversationsDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_CONVERSATIONS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(hotMatchesDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_HOT_MATCHES_DOWNLOADED));
        PriveTalkUtilities.badgesRequest(true, PriveTalkConstants.MenuBadges.MESSAGES_BAGE);
        super.onResume();
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(conversationsDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(hotMatchesDownloaded);
        KeyboardUtilities.closeKeyboard(getActivity(), rootView);

        if (hideConversation != null)
            hideConversation.cancel();

        if (handler != null)
            handler.removeCallbacksAndMessages(null);

        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PriveTalkConstants.ACTION_BAR_TITLE, title);
    }


    @Override
    protected String getActionBarTitle() {
        return title;
    }


    //aloupies koinos gnostes os laspologies
    private void initViews() {
        //for Hot matches
        shadowView = rootView.findViewById(R.id.shadowView);
        removeMatchBucket = rootView.findViewById(R.id.removeMatch);
        bucketAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_in_2);

        progressBar = rootView.findViewById(R.id.progressBar);
        addmeView = rootView.findViewById(R.id.addMePromote);
        addmeView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });

//        inboxSearchView = rootView.findViewById(R.id.inboxSearchView);
        inboxAppBar = (AppBarLayout) rootView.findViewById(R.id.inboxAppBar);
//        mToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.inboxCollapsing);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.inboxRecyclerView);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, mRecyclerView);

        //after top (horizontal) recyclerview draws, get height to use for padding and margins to other views
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                topRecyclerHeight = mRecyclerView.getHeight() /*+ 350*/; //for Hot matches

                inboxRecyclerView.setAdapter(inboxRecyclerAdapter);

                CoordinatorLayout.LayoutParams params2 = (CoordinatorLayout.LayoutParams) inboxRecyclerView.getLayoutParams();
                params2.setMargins(0, -topRecyclerHeight, 0, topRecyclerHeight);
                inboxRecyclerView.setLayoutParams(params2);

                //set top margin to appbarlayout
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) inboxAppBar.getLayoutParams();
                params.setMargins(0, topRecyclerHeight, 0, 0);
                inboxAppBar.setLayoutParams(params);
            }
        });

        inboxRecyclerView = (RecyclerView) rootView.findViewById(R.id.inboxUsersProfiles);
        inboxLayoutManager = new MyLinearLayoutManager(getContext());
        inboxRecyclerView.setLayoutManager(inboxLayoutManager);
        inboxRecyclerAdapter = new InboxRecyclerAdapter();

        inboxAppBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED)
                    appBarLayout.bringToFront();
                else {
                    ((CoordinatorLayout) rootView).removeView(appBarLayout);
                    ((CoordinatorLayout) rootView).addView(appBarLayout);
                }
//                System.out.println(state.name());
            }
        });

        PriveTalkEditText editText = (PriveTalkEditText) rootView.findViewById(R.id.searchContext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    inboxRecyclerAdapter.searchForKey(s.toString());
                else
                    inboxRecyclerAdapter.refreshData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //for Hot matches

        hand = new Handler();
        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.HOT_MATCHES, PriveTalkConstants.BROADCAST_HOT_MATCHES_DOWNLOADED, null, new JSONObject());
        hotMatchesObjectList = HotMatchesDatasource.getInstance(getContext()).getHotMatches();
        mRecyclerView1 = (RecyclerView) rootView.findViewById(R.id.hotmatchesUsersProfiles);
        //mGridLayoutManager = new MyGridLayoutManager(getContext(), 2);
        mGridLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mAdapter2 = new GridRecyclerAdapter();
        mRecyclerView1.setLayoutManager(mGridLayoutManager);

        mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                mRecyclerView1.setAdapter(mAdapter2);
            }
        }, mRecyclerView1);

        mRecyclerView1.post(new Runnable() {
            @Override
            public void run() {
           /*     topRecyclerHeight = mRecyclerView.getHeight()*//* +  320*//*;

                mRecyclerView1.setAdapter(mAdapter2);

                CoordinatorLayout.LayoutParams params2 = (CoordinatorLayout.LayoutParams) inboxRecyclerView.getLayoutParams();
                params2.setMargins(0, -topRecyclerHeight, 0, topRecyclerHeight);
                inboxRecyclerView.setLayoutParams(params2);

                //set top margin to appbarlayout
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) inboxAppBar.getLayoutParams();
                params.setMargins(0, topRecyclerHeight, 0, 0);
                inboxAppBar.setLayoutParams(params);

               topRecyclerHeight = mRecyclerView.getHeight();
                mRecyclerView1.setAdapter(mAdapter2);*/
            }
        });

    }


    private class InboxRecyclerAdapter extends RecyclerView.Adapter<InboxRecyclerAdapter.ViewHolder> {

        private int leftAndRightPadding;
        private int topAndBottomPadding;
        private List<ConversationObject> conversationObjects;
        private int colorGreen;
        private int colorGray;

        public InboxRecyclerAdapter() {
            leftAndRightPadding = getActivity().getResources().getDimensionPixelSize(R.dimen.inbox_row_left_and_right_padding);
            topAndBottomPadding = getActivity().getResources().getDimensionPixelSize(R.dimen.inbox_row_top_and_bottom_padding);
            conversationObjects = ConversationsDatasource.getInstance(getContext()).getConversations();
            colorGreen = ContextCompat.getColor(getContext(), R.color.tick_green);
            colorGray = ContextCompat.getColor(getContext(), R.color.tick_grey);
        }


        @Override
        public InboxRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inbox, parent, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(InboxRecyclerAdapter.ViewHolder holder, int position) {

            if (position == 0 || position == 1)
                holder.view.setPadding(0, topRecyclerHeight, 0, 0);
            else
                holder.view.setPadding(0, 0, 0, 0);

            holder.layout1.setTranslationX(0);
            holder.view.setPadding(leftAndRightPadding, topAndBottomPadding + ((position == 0) ? topRecyclerHeight : 0), leftAndRightPadding, topAndBottomPadding);

            holder.verifiedProfile.setColorFilter((conversationObjects.get(position).senderVerificationLevel > 0) ? colorGreen : colorGray, PorterDuff.Mode.SRC_IN);
            holder.royalUser.setColorFilter((conversationObjects.get(position).senderVerificationLevel > 1) ? colorGreen : colorGray, PorterDuff.Mode.SRC_IN);
            holder.socialVerified.setColorFilter((conversationObjects.get(position).senderVerificationLevel > 2) ? colorGreen : colorGray, PorterDuff.Mode.SRC_IN);
            holder.description.setText(conversationObjects.get(position).description.equals("null") ? "" : conversationObjects.get(position).description);
            holder.userInfo.setText(conversationObjects.get(position).partnerName.split(" ")[0] + ", " + conversationObjects.get(position).senderAge);

            Glide.with(getContext()).load(conversationObjects.get(position).partnerAvatarImg).error(R.drawable.dummy_img).into(holder.profilePicture);
            holder.hotMatchIcon.setVisibility(conversationObjects.get(position).isHotMatch ? View.VISIBLE : View.GONE);

            holder.layout1.setTag(R.id.position_tag, position);
            holder.deleteConversation.setTag(R.id.position_tag, position);
            holder.profilePicture.setTag(R.id.position_tag, position);
        }

        public void refreshData() {
            conversationObjects.clear();
            conversationObjects.addAll(ConversationsDatasource.getInstance(getContext()).getConversations());
            notifyDataSetChanged();
        }

        public void searchForKey(String key) {
            conversationObjects.clear();
            conversationObjects.addAll(ConversationsDatasource.getInstance(getContext()).searchConversations(key));
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return conversationObjects.size();
        }

        public void notifyItemDeleted(int position) {
            conversationObjects.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, conversationObjects.size());
        }


        public void handleViewClick(int position) {
            if (ishideConversationLocked())
                return;

            Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
            intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.CHAT_FRAGMENT);
            intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

            Bundle bundle = new Bundle();
            bundle.putSerializable("partnerObject", conversationObjects.get(position));
            intent.putExtra("bundle", bundle);

            LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public View layout1;
            public PriveTalkTextView deleteConversation;
            public CircleImageView profilePicture;
            public PriveTalkTextView userInfo;
            public PriveTalkTextView description;
            public ImageView verifiedProfile, royalUser, socialVerified;
            public View messageIcon;
            public View hotMatchIcon;

            public ViewHolder(View v) {
                super(v);
                view = v;

                layout1 = v.findViewById(R.id.layout1);
                profilePicture = (CircleImageView) v.findViewById(R.id.inboxProfilePicture);
                userInfo = (PriveTalkTextView) v.findViewById(R.id.partnerNameTextView);
                description = (PriveTalkTextView) v.findViewById(R.id.messageDescription);
                verifiedProfile = (ImageView) v.findViewById(R.id.messagesTick1);
                royalUser = (ImageView) v.findViewById(R.id.messagesTick2);
                socialVerified = (ImageView) v.findViewById(R.id.messagesTick3);
                messageIcon = v.findViewById(R.id.messageIcon);
                deleteConversation = (PriveTalkTextView) v.findViewById(R.id.deleteConversation);
                hotMatchIcon = v.findViewById(R.id.hotMatchIcon);
                layout1.setOnTouchListener(getSwipeToDeleteListener());

                profilePicture.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(PriveTalkConstants.KEY_OTHER_USER_ID, conversationObjects.get((int) view.getTag(R.id.position_tag)).partnerID);
                        PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
                    }
                });

                deleteConversation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideConversation(conversationObjects.get((int) v.getTag(R.id.position_tag)).partnerID, (int) v.getTag(R.id.position_tag));
                    }
                });
            }
        }

    }

    private boolean ishideConversationLocked() {
        return deleteConversationLock;
    }

    private void lockHideConversationRequest() {
        deleteConversationLock = true;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void unlockHideConversationRequest() {
        deleteConversationLock = false;
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideConversation(final int partnerID, final int position) {

        if (ishideConversationLocked())
            return;

        lockHideConversationRequest();

        hideConversation = new JsonObjectRequest(Request.Method.POST, Links.getHideConversationUrl(partnerID),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ConversationsDatasource.getInstance(getContext()).deleteConversation(partnerID);
                        MessageDatasource.getInstance(getContext()).deleteMessagesFromSender(partnerID);
                        inboxRecyclerAdapter.notifyItemDeleted(position);
                        unlockHideConversationRequest();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                unlockHideConversationRequest();
                if (error.networkResponse != null) {
                    try {
                        JSONObject obj = new JSONObject(new String(error.networkResponse.data));
                        Toast.makeText(getContext(), obj.optString("error_message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(hideConversation);
    }


    /**
     * Custom TouchListener for the side info
     */
    private View.OnTouchListener getSwipeToDeleteListener() {

        return new View.OnTouchListener() {
            float mDownRawX;
            float mDownX;
            private int mSwipeSlop = -1;
            private boolean mSwiping;
            private boolean isOpen = false;
            private static final int ANIMATION_DURATION = 120;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = MotionEventCompat.getActionMasked(event);
                if (mSwipeSlop < 0) {
                    mSwipeSlop = ViewConfiguration.get(getContext()).
                            getScaledTouchSlop();
                }
//                System.out.println("View touched");

                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
//                        System.out.println("View action down");
                        mDownRawX = event.getRawX(); //get view x
                        mDownX = event.getX();
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        float x = event.getRawX();
                        float deltaX = x - mDownRawX;

//                        System.out.println(" deltaX: " + deltaX);
                        float deltaXAbs = Math.abs(deltaX);
                        if (!mSwiping) {
                            if (deltaXAbs > mSwipeSlop) {
                                mSwiping = true;
                                inboxRecyclerView.setEnabled(false);
                                inboxRecyclerView.setClickable(false);
                                inboxLayoutManager.setCanScroll(false);
                            }
                        }
                        if (mSwiping) {
                            if (deltaXAbs <= inboxRecyclerView.getWidth() / 3) {
                                if (!isOpen && deltaX < 0)
                                    v.animate().translationX(-deltaXAbs).setDuration(0);
                                else if (isOpen && deltaX > 0)
                                    v.animate().translationX(-(inboxRecyclerView.getWidth() / 3) + deltaXAbs).setDuration(0);
                            }
                        }
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        inboxRecyclerView.setEnabled(true);
                        inboxRecyclerView.setClickable(true);
                        inboxLayoutManager.setCanScroll(true);
                        // User let go - figure out whether to animate the view or back into place
                        if (mSwiping) {
                            float x = event.getRawX();
                            float deltaX = x - mDownRawX;
                            float deltaXAbs = Math.abs(deltaX);
                            float slidePercentage = 1f - (deltaXAbs / inboxRecyclerView.getWidth());
                            int duration = (int) (ANIMATION_DURATION * slidePercentage);

                            //if user dragged more tha view widtdh/4 then slide to delete
                            if (!isOpen) {
                                if ((deltaXAbs > inboxRecyclerView.getWidth() / 6) && deltaX < 0) {
                                    isOpen = true;
                                    v.animate().translationX(-(inboxRecyclerView.getWidth() / 3)).setInterpolator(new DecelerateInterpolator()).setDuration(duration);
                                } else {
                                    isOpen = false;
                                    v.animate().translationX(0).setInterpolator(new DecelerateInterpolator()).setDuration(duration);
                                }
                            } else {
                                if ((deltaXAbs < inboxRecyclerView.getWidth() / 6) && deltaX > 0) {
                                    isOpen = true;
                                    v.animate().translationX(-(inboxRecyclerView.getWidth() / 3)).setInterpolator(new DecelerateInterpolator()).setDuration(duration);
                                } else {
                                    isOpen = false;
                                    v.animate().translationX(0).setInterpolator(new DecelerateInterpolator()).setDuration(duration);
                                }
                            }
                            mSwiping = false;
                        } else {
                            inboxRecyclerAdapter.handleViewClick((int) v.getTag(R.id.position_tag));
                        }
                        break;
                    }

                    case MotionEvent.ACTION_CANCEL: {
                        break;
                    }

                    case MotionEvent.ACTION_POINTER_UP: {
                        break;
                    }
                }

                return true;
            }
        };


    }


}