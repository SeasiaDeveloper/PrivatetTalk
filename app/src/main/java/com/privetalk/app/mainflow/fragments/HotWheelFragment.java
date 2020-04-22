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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.objects.HotWheelUser;
import com.privetalk.app.database.objects.ProfilePhoto;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.utilities.DrawerUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FlameImageView;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.RecyclerViewPager;
import com.privetalk.app.utilities.VolleySingleton;
import com.privetalk.app.utilities.dialogs.InstructionsDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class HotWheelFragment extends FragmentWithTitle {

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;

    private JsonObjectRequest sendVoteRequest;

    private View progressBar;

    private String title;
    private View rootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PromotedUsersAdapter mAdapter;
    private LayoutInflater inflater;
    private FrameLayout viewpagerContainer;
    private ViewPager viewPager1;
    private ViewPager viewPager2;
    private float swipeAlphaVariable;
    private JsonArrayRequest getHotWheelRequest;
    private AsyncTask<Void, Void, ArrayList<HotWheelUser>> parseHotWheel;
    private ArrayList<HotWheelUser> hotWheelUsers;
    private View flameVoteImageView, snowVoteImageView;
    private PriveTalkTextView nameTextView;
    private PriveTalkTextView ageTextView;
    private FlameImageView flameImageview;
    private ImageView pictureVerification;
    private ImageView socialVerification;
    private ImageView royalUserVerification;
    private BroadcastReceiver promotedUsersDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.refreshData();
        }
    };
    private View undoButton;
    private int lastVotedId;
    private JsonObjectRequest undoVoteRequest;
    private int listIndex = 0;
    private Handler handler;
    private Runnable runnable;
    private View addmeView;
    private View emptyListImage;
    private int imageCenterX;
    private int actionBarAndStatusBarSize;
    private int blueCircleWidth;
    private int imageRadius;
    private int pagerMargin;
    private PriveTalkTextView sexyTextView;
    private boolean alreadyLoaded = true;
    private boolean hasProfilePicture;
    private boolean lockDialog;
//    private LinearLayout.LayoutParams photosRecyclerViewParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() == null)
            hasProfilePicture = false;
        else {
            String mainPhoto = CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto().photo;
            hasProfilePicture = (mainPhoto != null && !mainPhoto.isEmpty());
        }

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }


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

        mAdapter = new PromotedUsersAdapter(getContext());
        PriveTalkUtilities.fetchPromotedUsers();

        swipeAlphaVariable = (float) getContext().getResources().getDimensionPixelSize(R.dimen.swipeHotWheelAlpha);

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

        if (hotWheelUsers == null)
            hotWheelUsers = new ArrayList<>();

        getHotWheel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        this.inflater = inflater;

        rootView = inflater.inflate(R.layout.fragment_hotwheel, container, false);


        initViews();


        //show instructions if not viewed again
        if (!getContext().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PriveTalkConstants.KEY_HOT_WHEEL_INSTRUCTIONS_READ, false))
            new InstructionsDialog(getContext(), (RelativeLayout) rootView, InstructionsDialog.HOT_WHEEL_INSTRUCTIONS);

        loadDataToViews();

        Glide.with(getContext())
                .load(CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() != null ?
                        CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto().square_thumb : "")
                .error(R.drawable.dummy_img).into((ImageView) rootView.findViewById(R.id.addMeImage));


        return rootView;
    }


    private void showAccessDeniedDialog() {

        if (lockDialog)
            return;

        lockDialog = true;

        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.to_use_hot_wheel_blah_blah)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        lockDialog = false;

                        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_DRAWER_FRAGMENT);
                        intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.PROFILE.ordinal());
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                lockDialog = false;
            }
        }).create().show();
    }


    public class ViewPagerAdapter extends PagerAdapter {

        public HotWheelUser hotWheelUser;
        private int type;

        public ViewPagerAdapter(HotWheelUser hotWheelUser, int type) {
            this.type = type;
            this.hotWheelUser = hotWheelUser;
        }

        @Override
        public int getCount() {
            return hotWheelUser.profilePhotos.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final RecyclerViewPager recyclerViewPager = (RecyclerViewPager) inflater.inflate(R.layout.layout_test, container, false);
            recyclerViewPager.setAdapter(new InnerAdapter1(hotWheelUser.profilePhotos.get(position), hotWheelUser.userID));

            recyclerViewPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {

                @Override
                public boolean canScrollVertically() {
                    return hasProfilePicture;
                }
            });

            recyclerViewPager.scrollToPosition(1);
            recyclerViewPager.addOnScrollListener(new RecyclerView.OnScrollListener() {

                private int totalScrollY;


                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalScrollY += dy;
                    ViewHolder viewHolder = (ViewHolder) recyclerViewPager.findViewHolderForAdapterPosition(1);

                    if (viewHolder == null)
                        return;

                    if (totalScrollY < 0) {
                        viewHolder.alphaSnowView.setAlpha(Math.abs((float) totalScrollY) / swipeAlphaVariable);
                        viewHolder.alphaFlameView.setAlpha(0);
                        viewHolder.profileImageview.setVisibility(View.INVISIBLE);
                        viewHolder.circleImageView.setVisibility(View.VISIBLE);
                    } else if (totalScrollY > 0) {
                        viewHolder.alphaFlameView.setAlpha((float) totalScrollY / swipeAlphaVariable);
                        viewHolder.alphaSnowView.setAlpha(0);
                        viewHolder.profileImageview.setVisibility(View.INVISIBLE);
                        viewHolder.circleImageView.setVisibility(View.VISIBLE);
                    } else if (totalScrollY == 0) {
                        viewHolder.profileImageview.setVisibility(View.VISIBLE);
                        viewHolder.circleImageView.setVisibility(View.INVISIBLE);
                    }
                }
            });


            recyclerViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {

                @Override
                public void OnPageChanged(int previousPosition, int currentPosition) {

                    if (currentPosition == 2 || currentPosition == 0) {

                        if (currentPosition == 2) {
                            sendVote(true, hotWheelUser.userID);
                        } else {
                            sendVote(false, hotWheelUser.userID);
                        }
                        switchViewPager(type);
                    }
                }
            });

            recyclerViewPager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAccessDeniedDialog();
                }
            });

            container.addView(recyclerViewPager);

            return recyclerViewPager;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    private void initViews() {

        progressBar = rootView.findViewById(R.id.progressBar);
        if (!alreadyLoaded) {
            alreadyLoaded = true;
            progressBar.setVisibility(View.VISIBLE);
        }
        addmeView = rootView.findViewById(R.id.addMePromote);
        addmeView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });

        flameImageview = (FlameImageView) rootView.findViewById(R.id.flameImageView);
        nameTextView = (PriveTalkTextView) rootView.findViewById(R.id.profileName);
        ageTextView = (PriveTalkTextView) rootView.findViewById(R.id.profileAge);
        pictureVerification = (ImageView) rootView.findViewById(R.id.profilePictureVerificationTick);
        sexyTextView = (PriveTalkTextView) rootView.findViewById(R.id.hot_wheel_sexy_text);
        socialVerification = (ImageView) rootView.findViewById(R.id.socialMediaVerificationTick);
        royalUserVerification = (ImageView) rootView.findViewById(R.id.royalUserVerificationTick);
        undoButton = rootView.findViewById(R.id.hotWheelUndoButton);
        undoButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                undoLastVoteAction();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.hotWheelAddme);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, mRecyclerView);


        flameVoteImageView = rootView.findViewById(R.id.flameVoteImageView);
        flameVoteImageView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (hasProfilePicture) {
                    sendVote(true, getShowHotWheelUser());
                    switchViewPager(getViewpagerType());
                } else
                    showAccessDeniedDialog();
            }
        });
        snowVoteImageView = rootView.findViewById(R.id.snowImage);
        snowVoteImageView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (hasProfilePicture) {
                    sendVote(false, getShowHotWheelUser());
                    switchViewPager(getViewpagerType());
                } else
                    showAccessDeniedDialog();
            }
        });
        viewpagerContainer = (FrameLayout) rootView.findViewById(R.id.ViewPagersContainer);

        viewPager1 = (ViewPager) rootView.findViewById(R.id.hotWheelViewpager1);
        viewPager1.setOffscreenPageLimit(6);
        viewPager2 = (ViewPager) rootView.findViewById(R.id.hotWheelViewpager2);
        viewPager2.setOffscreenPageLimit(6);

        emptyListImage = rootView.findViewById(R.id.emptyListImage);


        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                photosRecyclerView.smoothScrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager1.addOnPageChangeListener(pageListener);
        viewPager2.addOnPageChangeListener(pageListener);


    }


//    private void setPhotosRecyclerViewWidth() {
//        int count = hotWheelUsers != null && !hotWheelUsers.isEmpty() && hotWheelUsers.size() > listIndex ? hotWheelUsers.get(listIndex).profilePhotos.size() : 0;
//
//        int smallPhotosWidth = getResources().getDimensionPixelSize(R.dimen.small_photos_width);
//
//        //set recyclerview width depending photos count
//        if (count == 0)
//            photosRecyclerViewParams.width = 0;
//        else if (count == 1)
//            photosRecyclerViewParams.width = smallPhotosWidth;
//        else if (count == 2)
//            photosRecyclerViewParams.width = 2 * smallPhotosWidth;
//        else if (count == 3)
//            photosRecyclerViewParams.width = 3 * smallPhotosWidth;
//        else
//            photosRecyclerViewParams.width = 4 * smallPhotosWidth;
//
//
////        photosRecyclerView.setLayoutParams(photosRecyclerViewParams);
//
//    }


    private void undoLastVoteAction() {

        undoButton.setEnabled(false);
        undoButton.setClickable(false);

        if (listIndex == 0)
            return;
        listIndex--;


        loadDataToViews();


        sexyTextView.setText("");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("partner_id", hotWheelUsers.get(listIndex).userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        undoVoteRequest = new JsonObjectRequest(Request.Method.DELETE, String.format(Links.UNDO_MATCH, lastVotedId), jsonObject, new Response.Listener<JSONObject>() {
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(undoVoteRequest);

    }


    private void sendVote(boolean is_hot, final int partner_id) {

        undoButton.setEnabled(true);
        undoButton.setClickable(true);


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
//                System.out.println(response.toString());
//                System.out.println("vote succeeded");
                lastVotedId = partner_id;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                System.out.println("error vote" + error.networkResponse != null ? new String(error.networkResponse.data) : "null");
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

    @Override
    public void onResume() {

        handler.post(runnable);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));

        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {

        if (handler != null)
            handler.removeCallbacksAndMessages(null);

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
        if (getHotWheelRequest != null)
            getHotWheelRequest.cancel();
        if (parseHotWheel != null)
            parseHotWheel.cancel(true);
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

    public class InnerAdapter1 extends RecyclerViewPager.Adapter<ViewHolder> {
        ProfilePhoto object;
        private int userId;

        public InnerAdapter1(ProfilePhoto object, int userId) {
            this.object = object;
            this.userId = userId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.circleimageview_my_profile, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (position == 1) {
                Glide.with(getContext()).load(object.medium_square_thumb).error(R.drawable.dummy_img).dontTransform().into(holder.profileImageview);
                Glide.with(getContext()).load(object.medium_square_thumb).error(R.drawable.dummy_img).dontTransform().into(holder.circleImageView);

                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    float mDownRawX;
                    float mDownRawY;
                    int distanceX;
                    int distanceY;
                    int distanceFromCenter;
                    private int imageCenterY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (!hasProfilePicture) {
                            showAccessDeniedDialog();
                            return true;
                        }

                        final int action = MotionEventCompat.getActionMasked(event);
                        switch (action) {
                            case MotionEvent.ACTION_DOWN: {
                                mDownRawX = event.getRawX();
                                mDownRawY = event.getRawY() - actionBarAndStatusBarSize;
                                imageCenterY = pagerMargin + imageRadius + mRecyclerView.getHeight();
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
                                    bundle.putSerializable("other_user_photos", hotWheelUsers.get(listIndex).profilePhotos);
                                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_FULL_SCREEN_PHOTOS, bundle);
                                }
                                break;
                            }
                        }
                        return true;
                    }
                });
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("other_user_photos", hotWheelUsers.get(listIndex).profilePhotos);
//                        PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_FULL_SCREEN_PHOTOS, bundle);
//                    }
//                });
            } else {
                holder.itemView.setOnTouchListener(null);
                holder.profileImageview.setImageDrawable(null);
                holder.circleImageView.setImageDrawable(null);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final View alphaFlameView;
        private final View alphaSnowView;
        private ImageView profileImageview;
        private CircleImageView circleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageview = (ImageView) itemView.findViewById(R.id.profileImageview);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profileCircleImageview);
            alphaFlameView = itemView.findViewById(R.id.alphaFlame);
            alphaSnowView = itemView.findViewById(R.id.alphaSnow);
        }
    }

    private class SmallPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SmallPhotosViewHolder(inflater.inflate(R.layout.row_small_circle_imageview, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            SmallPhotosViewHolder holder1 = (SmallPhotosViewHolder) holder;

            Glide.with(PriveTalkApplication.getInstance()).load(hotWheelUsers.get(listIndex).profilePhotos.get(position).square_thumb).error(R.drawable.dummy_img).into(holder1.circleImageView);

        }

        @Override
        public int getItemCount() {
            return hotWheelUsers != null && !hotWheelUsers.isEmpty() && hotWheelUsers.size() > listIndex ? hotWheelUsers.get(listIndex).profilePhotos.size() : 0;
        }

        private class SmallPhotosViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView circleImageView;

            public SmallPhotosViewHolder(View itemView) {
                super(itemView);

                circleImageView = (CircleImageView) itemView.findViewById(R.id.smallProfileCircleImageview);
            }
        }
    }

    private void getHotWheel() {

        alreadyLoaded = false;

        getHotWheelRequest = new JsonArrayRequest(JsonArrayRequest.Method.GET, Links.HOT_WHEEL, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                parseHotWheel = new AsyncTask<Void, Void, ArrayList<HotWheelUser>>() {
                    @Override
                    protected ArrayList<HotWheelUser> doInBackground(Void... params) {

                        ArrayList<HotWheelUser> hotWheelUserArrayList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            HotWheelUser hotWheelUser = new HotWheelUser(response.optJSONObject(i));

                            if (hotWheelUser.profilePhotos.size() > 0)
                                hotWheelUserArrayList.add(hotWheelUser);

                        }

                        return hotWheelUserArrayList;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<HotWheelUser> hotWheelUserArrayList) {

                        hotWheelUsers.clear();
                        hotWheelUsers.addAll(hotWheelUserArrayList);
                        listIndex = 0;

                        if (hotWheelUsers.size() == 0)
                            sexyTextView.setText(R.string.no_more_users_to_show);

                        if (viewPager1 != null) {
                            if (hotWheelUsers.size() <= 0) {
                                disableHotWheel();
//                                sexyTextView.setText(R.string.no_more_users_to_show);
                            } else {
                                sexyTextView.setText("");
                            }


                            progressBar.setVisibility(View.GONE);
                            loadDataToViews();
                        }

                    }
                }.execute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                if (hotWheelUsers == null || hotWheelUsers.size() == 0)
                    sexyTextView.setText(R.string.no_more_users_to_show);
            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getHotWheelRequest);
    }

    private void loadDataToViews() {

        if (hotWheelUsers.size() > listIndex) {
            viewPager1.setAdapter(new ViewPagerAdapter(hotWheelUsers.get(listIndex), 0));
            viewPager1.setVisibility(View.VISIBLE);
            viewPager1.bringToFront();
            fillUserInformation((ViewPagerAdapter) viewPager1.getAdapter());
//            setPhotosRecyclerViewWidth();
//            smallPhotosAdapter.notifyDataSetChanged();
        }
        if (hotWheelUsers.size() > listIndex + 1) {
            viewPager2.setAdapter(new ViewPagerAdapter(hotWheelUsers.get(listIndex + 1), 1));
            viewPager2.setVisibility(View.VISIBLE);
        }

    }

    public int getShowHotWheelUser() {

        if (viewPager1.getAdapter() == null || viewPager2.getAdapter() == null)
            return -1;

        if (viewPager1.getVisibility() == View.GONE && viewPager2.getVisibility() == View.GONE)
            return -1;

        if (viewpagerContainer.indexOfChild(viewPager1) > viewpagerContainer.indexOfChild(viewPager2))
            return ((ViewPagerAdapter) viewPager1.getAdapter()).hotWheelUser.userID;

        else
            return ((ViewPagerAdapter) viewPager2.getAdapter()).hotWheelUser.userID;
    }

    private int getViewpagerType() {

        return viewpagerContainer.indexOfChild(viewPager1) > viewpagerContainer.indexOfChild(viewPager2) ? TYPE_ONE : TYPE_TWO;

    }

    private void switchViewPager(final int type) {

        if (hotWheelUsers.size() <= listIndex)
            return;

        listIndex++;

        if (type == TYPE_ONE) {
            viewPager2.bringToFront();
//            setPhotosRecyclerViewWidth();
//            smallPhotosAdapter.notifyDataSetChanged();
        } else {
            viewPager1.bringToFront();
//            setPhotosRecyclerViewWidth();
//            smallPhotosAdapter.notifyDataSetChanged();
        }
        rootView.post(new Runnable() {
            @Override
            public void run() {
                if (type == TYPE_ONE) {

                    if (viewPager2.getAdapter() != null)
                        fillUserInformation((ViewPagerAdapter) viewPager2.getAdapter());
                    else
                        disableHotWheel();
                    if (hotWheelUsers.size() > listIndex + 1) {

                        viewPager1.removeAllViews();
                        viewPager1.setAdapter(new ViewPagerAdapter(hotWheelUsers.get(listIndex + 1), 0));

                    } else {

                        if (viewPager2.getVisibility() == View.GONE) {
//                            photosRecyclerView.setVisibility(View.GONE);
                            disableHotWheel();
                        }

                        viewPager1.setVisibility(View.GONE);
                    }

                } else {

                    if (viewPager1.getAdapter() != null)
                        fillUserInformation((ViewPagerAdapter) viewPager1.getAdapter());
                    else
                        disableHotWheel();

                    if (hotWheelUsers.size() > listIndex + 1) {
                        viewPager2.removeAllViews();
                        viewPager2.setAdapter(new ViewPagerAdapter(hotWheelUsers.get(listIndex + 1), 1));

                    } else {

                        if (viewPager1.getVisibility() == View.GONE) {
//                            photosRecyclerView.setVisibility(View.GONE);
                            disableHotWheel();
                        }

//                        setPhotosRecyclerViewWidth();
//                        smallPhotosAdapter.notifyDataSetChanged();
                        viewPager2.setVisibility(View.GONE);
                    }
                }
            }

        });
    }

    private void fillUserInformation(ViewPagerAdapter adapter) {

        if (adapter == null)
            return;

        nameTextView.setText(adapter.hotWheelUser.userName.split(" ")[0]);
        ageTextView.setText(String.valueOf(adapter.hotWheelUser.userAge));
        flameImageview.changeHotness(0);
        pictureVerification.setColorFilter(adapter.hotWheelUser.photosVerified ? ContextCompat.getColor(getContext(), R.color.tick_green) : Color.WHITE, PorterDuff.Mode.SRC_IN);
        socialVerification.setColorFilter(adapter.hotWheelUser.socialVerified ? ContextCompat.getColor(getContext(), R.color.tick_green) : Color.WHITE, PorterDuff.Mode.SRC_IN);
        royalUserVerification.setColorFilter(adapter.hotWheelUser.royalUser ? ContextCompat.getColor(getContext(), R.color.tick_green) : Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

    private void disableHotWheel() {
        nameTextView.setText("");
        ageTextView.setText("");
        flameImageview.changeHotness(0);
        pictureVerification.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        socialVerification.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        royalUserVerification.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        sexyTextView.setText(getString(R.string.no_more_users_to_show));
    }

    public void isListEmpty(boolean isListEmpty) {
        if (isListEmpty)
            emptyListImage.setVisibility(View.VISIBLE);
        else
            emptyListImage.setVisibility(View.GONE);
    }
}
