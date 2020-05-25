package com.privetalk.app.mainflow.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.mainflow.adapters.PromotedUsersAdapter;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;

public class ProfileVisitorsFragment extends FragmentWithTitle {
    private View rootView;
    private RecyclerView promotedUsersRecyclerView;
    private PromotedUsersAdapter mAdapter;
    private Handler handler;
    private Runnable runnable;
    private LinearLayoutManager mLayoutManager;
    private int topRecycleHeight;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        mAdapter = new PromotedUsersAdapter(getContext());
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(promotedUsersDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PROMOTED_USERS_DOWNLOADED));
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
        return getString(R.string.profile_visitors_screen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.profile_visitors_layout, container, false);
        return rootView;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(promotedUsersDownloaded);
        super.onDestroy();
    }

    private void initViews(){
        promotedUsersRecyclerView = (RecyclerView) rootView.findViewById(R.id.addMe);
        promotedUsersRecyclerView.setLayoutManager(mLayoutManager);
        promotedUsersRecyclerView.setAdapter(mAdapter);
        promotedUsersRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                topRecycleHeight = promotedUsersRecyclerView.getHeight();
            }
        });
    }
}
