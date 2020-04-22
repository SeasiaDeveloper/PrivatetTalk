package com.privetalk.app.mainflow.fragments.profile_edit;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.privetalk.app.R;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.database.objects.LanguageObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.SpacesItemDecoration;

import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 20/01/16.
 */
public class PersonalInfoEditParentFragment extends FragmentWithTitle {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private View rootView;
    private LayoutInflater inflater;
    private int parentType;

    public static ArrayList<InterestObject> pendingInterests;

    public static final int PARENT_TYPEACTIVITIES = 0;
    public static final int PARENT_TYPE_PROFILE_EDIT = 1;

    public static boolean infoChanged;
    public static CurrentUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
//            System.out.println("SAVED INSTANCE WAS <NOT> NULL PAREnt EDIT");
            parentType = savedInstanceState.getInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE);
            infoChanged = savedInstanceState.getBoolean("infoChanged");
            currentUser = (CurrentUser) savedInstanceState.getSerializable(CurrentUser.class.getName());
        } else {
//            System.out.println("SAVED INSTANCE WAS NULL PAREnt EDIT");
            parentType = getArguments().getInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE);
            infoChanged = false;
            currentUser = (CurrentUser) getArguments().getSerializable(CurrentUser.class.getName());
            pendingInterests = new ArrayList<>();
        }
        if (currentUser ==null)
            currentUser = new CurrentUser();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        this.inflater = inflater;

        rootView = inflater.inflate(R.layout.activity_personal_info_edit, container, false);

        if (currentUser != null) {
            recyclerView = (RecyclerView) rootView.findViewById(R.id.personalInfoEditRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter = (parentType == PARENT_TYPE_PROFILE_EDIT ? new ProfileEditAdapter() : new ActivitiesAdapter()));
            recyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)));
        }

        KeyboardUtilities.setMode(KeyboardUtilities.KEYBOARDMODE.ADJUST_VIEWS, getActivity(), rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_NOTIFICATION_ICON);
        intent.putExtra("toText", true);

        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);

        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.personalInfoEdit);
    }

    @Override
    public void onPause() {
        super.onPause();

        Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_NOTIFICATION_ICON);
        intent.putExtra("toText", false);

        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PriveTalkConstants.PROFILE_EDIT_PARENT_TYPE, parentType);
        outState.putSerializable(CurrentUser.class.getName(), currentUser);
        outState.putBoolean("infoChanged", infoChanged);

    }

    private class ProfileEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        String personalInfoEdit[];

        public ProfileEditAdapter() {
            personalInfoEdit = getResources().getStringArray(R.array.personalInfoEditArray);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.row_personal_info, parent, false)) {

            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((TextView) holder.itemView.findViewById(R.id.row_personal_info_title)).setText(personalInfoEdit[position]);
            if (getValueFromTitle(position) != null)
                ((TextView) holder.itemView.findViewById(R.id.row_personal_info_contents)).setText(getValueFromTitle(position));

            holder.itemView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {

                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.PERSONALEDIT_INFO_PAGER_FRAGMENTPARENT_ID, position, PARENT_TYPE_PROFILE_EDIT);

                }
            });

        }

        @Override
        public int getItemCount() {
            return personalInfoEdit.length;
        }
    }

    private class ActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        String activitiesEdit[];

        public ActivitiesAdapter() {
            activitiesEdit = getResources().getStringArray(R.array.activities_array);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.row_personal_info, parent, false)) {

            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((TextView) holder.itemView.findViewById(R.id.row_personal_info_title)).setText(activitiesEdit[position]);
            if (getValueFromTitle(position) != null)
                ((TextView) holder.itemView.findViewById(R.id.row_personal_info_contents)).setText(getValueFromTitleForActivities(position));

            holder.itemView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {

                    PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.PERSONALEDIT_ACTIVITIES_PAGER_FRAGMENTPARENT_ID, position, PARENT_TYPEACTIVITIES);

                }
            });

        }

        @Override
        public int getItemCount() {
            return activitiesEdit.length;
        }
    }

    private String getValueFromTitleForActivities(int position) {

        switch (position) {
            case PriveTalkConstants.INTERESTS_EDIT_ACTIVITIES:
                return InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("a"));
            case PriveTalkConstants.INTERESTS_EDIT_MUSIC:
                return InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("m"));
            case PriveTalkConstants.INTERESTS_EDIT_MOVIES:
                return InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("mo"));
            case PriveTalkConstants.INTERESTS_EDIT_LITERATURE:
                return InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("l"));
            case PriveTalkConstants.INTERESTS_EDIT_PLACES:
                return InterestObject.getStringFromList(currentUser.currentUserDetails.interests.get("p"));
            default:
                return "";
        }

    }

    private String getValueFromTitle(int position) {

        try {

            switch (position) {
                case PriveTalkConstants.PROFILE_EDIT_NAME:
                    return currentUser.name;
                case PriveTalkConstants.PROFILE_EDIT_AGE:
                    return PriveTalkUtilities.getAge(currentUser.birthday);
                case PriveTalkConstants.PROFILE_EDIT_LOCATION:
                    return currentUser.location;
                case PriveTalkConstants.PROFILE_EDIT_RELATIONSHIP:
                    return currentUser.currentUserDetails.relationship_status.display;
                case PriveTalkConstants.PROFILE_EDIT_SEXUALITY:
                    return currentUser.currentUserDetails.sexuality_status.display;
                case PriveTalkConstants.PROFILE_EDIT_HEIGHT:
                    if (currentUser.currentUserDetails.height > 0)
                        return PriveTalkUtilities.centimetersToFeetAndInches(currentUser.currentUserDetails.height) + " (" + currentUser.currentUserDetails.height + " cm)";
                    else
                        return getString(R.string.not_yet_set);
                case PriveTalkConstants.PROFILE_EDIT_WEIGHT:
                    if (currentUser.currentUserDetails.weight > 0)
                        return (int) (currentUser.currentUserDetails.weight * 2.2f) + " lb (" + currentUser.currentUserDetails.weight + " kg)";
                    else
                        return getString(R.string.not_yet_set);
                case PriveTalkConstants.PROFILE_EDIT_BODY_TYPE:
                    return currentUser.currentUserDetails.body_type.display;
                case PriveTalkConstants.PROFILE_EDIT_COLOUR_OF_HAIR:
                    return currentUser.currentUserDetails.hair_color.display;
                case PriveTalkConstants.PROFILE_EDIT_COLOUR_OF_EYES:
                    return currentUser.currentUserDetails.eyes_color.display;
                case PriveTalkConstants.PROFILE_EDIT_SMOKING:
                    return currentUser.currentUserDetails.smoking_status.display;
                case PriveTalkConstants.PROFILE_EDIT_DRINKING:
                    return currentUser.currentUserDetails.drinking_status.display;
                case PriveTalkConstants.PROFILE_EDIT_EDUCATION:
                    return currentUser.currentUserDetails.education_status.display;
                case PriveTalkConstants.PROFILE_EDIT_WORK:
                    if (currentUser.currentUserDetails.interests.get("o").size() > 0)
                        return currentUser.currentUserDetails.interests.get("o").get(0).title;
                    else
                        return getString(R.string.unspecified);
                case PriveTalkConstants.PROFILE_EDIT_LANGUAGE:
                    return LanguageObject.getStringFromList(currentUser.currentUserDetails.languageObjects);
                case PriveTalkConstants.PROFILE_EDIT_RELIGION:
                    return currentUser.currentUserDetails.faith.religion.display;
                default:
                    return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

    }
}
