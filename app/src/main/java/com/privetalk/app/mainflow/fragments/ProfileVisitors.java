package com.privetalk.app.mainflow.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.ProfileVisitorsDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.ProfileVisitorsObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class ProfileVisitors extends FragmentWithTitle {

    private View rootView;
    private RecyclerView promotedUsersRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PromotedUsersAdapter mAdapter;
    private RecyclerView profileVisitorsRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private GridRecyclerAdapter mAdapter2;
    private int topRecyclerHeight;
    private String title;
    private List<ProfileVisitorsObject> todayVisitorsList;
    private List<ProfileVisitorsObject> weekVisitorsList;
    private View progressBar;
    private BroadcastReceiver profileVisitorsDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            if (todayVisitorsList == null)
                todayVisitorsList = new ArrayList<>();
            if (weekVisitorsList == null)
                weekVisitorsList = new ArrayList<>();

            todayVisitorsList.clear();
            weekVisitorsList.clear();
            todayVisitorsList.addAll(ProfileVisitorsDatasource.getInstance(getContext()).getTodayVisitors());
            weekVisitorsList.addAll(ProfileVisitorsDatasource.getInstance(getContext()).getWeekVisitors());
            mAdapter2.notifyDataSetChanged();

        }
    };

    private BroadcastReceiver promotedUsersDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.refreshData();
        }
    };
    private Handler handler;
    private Runnable runnable;
    private View addmeView;
    private CurrentUser currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }


        todayVisitorsList = ProfileVisitorsDatasource.getInstance(getContext()).getTodayVisitors();
        weekVisitorsList = ProfileVisitorsDatasource.getInstance(getContext()).getWeekVisitors();
//        ProfileVisitorsDatasource.getInstance(getContext()).deleteProfileVisitors();

        mAdapter = new PromotedUsersAdapter(getContext());

        //download/refresh promoted users
        PriveTalkUtilities.fetchPromotedUsers();


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (PriveTalkUtilities.checkIfPromotedUsersIsScrollable(promotedUsersRecyclerView, mAdapter)) {
                    promotedUsersRecyclerView.scrollBy(1, 0);
                }
                handler.postDelayed(this, 15);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        rootView = inflater.inflate(R.layout.fragment_profile_visitors, container, false);

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();

        initViews();

        Glide.with(getContext())
                .load(CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto() != null ?
                        CurrentUserPhotosDatasource.getInstance(getContext()).getProfilePhoto().square_thumb : "")
                .error(R.drawable.dummy_img).into((ImageView) rootView.findViewById(R.id.addMeImage));

        return rootView;
    }

    @Override
    public void onResume() {
        handler.post(runnable);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(profileVisitorsDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROFILE_VISITORS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
        PriveTalkUtilities.badgesRequest(true, PriveTalkConstants.MenuBadges.VISITORS_BADGE);
        //download profile visitors
        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.GET_PROFILE_VISITORS, PriveTalkConstants.BROADCAST_PROFILE_VISITORS_DOWNLOADED, null, new JSONObject());
        progressBar.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(profileVisitorsDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
//        if (getProfileVisitorsObjectRequest != null)
//            getProfileVisitorsObjectRequest.cancel();

        if (handler != null)
            handler.removeCallbacksAndMessages(null);

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
        addmeView = rootView.findViewById(R.id.addMePromote);
        addmeView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });

        promotedUsersRecyclerView = (RecyclerView) rootView.findViewById(R.id.addMe);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(false);
        promotedUsersRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                promotedUsersRecyclerView.setAdapter(mAdapter);
            }
        }, promotedUsersRecyclerView);

        promotedUsersRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                topRecyclerHeight = promotedUsersRecyclerView.getHeight();
                profileVisitorsRecyclerView.setAdapter(mAdapter2);
            }
        });

        profileVisitorsRecyclerView = (RecyclerView) rootView.findViewById(R.id.communityUsersProfiles);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter2 = new GridRecyclerAdapter();
        profileVisitorsRecyclerView.setLayoutManager(mGridLayoutManager);

        //change the span so i can have headers
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter2.isHeader(position) ? mGridLayoutManager.getSpanCount() : 1;
            }
        });

    }


    private class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder> {

        private Drawable onlineDrawable;
        private Drawable offlineDrawable;
        private int colorGreen;
        private int colorWhite;
        private List<ProfileVisitorsObject> tempList;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public CircleImageView userProfilePicture;
            public PriveTalkTextView userName;
            public ImageView pictureVerifiedTick, superUserTick, socialMediaTick;
            public PriveTalkTextView userAge;
            public ImageView userStatus;
            public View header;
            public View profile;
            public PriveTalkTextView headerText;
            public ImageView royalUserIcon;
            public View frame;
            public View img;
            public View shadowView;

            public ViewHolder(View v) {
                super(v);
                view = v;
                userProfilePicture = (CircleImageView) v.findViewById(R.id.visitorsProfilePicture);
                userName = (PriveTalkTextView) v.findViewById(R.id.visitorsUserName);
                pictureVerifiedTick = (ImageView) v.findViewById(R.id.visitorsUserTick1);
                superUserTick = (ImageView) v.findViewById(R.id.visitorsUserTick3);
                socialMediaTick = (ImageView) v.findViewById(R.id.visitorsUserTick2);
                userAge = (PriveTalkTextView) v.findViewById(R.id.visitorsUserAge);
                userStatus = (ImageView) v.findViewById(R.id.visitorsUserStatus);
                header = v.findViewById(R.id.visitorsHeader);
                headerText = (PriveTalkTextView) v.findViewById(R.id.visitorsHeaderText);
                profile = v.findViewById(R.id.visitorsProfile);
                royalUserIcon = (ImageView) v.findViewById(R.id.visitorsCrow);
                frame = v.findViewById(R.id.frame);
                img = v.findViewById(R.id.img);
                shadowView = v.findViewById(R.id.shadowView);
            }
        }

        public boolean isHeader(int position) {
            if (position == 0 || position == (todayVisitorsList.size() + 1))
                return true;
            else
                return false;
        }

        public GridRecyclerAdapter() {
            onlineDrawable = ContextCompat.getDrawable(getContext(), R.drawable.user_online);
            offlineDrawable = ContextCompat.getDrawable(getContext(), R.drawable.user_offline);
            colorGreen = ContextCompat.getColor(getContext(), R.color.tick_green);
            colorWhite = Color.WHITE;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public GridRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_profile_visitors, parent, false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((int) v.getTag(R.id.user_id_tag) != -1) {

                        if (currentUser.royal_user) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(PriveTalkConstants.KEY_OTHER_USER_ID, ((int) v.getTag(R.id.user_id_tag)));
                            PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
                        } else {
                            showRoyalDialog();
                        }
                    }
                }
            });

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (position == 0) {
                holder.view.setPadding(0, topRecyclerHeight, 0, 0);
            } else
                holder.view.setPadding(0, 0, 0, 0);


            if (isHeader(position)) {
                holder.view.setTag(R.id.user_id_tag, -1);
                holder.profile.setVisibility(View.GONE);
                holder.header.setVisibility(View.VISIBLE);
                if (position == 0)
                    holder.headerText.setText(R.string.todays_visitors);
                else
                    holder.headerText.setText(R.string.week_visitors);
            } else {
                holder.userName.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.pictureVerifiedTick.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.pictureVerifiedTick.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.superUserTick.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.socialMediaTick.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.userAge.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.userStatus.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.royalUserIcon.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.frame.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.img.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                holder.shadowView.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);

                tempList = (todayVisitorsList.size() >= position - 1) ? todayVisitorsList : weekVisitorsList;
                position = (todayVisitorsList.size() >= position - 1) ? (position - 1) : (position - todayVisitorsList.size() - 2);

                holder.view.setTag(R.id.user_id_tag, tempList.get(position).userID);
                holder.profile.setVisibility(View.VISIBLE);
                holder.header.setVisibility(View.GONE);

                if (currentUser.royal_user) {

                    holder.userName.setText(tempList.get(position).name.split(" ")[0]);
                    holder.userAge.setText(String.valueOf(tempList.get(position).age));
                    holder.userStatus.setImageDrawable(tempList.get(position).isOnline ? onlineDrawable : offlineDrawable);

//                    if (tempList.get(position).squareThumb != null && !tempList.get(position).squareThumb.isEmpty())
                    Glide.with(getContext()).load(tempList.get(position).squareThumb).error(R.drawable.dummy_img).into(holder.userProfilePicture);

                    //status online/ofline
                    holder.userStatus.setImageDrawable(tempList.get(position).isOnline ?
                            onlineDrawable : offlineDrawable);
                    //photos verified
                    holder.pictureVerifiedTick.setColorFilter(tempList.get(position).photosVerified ? colorGreen : colorWhite);
                    //royal user
                    holder.superUserTick.setColorFilter(tempList.get(position).royalUser ? colorGreen : colorWhite);
                    //social media verified
                    holder.socialMediaTick.setColorFilter(tempList.get(position).socialVerified ? colorGreen : colorWhite);

                    if (tempList.get(position).royalUser)
                        holder.royalUserIcon.setImageResource(R.drawable.is_royal_user);
                    else
                        holder.royalUserIcon.setImageDrawable(null);
                } else {
                    Glide.with(getContext()).load(tempList.get(position)
                            .squareThumb).bitmapTransform(new BlurTransformation(getContext(), PriveTalkConstants.IMAGE_BLURRINESS)).error(R.drawable.dummy_img).into(holder.userProfilePicture);
                }
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return todayVisitorsList.size() + weekVisitorsList.size() + 2;
        }
    }


    private void showRoyalDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.only_available)).
                setMessage(R.string.view_our_plans).
                setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                        intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ROYAL_USER_PLANS_FRAGMENT_ID);
                        intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
                    }
                }).setNegativeButton(getString(R.string.no_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }

}
