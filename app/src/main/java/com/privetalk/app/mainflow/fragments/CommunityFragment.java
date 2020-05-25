package com.privetalk.app.mainflow.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CommunityUsersDatasourse;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.objects.CommunityUsersObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

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
public class CommunityFragment extends FragmentWithTitle {

//    public static final String KEY_SEARCH_FILTER = "search_filter";

    private static final int ALL_RESULTS = 0;
    private static final int SEARCH_COMMUNITY = 1;
    private boolean shouldUpdate = true;

    private View rootView;
    private PriveTalkTextView closeToLocationTextView;
    private RecyclerView promotedUsersRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PromotedUsersAdapter mAdapter;
    private RecyclerView communityUsersRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private GridRecyclerAdapter communityUsersAdapter;
    private int topRecycleHeight;
    private String title;
    private View searchFilter;
    private View clearSearchFiltesButton;
    private JsonArrayRequest getCommunityRequest;
    private AsyncTask<Void, Void, Void> parseCommunityObjects;
    private boolean fragmentPaused = true;
    private RelativeLayout searchLayout;

    private BroadcastReceiver promotedUsersDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAdapter != null)
                mAdapter.refreshData();

            //load profile pic to promote view
            Glide.with(getContext())
                    .load(CurrentUserPhotosDatasource.getInstance(getContext()).checkProfilePic(getContext())!=null?
                            CurrentUserPhotosDatasource.getInstance(getContext()).checkProfilePic(getContext()).square_thumb : "")
                    .error(R.drawable.dummy_img).into((ImageView) rootView.findViewById(R.id.addMeImage));
        }
    };


    private BroadcastReceiver refreshCommunity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCommunityUsers();
        }
    };


    private BroadcastReceiver refreshCommunityDataset = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!fragmentPaused)
                communityUsersAdapter.refresh();
        }
    };


    private Handler handler;
    private Runnable runnable;
    private View addmeView;
    private View progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putString(PriveTalkConstants.KEY_SEARCH_FILTER, "").apply();
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }

        mAdapter = new PromotedUsersAdapter(getContext());

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshCommunityDataset, new IntentFilter(PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(refreshCommunity, new IntentFilter(PriveTalkConstants.BROADCAST_COMMUNITY_RECEIVER));

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

    @Override
    protected String getActionBarTitle() {
        return title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_community, container, false);

        initViews();

        getCommunityUsers();

        return rootView;
    }

    @Override
    public void onResume() {
        fragmentPaused = false;
        handler.post(runnable);
        refreshClearSearchFilterButtonStatus();
        super.onResume();
    }


    @Override
    public void onPause() {
        fragmentPaused = true;
        if (getCommunityRequest != null) getCommunityRequest.cancel();
        if (parseCommunityObjects != null) parseCommunityObjects.cancel(true);
        if (handler != null) handler.removeCallbacksAndMessages(null);
        super.onPause();
    }


    @Override
    public void onDestroy() {
//        System.out.println("This is fucking DESTROY?!!!");
        CommunityUsersObject.markAsSeen(communityUsersAdapter.communityUsersObjects);
        CommunityUsersDatasourse.getInstance(getContext()).deleteCommunityUsers();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshCommunityDataset);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshCommunity);

        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST);

        getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(PriveTalkConstants.KEY_SHOULD_UPDATE_COMMUNITY, true).apply();
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PriveTalkConstants.ACTION_BAR_TITLE, title);
    }

    private void getCommunityUsers() {

        shouldUpdate = getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PriveTalkConstants.KEY_SHOULD_UPDATE_COMMUNITY, true);

        if (!shouldUpdate) return;

//        CommunityUsersDatasourse.getInstance(getContext()).deleteCommunityUsers();
        if (getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getString(PriveTalkConstants.KEY_SEARCH_FILTER, "").isEmpty()) {
            fetchCommunity(ALL_RESULTS);
        } else {
            clearSearchFiltesButton.setVisibility(View.VISIBLE);
            fetchCommunity(SEARCH_COMMUNITY);
        }

    }


    private void refreshClearSearchFilterButtonStatus() {
        if (!getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getString(PriveTalkConstants.KEY_SEARCH_FILTER, "").isEmpty()) {
            clearSearchFiltesButton.setVisibility(View.VISIBLE);
        }
    }

    /*
    Initialize all views and set Actions (click listeners etc)
     */
    private void initViews() {

        progressBar = rootView.findViewById(R.id.progressBar);

        addmeView = rootView.findViewById(R.id.addMePromote);
        addmeView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });

        clearSearchFiltesButton = rootView.findViewById(R.id.clearSearchFiltesButton);
        clearSearchFiltesButton.setVisibility(View.GONE);

        closeToLocationTextView = (PriveTalkTextView) rootView.findViewById(R.id.closeToLocationTextView);

        String area = getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getString(PriveTalkConstants.ADMINISTRATIVE_AREA, "");
        if (!area.isEmpty())
            closeToLocationTextView.setText(getString(R.string.close_to) + " {b}" + area + "{/b}");

        //RecyclerView BoostPopularity/Addme
        promotedUsersRecyclerView = (RecyclerView) rootView.findViewById(R.id.addMe);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(false);

        promotedUsersRecyclerView.setLayoutManager(mLayoutManager);
        promotedUsersRecyclerView.setAdapter(mAdapter);
        /*mAdapter.setImageViewSize(new PromotedUsersAdapter.Callback() {
            @Override
            public void done() {
                    promotedUsersRecyclerView.setAdapter(mAdapter);
            }
        }, promotedUsersRecyclerView);*/


        //Set adapter to 2nd RecyclerView after first RecyclerView created so top Padding can be calculated
        promotedUsersRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                topRecycleHeight = promotedUsersRecyclerView.getHeight();
                communityUsersRecyclerView.setAdapter(communityUsersAdapter);
            }
        });

        //RecyclerView Users Profiles
        communityUsersRecyclerView = (RecyclerView) rootView.findViewById(R.id.communityUsersProfiles);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        communityUsersAdapter = new GridRecyclerAdapter();
        communityUsersRecyclerView.setLayoutManager(mGridLayoutManager);

        //search filter view.  set search filter fragment
        searchFilter = rootView.findViewById(R.id.searchFilter);
        searchFilter.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.SEARCH_FILTER_FRAGMENT_ID);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });

        clearSearchFiltesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchFiltesButton.setVisibility(View.GONE);
                getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putString(PriveTalkConstants.KEY_SEARCH_FILTER, "").apply();
                CommunityUsersDatasourse.getInstance(getContext()).deleteCommunityUsers();
                fetchCommunity(ALL_RESULTS);
            }
        });

    }


    /*
    RecyclerView Adapter With Grid Layout (Span = 2) For Other Users Profiles
     */
    private class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder> {

        private ArrayList<CommunityUsersObject> communityUsersObjects;
        private int colorGreen;
        private int colorWhite;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public CircleImageView userProfilePicture;
            public PriveTalkTextView userName;
            public ImageView verifiedPhotos, royalUser, socialVerified;
            public PriveTalkTextView userAge;
            public ImageView userStatus;
            public ImageView royalUserIcon;


            public ViewHolder(View v) {
                super(v);
                view = v;
                userProfilePicture = (CircleImageView) v.findViewById(R.id.communityProfilePicture);
                userName = (PriveTalkTextView) v.findViewById(R.id.communityUserName);
                verifiedPhotos = (ImageView) v.findViewById(R.id.communityUserTick1);
                royalUser = (ImageView) v.findViewById(R.id.communityUserTick3);
                socialVerified = (ImageView) v.findViewById(R.id.communityUserTick2);
                userAge = (PriveTalkTextView) v.findViewById(R.id.communityUserAge);
                userStatus = (ImageView) v.findViewById(R.id.communityUserStatus);
                royalUserIcon = (ImageView) v.findViewById(R.id.communityCrow);
            }
        }

        public GridRecyclerAdapter() {
            communityUsersObjects = CommunityUsersDatasourse.getInstance(getContext()).getCommunityUsers();
            colorGreen = ContextCompat.getColor(getContext(), R.color.tick_green);
            colorWhite = Color.WHITE;
        }

        public void refresh() {
            if (progressBar != null && progressBar.isShown())
                progressBar.setVisibility(View.GONE);
            communityUsersObjects.clear();
            communityUsersObjects.addAll(CommunityUsersDatasourse.getInstance(getContext()).getCommunityUsers());
            notifyDataSetChanged();
        }


        // Create new views (invoked by the layout manager)
        @Override
        public GridRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_community, parent, false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PriveTalkConstants.KEY_OTHER_USER_ID, ((int) v.getTag(R.id.user_id_tag)));
                    bundle.putString(PriveTalkConstants.OTHER_USER_PROFILE_FROM, "other");
                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
                }
            });

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.view.setPadding(0, (position == 0 || position == 1) ? topRecycleHeight : 0, 0, 0);

            holder.userProfilePicture.setImageDrawable(null);
            communityUsersObjects.get(position).wasSeen = true;

            //set userid tag for touch listener
            holder.view.setTag(R.id.user_id_tag, communityUsersObjects.get(position).userID);
            //name
            holder.userName.setText(communityUsersObjects.get(position).userName.split(" ")[0]);
            //age
            holder.userAge.setText(String.valueOf(communityUsersObjects.get(position).userAge));
            //status online/ofline
            holder.userStatus.setImageDrawable(communityUsersObjects.get(position).isOnline ?
                    ContextCompat.getDrawable(getContext(), R.drawable.user_online) : ContextCompat.getDrawable(getContext(), R.drawable.user_offline));
            //photos verified
            holder.verifiedPhotos.setColorFilter(communityUsersObjects.get(position).photosVerified ? colorGreen : colorWhite);
            //royal user
            holder.royalUser.setColorFilter(communityUsersObjects.get(position).royalUser ? colorGreen : colorWhite);
            //social media verified
            holder.socialVerified.setColorFilter(communityUsersObjects.get(position).socialVerified ? colorGreen : colorWhite);

            holder.royalUserIcon.setImageDrawable(communityUsersObjects.get(position).royalUser ?
                    ContextCompat.getDrawable(getContext(), R.drawable.is_royal_user) : null);

            //load profile picture
            Glide.with(getContext()).load(communityUsersObjects.get(position).profilePhoto.medium_square_thumb).error(R.drawable.dummy_img).dontAnimate().into(holder.userProfilePicture);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return communityUsersObjects.size();
        }
    }

//    private void fetchCommunity(int requestType) {
//        fetchCommunity(requestType);
//    }

    private void fetchCommunity(int requestType) {

      /*  if (requestType == ALL_RESULTS && !CommunityUsersDatasourse.getInstance(getContext()).isEmpty())
            return;*/

        String URL = "";
        JSONObject obj = new JSONObject();
        int method = 0;

        switch (requestType) {
            case ALL_RESULTS:
                if (CommunityUsersDatasourse.getInstance(getContext()).isEmpty()) {
                    CommunityUsersDatasourse.getInstance(getContext()).deleteCommunityUsers();
                    VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST);
                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                    URL = Links.getCommunityWithPagingUrl();
                    PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, URL, PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST, null, null);
                }
                return;

            case SEARCH_COMMUNITY:
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

//                VolleySingleton.getInstance(PriveTalkApplication.getInstance()).cancelAllCommunity();
                VolleySingleton.getInstance(PriveTalkApplication.getInstance()).getRequestQueue().cancelAll(PriveTalkConstants.COMMUNITY_DOWNLOAD_BROADCAST);

                URL = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().royal_user ? Links.ADVANCED_SEARCH_COMMUNITY : Links.SEARCH_COMMUNITY;
                method = JsonArrayRequest.Method.POST;
                try {
                    obj = new JSONObject(getActivity().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getString(PriveTalkConstants.KEY_SEARCH_FILTER, ""));
                    Log.d("CommunityJSON", " request" + obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        getCommunityRequest = new JsonArrayRequest(method, URL, obj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                progressBar.setVisibility(View.GONE);
                Log.d("CommunityJSON", "community_result" + response);

                parseCommunityObjects = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        CommunityUsersDatasourse.getInstance(getContext()).deleteCommunityUsers();

                        CommunityUsersObject.parseAndSave(response);

                        shouldUpdate = false;

                        PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                                .edit().putBoolean(PriveTalkConstants.KEY_SHOULD_UPDATE_COMMUNITY, shouldUpdate).apply();

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Log.d("onPostExecute", " result" + response);
                        if (communityUsersAdapter != null)
                            communityUsersAdapter.refresh();
                    }
                }.execute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CommunityJSON", " error " + error.toString());
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), PriveTalkUtilities.getErrorMessage(error), Toast.LENGTH_SHORT).show();
                if (error.networkResponse != null) {
                }
            }
        }) {

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                }
                return super.parseNetworkError(volleyError);
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

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getCommunityRequest, PriveTalkConstants.COMMUNITY_TAG);

    }


}
