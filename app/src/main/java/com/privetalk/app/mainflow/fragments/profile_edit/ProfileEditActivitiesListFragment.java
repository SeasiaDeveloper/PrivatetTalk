package com.privetalk.app.mainflow.fragments.profile_edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.datasource.InterestsDatasource;
import com.privetalk.app.database.objects.CurrentUserDetails;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.TintImageView;
import com.privetalk.app.utilities.ViewpagerFragment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 28/01/16.
 */
public class ProfileEditActivitiesListFragment extends FragmentWithTitle implements ViewpagerFragment {

    private View rootView;
    private RecyclerView simpleListRecyclerView;
    private int fragmentPosition;

    private LayoutInflater layoutInflater;
    private ArrayList<InterestObject> allDatabaseInterests;
    private String link;
    private String interestType;
    private PriveTalkEditText searchEditText;
    private SimpleListRecyclerAdapter adapter;
    private BroadcastReceiver attributesDownloadedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            allDatabaseInterests.clear();
            allDatabaseInterests.addAll(InterestsDatasource.getInstance(getContext()).getAllInterestsForType(interestType, ""));
            for (InterestObject interestObject : PersonalInfoEditParentFragment.pendingInterests) {
                if (interestObject.interest_type.equals(interestType))
                    allDatabaseInterests.add(interestObject);
            }
            adapter.notifyDataSetChanged();

        }
    };

    public static ProfileEditActivitiesListFragment newInstance(int position) {

        ProfileEditActivitiesListFragment fragmentFirst = new ProfileEditActivitiesListFragment();
        Bundle args = new Bundle();
        args.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, position);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        allDatabaseInterests = new ArrayList<>();

        if (savedInstanceState == null) {
            fragmentPosition = getArguments().getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);

        } else {
            fragmentPosition = savedInstanceState.getInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION);
        }

        switch (fragmentPosition) {
            case PersonalInfoActivitiesPager.ACTIVITIES:
                interestType = "a";
                link = Links.ACTIVITIES;
                break;
            case PersonalInfoActivitiesPager.MUSIC:
                interestType = "m";
                link = Links.MUSIC;
                break;
            case PersonalInfoActivitiesPager.MOVIES:
                interestType = "mo";
                link = Links.MOVIES;
                break;
            case PersonalInfoActivitiesPager.LITERATURE:
                interestType = "l";
                link = Links.LITERATURE;
                break;
            case PersonalInfoActivitiesPager.PLACES:
                interestType = "p";
                link = Links.PLACES;
                break;
            default:
                allDatabaseInterests = new ArrayList<>();

        }

        allDatabaseInterests.clear();
        allDatabaseInterests.addAll(InterestsDatasource.getInstance(getContext()).getAllInterestsForType(interestType, ""));
        for (InterestObject interestObject : PersonalInfoEditParentFragment.pendingInterests) {
            if (interestObject.interest_type.equals(interestType))
                allDatabaseInterests.add(interestObject);
        }

        if (savedInstanceState == null) {
            PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, link, link, null, new JSONObject());
        }

        adapter = new SimpleListRecyclerAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layoutInflater = inflater;

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_edit_simple_list, container, false);

        searchEditText = (PriveTalkEditText) rootView.findViewById(R.id.searchContext);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                allDatabaseInterests.clear();
                allDatabaseInterests.addAll(InterestsDatasource.getInstance(getContext()).getAllInterestsForType(interestType, s.toString()));
                for (InterestObject interestObject : PersonalInfoEditParentFragment.pendingInterests) {
                    if (interestObject.interest_type.equals(interestType))
                        allDatabaseInterests.add(interestObject);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        simpleListRecyclerView = (RecyclerView) rootView.findViewById(R.id.simpleListRecyclerView);
        simpleListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        simpleListRecyclerView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(attributesDownloadedReceiver, new IntentFilter(link));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(attributesDownloadedReceiver);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PersonalInfoEditPagerFragment.KEY_FRAGMENT_POSITION, fragmentPosition);
    }

    @Override
    protected String getActionBarTitle() {
        return null;//getResources().getStringArray(R.array.activities_array)[fragmentPosition].replace(":", "");
    }

    @Override
    public void onPauseFragment() {
        PersonalInfoEditParentFragment.infoChanged = haveDataChanged() || PersonalInfoEditParentFragment.infoChanged;
    }

    @Override
    public void onResumeFragment() {

    }

    private class SimpleListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int VIEWTYPE_NORMAL = 0;
        private int VIEWTYPE_LAST = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (VIEWTYPE_NORMAL == viewType) {
                return new ViewHolder(layoutInflater.inflate(R.layout.row_edit_simple_list, parent, false));
            }

            return new LastLineViewHolder(layoutInflater.inflate(R.layout.row_add_new_activity, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holderIn, final int position) {

            if (((interestType.equals("a") || interestType.equals("p")) && position == allDatabaseInterests.size())) {
                final LastLineViewHolder holder = (LastLineViewHolder) holderIn;
                holder.addNewButton.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {

                        if (!holder.priveTalkEditText.getText().toString().trim().isEmpty()) {

                            InterestObject interest = new InterestObject(interestType, holder.priveTalkEditText.getText().toString().trim());

                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.interests.get(interestType).add(interest);
                            allDatabaseInterests.add(interest);
                            PersonalInfoEditParentFragment.pendingInterests.add(interest);
                            notifyDataSetChanged();
                        }
                    }
                });

            } else {

                final ViewHolder holder = (ViewHolder) holderIn;
                holder.titleTextview.setText(allDatabaseInterests.get(position).title);
                holder.imageView.setImageResource(R.drawable.circle_blue_stroke);
                holder.isSelected = false;

                for (InterestObject interestObject : PersonalInfoEditParentFragment.currentUser.currentUserDetails.interests.get(interestType)) {
                    if (interestObject.title.equals(allDatabaseInterests.get(position).title)) {
                        holder.imageView.setImageResource(R.drawable.tick_selected);
                        holder.isSelected = true;
                        holder.itemView.setTag(interestObject);
                        break;
                    }
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.isSelected)
                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.interests.get(interestType).remove(holder.itemView.getTag());
                        else
                            PersonalInfoEditParentFragment.currentUser.currentUserDetails.interests.get(interestType).add(allDatabaseInterests.get(position));

                        notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return (interestType.equals("a") || interestType.equals("p")) && position == allDatabaseInterests.size() ? VIEWTYPE_LAST : VIEWTYPE_NORMAL;
        }

        @Override
        public int getItemCount() {
            return (interestType.equals("a") || interestType.equals("p")) ? allDatabaseInterests.size() + 1 : allDatabaseInterests.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextview;
        private TintImageView imageView;
        private boolean isSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextview = (TextView) itemView.findViewById(R.id.rowEditTitle);
            imageView = (TintImageView) itemView.findViewById(R.id.tickImageView);
        }
    }

    private class LastLineViewHolder extends RecyclerView.ViewHolder {

        private PriveTalkEditText priveTalkEditText;
        private View addNewButton;

        public LastLineViewHolder(View itemView) {
            super(itemView);
            priveTalkEditText = (PriveTalkEditText) itemView.findViewById(R.id.addNewEditText);
            addNewButton = itemView.findViewById(R.id.addButton);
        }
    }

    private boolean haveDataChanged() {

        CurrentUserDetails currentUserDetails = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails();


        String databaseString = InterestObject.getStringFromList(currentUserDetails.interests.get(interestType));
        String currentString = InterestObject.getStringFromList(PersonalInfoEditParentFragment.currentUser.currentUserDetails.interests.get(interestType));

        return !databaseString.equals(currentString);
    }
}
