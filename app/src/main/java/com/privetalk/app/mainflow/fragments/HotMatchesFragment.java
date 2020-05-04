package com.privetalk.app.mainflow.fragments;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.HotMatchesDatasource;
import com.privetalk.app.database.objects.HotMatchesObject;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.utilities.BitmapUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.LongPressAndClickListener;
import com.privetalk.app.utilities.MyGridLayoutManager;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class HotMatchesFragment extends FragmentWithTitle {
    private View rootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PromotedUsersAdapter mAdapter;
    private RecyclerView mRecyclerView1;
    private MyGridLayoutManager mGridLayoutManager;
    private GridRecyclerAdapter mAdapter2;
    private View removeMatchBucket;
    private Handler hand;
    private CircularImageView bitmapImageView;
    public static Boolean CAN_USE_TOUCH = true;
    private float initialY = 0;
    private float intitialX = 0;
    private View shadowView;
    private int topRecyclerHeight;
    private String title;
    private View progressBar;
    private Animation bucketAnimation;
    //    private List<PromotedUsersObject> promotedUsersObjectList;
    private List<HotMatchesObject> hotMatchesObjectList;
    private JsonObjectRequest hideMatchRequest;


    private BroadcastReceiver promotedUsersDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.refreshData();
        }
    };

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
    private Handler handler;
    private Runnable runnable;
    private View addmeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }

        hand = new Handler();

        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.HOT_MATCHES, PriveTalkConstants.BROADCAST_HOT_MATCHES_DOWNLOADED, null, new JSONObject());

        mAdapter = new PromotedUsersAdapter(getContext());
        PriveTalkUtilities.fetchPromotedUsers();

//        promotedUsersObjectList = PromotedUsersDatasource.getInstance(getContext()).getPromotedUsers();
        hotMatchesObjectList = HotMatchesDatasource.getInstance(getContext()).getHotMatches();

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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_hot_matches, container, false);

        initViews();

        Glide.with(getContext())
                .load(CurrentUserPhotosDatasource.getInstance(getContext()).checkProfilePic(getContext()) != null?
                        CurrentUserPhotosDatasource.getInstance(getContext()).checkProfilePic(getContext()).square_thumb : "")
                .error(R.drawable.dummy_img).into((ImageView) rootView.findViewById(R.id.addMeImage));

        return rootView;
    }


    @Override
    public void onResume() {

        handler.post(runnable);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(hotMatchesDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_HOT_MATCHES_DOWNLOADED));
        PriveTalkUtilities.badgesRequest(true, PriveTalkConstants.MenuBadges.MATCHES_BADGE);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {

        if (hideMatchRequest != null)
            hideMatchRequest.cancel();

        if (handler != null)
            handler.removeCallbacksAndMessages(null);

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(hotMatchesDownloaded);

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void initViews() {

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        addmeView = rootView.findViewById(R.id.addMePromote);
        addmeView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });

        shadowView = rootView.findViewById(R.id.shadowView);

        removeMatchBucket = rootView.findViewById(R.id.removeMatch);

        bucketAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_in_2);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.addMe);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
       /* mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, mRecyclerView);
*/
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                topRecyclerHeight = mRecyclerView.getHeight();
                mRecyclerView1.setAdapter(mAdapter2);
            }
        });

        mRecyclerView1 = (RecyclerView) rootView.findViewById(R.id.hotmatchesUsersProfiles);
        mGridLayoutManager = new MyGridLayoutManager(getContext(), 2);
        mAdapter2 = new GridRecyclerAdapter();
        mRecyclerView1.setLayoutManager(mGridLayoutManager);
//        mRecyclerView1.setAdapter(mAdapter2);

    }


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
                    .inflate(R.layout.row_hot_matches, parent, false);

            //set new Long press and click listener for my view
            v.setOnTouchListener(newLongPressAndClickListener());

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (position == 0 || position == 1)
                holder.view.setPadding(0, topRecyclerHeight, 0, 0);
            else
                holder.view.setPadding(0, 0, 0, 0);

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


    private void hideMatch(int matchID, final int itemPosition, final View v) {

        progressBar.setVisibility(View.VISIBLE);

        hideMatchRequest = new JsonObjectRequest(Request.Method.POST, Links.getHideMatchUrl(matchID), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);

                if (response.optBoolean("success")) {
                    HotMatchesDatasource.getInstance(getContext()).deleteHotMatch(hotMatchesObjectList.get(itemPosition).matchID);
                    mRecyclerView1.setEnabled(true);
                    mRecyclerView1.setClickable(true);
                    mGridLayoutManager.setCanScroll(true);
                    hotMatchesObjectList.remove(itemPosition);
                    mAdapter2.notifyItemRemoved(itemPosition);
                    mAdapter2.notifyItemRangeChanged(itemPosition, hotMatchesObjectList.size());
                    Toast.makeText(getContext(), getString(R.string.deleted_match), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.hide_match_problemo), Toast.LENGTH_SHORT).show();
                    bitmapImageView.animate().scaleX(1.0f).scaleY(1.0f).translationX(0).translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(250);
                    hand.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bitmapImageView.setVisibility(View.GONE);
                            ((RelativeLayout) rootView).removeView(bitmapImageView);
                            v.setVisibility(View.VISIBLE);

                            mRecyclerView1.setEnabled(true);
                            mRecyclerView1.setClickable(true);
                            mGridLayoutManager.setCanScroll(true);
                        }
                    }, 250);
                }

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out_2);
                removeMatchBucket.setVisibility(View.VISIBLE);
                removeMatchBucket.startAnimation(animation);
                hand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeMatchBucket.setVisibility(View.GONE);
                    }
                }, 300);


                CAN_USE_TOUCH = true;
                initialY = 0;
                intitialX = 0;

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(getContext(), getString(R.string.hide_match_problemo), Toast.LENGTH_SHORT).show();

                bitmapImageView.animate().scaleX(1.0f).scaleY(1.0f).translationX(0).translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(250);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out_2);
                removeMatchBucket.setVisibility(View.VISIBLE);
                removeMatchBucket.setAnimation(animation);

                hand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bitmapImageView.setVisibility(View.GONE);
                        ((RelativeLayout) rootView).removeView(bitmapImageView);
                        v.setVisibility(View.VISIBLE);

                        mRecyclerView1.setEnabled(true);
                        mRecyclerView1.setClickable(true);
                        mGridLayoutManager.setCanScroll(true);
                    }
                }, 250);

                hand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeMatchBucket.setVisibility(View.GONE);
                    }
                }, 300);

                CAN_USE_TOUCH = true;
                initialY = 0;
                intitialX = 0;
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(hideMatchRequest);

    }


    private LongPressAndClickListener newLongPressAndClickListener() {
        return new LongPressAndClickListener(hand) {
            @Override
            public void OnClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("other_user_id", ((int) v.getTag(R.id.user_id_tag)));
                bundle.putString(PriveTalkConstants.OTHER_USER_PROFILE_FROM, "other");
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
                    mGridLayoutManager.setCanScroll(false);


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
                        hideMatch((int) v.getTag(R.id.user_id_tag), (int) v.getTag(R.id.position_tag), v);
//                        bitmapImageView.setVisibility(View.VISIBLE);
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
                                mGridLayoutManager.setCanScroll(true);
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
}
