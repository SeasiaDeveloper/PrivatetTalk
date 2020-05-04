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
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.FavoritesDatasourse;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.FavoritesObject;
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
public class FavoritesFragment extends FragmentWithTitle {

    private View rootView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PromotedUsersAdapter mAdapter;
    private RecyclerView mRecyclerView1;
    private GridLayoutManager mGridLayoutManager;
    private GridRecyclerAdapter mAdapter2;
    private int topRecyclerHeight;
    private String title;
    private List<FavoritesObject> favoritesObjects;
    private List<FavoritesObject> favoritedByObjects;
    private View progressBar;
    private boolean hideProgressBar = false;
//    private List<PromotedUsersObject> promotedUsersObjectList;

    private BroadcastReceiver favoritesDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            hideProgressBar = true;
            if (favoritesObjects == null)
                favoritesObjects = new ArrayList<>();
            favoritesObjects.clear();
            favoritesObjects.addAll(FavoritesDatasourse.getInstance(getContext()).getFavorites());
            mAdapter2.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver favoritedByDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            hideProgressBar = true;
            if (favoritedByObjects == null)
                favoritedByObjects = new ArrayList<>();
            favoritedByObjects.clear();
            favoritedByObjects.addAll(FavoritesDatasourse.getInstance(getContext()).getFavoritesBy());
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


        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.GET_FAVORITES, PriveTalkConstants.BROADCAST_FAVORITES_DOWNLOADED, null, new JSONObject());
        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.GET_FAVORITED_BY, PriveTalkConstants.BROADCAST_FAVORITED_BY_DOWNLOADED, null, new JSONObject());

        mAdapter = new PromotedUsersAdapter(getContext());
        PriveTalkUtilities.fetchPromotedUsers();

        favoritesObjects = FavoritesDatasourse.getInstance(getContext()).getFavorites();
        favoritedByObjects = FavoritesDatasourse.getInstance(getContext()).getFavoritesBy();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        initViews();

        return rootView;
    }


    private void initViews() {
        if (!hideProgressBar)
            progressBar = rootView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        addmeView = rootView.findViewById(R.id.addMePromote);
        addmeView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.addMe);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(false);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, mRecyclerView);

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                topRecyclerHeight = mRecyclerView.getHeight();
                mRecyclerView1.setAdapter(mAdapter2);
            }
        });

        mRecyclerView1 = (RecyclerView) rootView.findViewById(R.id.communityUsersProfiles);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter2 = new GridRecyclerAdapter();
        mRecyclerView1.setLayoutManager(mGridLayoutManager);

        //change the span so i can have headers
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter2.isHeader(position) ? mGridLayoutManager.getSpanCount() : 1;
            }
        });

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

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
        currentUser.currentUserDetails = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails();


        Glide.with(getContext())
                .load(CurrentUserPhotosDatasource.getInstance(getContext()).checkProfilePic(getContext())!=null?
                        CurrentUserPhotosDatasource.getInstance(getContext()).checkProfilePic(getContext()).square_thumb : "")
                .error(R.drawable.dummy_img).into((ImageView) rootView.findViewById(R.id.addMeImage));

    }

    @Override
    public void onResume() {

        handler.post(runnable);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(favoritesDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_FAVORITES_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(favoritedByDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_FAVORITED_BY_DOWNLOADED));
        PriveTalkUtilities.badgesRequest(true, PriveTalkConstants.MenuBadges.FAVORITES_BADGE);
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

        hideProgressBar = true;
        progressBar.setVisibility(View.GONE);

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(favoritesDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(favoritedByDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
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


    private class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder> {

        private Drawable onlineDrawable;
        private Drawable offlineDrawable;
        private int colorGreen;
        private int colorWhite;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public CircleImageView userProfilePicture;
            public PriveTalkTextView userName;
            public ImageView verifiedPhotos, royalUser, socialVerified;
            public PriveTalkTextView userAge;
            public ImageView userStatus;
            public View header;
            public View profile;
            public PriveTalkTextView headerText;
            public ImageView favoritesStar;
            public View ageBack;
            public View shadowView;
            public View whiteFrame;

            public ViewHolder(View v) {
                super(v);
                view = v;
                userProfilePicture = (CircleImageView) v.findViewById(R.id.favoritesProfilePicture);
                userName = (PriveTalkTextView) v.findViewById(R.id.favoritesUserName);
                verifiedPhotos = (ImageView) v.findViewById(R.id.favoritesUserTick1);
                royalUser = (ImageView) v.findViewById(R.id.favoritesUserTick3);
                socialVerified = (ImageView) v.findViewById(R.id.favoritesUserTick2);
                userAge = (PriveTalkTextView) v.findViewById(R.id.favoritesUserAge);
                userStatus = (ImageView) v.findViewById(R.id.favoritesUserStatus);
                header = v.findViewById(R.id.favoritesHeader);
                headerText = (PriveTalkTextView) v.findViewById(R.id.favoritesHeaderText);
                profile = v.findViewById(R.id.favoritesProfile);
                favoritesStar = (ImageView) v.findViewById(R.id.favoritesStar);
                ageBack = v.findViewById(R.id.ageBack);
                shadowView = v.findViewById(R.id.shadowView);
                whiteFrame = v.findViewById(R.id.whiteFrame);
            }
        }

        public boolean isHeader(int position) {
            if (position == 0 || position == (favoritesObjects.size() + 1))
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
                    .inflate(R.layout.row_favorites, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {


            if (position == 0)
                holder.view.setPadding(0, topRecyclerHeight, 0, 0);
            else
                holder.view.setPadding(0, 0, 0, 0);


            if (isHeader(position)) {
                holder.view.setTag(R.id.user_id_tag, -1);
                holder.profile.setVisibility(View.GONE);
                holder.header.setVisibility(View.VISIBLE);
                if (position == 0)
                    holder.headerText.setText(R.string.your_favorites);
                else
                    holder.headerText.setText(R.string.who_favoried_you);
            } else {
                holder.profile.setVisibility(View.VISIBLE);
                holder.header.setVisibility(View.GONE);

                if (favoritesObjects.size() >= position - 1) {
                    holder.view.setTag(R.id.user_id_tag, favoritesObjects.get(position - 1).userID);
                    holder.view.setClickable(true);
                    holder.userName.setVisibility(View.VISIBLE);
                    holder.userAge.setVisibility(View.VISIBLE);
                    holder.userStatus.setVisibility(View.VISIBLE);
                    holder.favoritesStar.setVisibility(View.VISIBLE);
                    holder.verifiedPhotos.setVisibility(View.VISIBLE);
                    holder.royalUser.setVisibility(View.VISIBLE);
                    holder.socialVerified.setVisibility(View.VISIBLE);
                    holder.ageBack.setVisibility(View.VISIBLE);
                    holder.shadowView.setVisibility(View.VISIBLE);
                    holder.whiteFrame.setVisibility(View.VISIBLE);
                    holder.view.setTag(R.id.user_id_tag, favoritesObjects.get(position - 1).userID);
                    holder.userName.setText(favoritesObjects.get(position - 1).userName.split(" ")[0]);
                    holder.userAge.setText(String.valueOf(favoritesObjects.get(position - 1).userAge));
                    holder.userStatus.setImageDrawable(favoritesObjects.get(position - 1).isOnline ? onlineDrawable : offlineDrawable);
                    //photos verified
                    holder.verifiedPhotos.setColorFilter(favoritesObjects.get(position - 1).photosVerified ? colorGreen : colorWhite);
                    //royal user
                    holder.royalUser.setColorFilter(favoritesObjects.get(position - 1).royalUser ? colorGreen : colorWhite);
                    //social media verified
                    holder.socialVerified.setColorFilter(favoritesObjects.get(position - 1).socialVerified ? colorGreen : colorWhite);

                    Glide.with(getContext()).load(favoritesObjects.get(position - 1).mainProfilePhoto.squareThumb).error(R.drawable.dummy_img).into(holder.userProfilePicture);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ((int) v.getTag(R.id.user_id_tag) != -1) {
                                Bundle bundle = new Bundle();
                                bundle.putInt(PriveTalkConstants.KEY_OTHER_USER_ID, ((int) v.getTag(R.id.user_id_tag)));
                                bundle.putString(PriveTalkConstants.OTHER_USER_PROFILE_FROM, "other");
                                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);

                            }
                        }
                    });
                } else {
                    holder.view.setTag(R.id.user_id_tag, -1);
                    holder.view.setClickable(false);
                    holder.userName.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.userAge.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.userStatus.setImageDrawable(favoritedByObjects.get(position - favoritesObjects.size() - 2).isOnline ?
                            ContextCompat.getDrawable(getContext(), R.drawable.user_online) : ContextCompat.getDrawable(getContext(), R.drawable.user_offline));
                    holder.verifiedPhotos.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.royalUser.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.socialVerified.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.ageBack.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.shadowView.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.whiteFrame.setVisibility(currentUser.royal_user ? View.VISIBLE : View.INVISIBLE);
                    holder.view.setTag(R.id.user_id_tag, favoritedByObjects.get(position - favoritesObjects.size() - 2).userID);
                    holder.userName.setText(favoritedByObjects.get(position - favoritesObjects.size() - 2).userName.split(" ")[0]);
                    holder.userAge.setText(String.valueOf(favoritedByObjects.get(position - favoritesObjects.size() - 2).userAge));
                    holder.userStatus.setImageDrawable(favoritedByObjects.get(position - favoritesObjects.size() - 2).isOnline ? onlineDrawable : offlineDrawable);

                    //photos verified
                    holder.verifiedPhotos.setColorFilter(favoritedByObjects.get(position - favoritesObjects.size() - 2).photosVerified ? colorGreen : colorWhite);
                    //royal user
                    holder.royalUser.setColorFilter(favoritedByObjects.get(position - favoritesObjects.size() - 2).royalUser ? colorGreen : colorWhite);
                    //social media verified
                    holder.socialVerified.setColorFilter(favoritedByObjects.get(position - favoritesObjects.size() - 2).socialVerified ? colorGreen : colorWhite);


                    if (currentUser.royal_user)
                        Glide.with(getContext()).load(favoritedByObjects.get(position - favoritesObjects.size() - 2).mainProfilePhoto.squareThumb).into(holder.userProfilePicture);
                    else
                        Glide.with(getContext()).load(favoritedByObjects.get(position - favoritesObjects.size() - 2).mainProfilePhoto.squareThumb).
                                bitmapTransform(new BlurTransformation(getContext(), PriveTalkConstants.IMAGE_BLURRINESS)).error(R.drawable.dummy_img).into(holder.userProfilePicture);


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ((int) v.getTag(R.id.user_id_tag) != -1) {

                                if (currentUser.royal_user) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(PriveTalkConstants.KEY_OTHER_USER_ID, ((int) v.getTag(R.id.user_id_tag)));
                                    bundle.putString(PriveTalkConstants.OTHER_USER_PROFILE_FROM, "other");
                                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
                                } else
                                    showRoyalDialog();
                            }
                        }
                    });
                }
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return favoritesObjects.size() + favoritedByObjects.size() + 2;
        }
    }

    private void showRoyalDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.only_available)).
                setMessage(R.string.royal_user_plans_favourite).
                setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                        intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ROYAL_USER_PLANS_FRAGMENT_ID);
                        intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
                    }
                }).setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }
}
