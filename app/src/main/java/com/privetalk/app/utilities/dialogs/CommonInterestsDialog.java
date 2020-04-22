package com.privetalk.app.utilities.dialogs;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.PriveTalkTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CommonInterestsDialog {

    private RelativeLayout rootView;
    private View dialogView;
    private View dismissDialog;
    private View outerSpace;
    private RelativeLayout.LayoutParams params;
    private Animation in, out;
    private RecyclerView mRecyclerView;
    private Activity activity;
    private HashMap<String, ArrayList<InterestObject>> commonInterests;
    private CommonInterestsRecyclerAdapter mAdapter;


    public boolean isVisible = false;


    public CommonInterestsDialog(Activity activity, RelativeLayout rootView) {
        this.rootView = rootView;
        this.activity = activity;

        commonInterests = new HashMap<>();

        //dialog layout params
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        //dialog in and out animations
        in = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out);

        dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_common_interests, null);

        //all dialog views
        dismissDialog = dialogView.findViewById(R.id.closeDialog);
        outerSpace = dialogView.findViewById(R.id.commonFriendsDialogOuterSpace);

        mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialogCommonInterestsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setAdapter(mAdapter = new CommonInterestsRecyclerAdapter(activity));

        outerSpace.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

    }

    public CommonInterestsDialog setDataSet(HashMap<String, ArrayList<InterestObject>> commonInterests) {
        this.commonInterests = commonInterests;
        mAdapter.notifyDataSetChanged();
        return this;
    }

    public CommonInterestsDialog show() {
        isVisible = true;
        dialogView.setLayoutParams(params);

        rootView.addView(dialogView);

        dialogView.startAnimation(in);
        return this;
    }

    public void dismiss() {
        isVisible = false;
        dialogView.startAnimation(out);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.removeView(dialogView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private class CommonInterestsRecyclerAdapter extends RecyclerView.Adapter<CommonInterestsRecyclerAdapter.ViewHolder> {

        private String notSetYetString;
        private static final int ACTIVITIES = 0;
        private static final int MUSIC = 1;
        private static final int MOVIES = 2;
        private static final int LITERATURE = 3;
        private static final int PLACES = 4;
        private static final int COUNT = 5;

        public CommonInterestsRecyclerAdapter(Context context) {
            notSetYetString = context.getString(R.string.not_yet_set);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View rootView;
            public ImageView interestIcon;
            public PriveTalkTextView interestName;
            public String commonInterestString;

            public ViewHolder(View itemView) {
                super(itemView);
                this.rootView = itemView;
                interestIcon = (ImageView) rootView.findViewById(R.id.commonInterestsDialogActivityIcon);
                interestName = (PriveTalkTextView) rootView.findViewById(R.id.commonInterestDialogActivityName);
            }
        }


        @Override
        public CommonInterestsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_common_interests_dialog, null);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(CommonInterestsRecyclerAdapter.ViewHolder holder, int position) {
            if (commonInterests != null)
                switch (position) {
                    case ACTIVITIES:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("a"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(activity).load(R.drawable.profile_interests_activities).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MUSIC:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("m"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(activity).load(R.drawable.profile_interests_music).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MOVIES:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("mo"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(activity).load(R.drawable.profile_interests_movies).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case LITERATURE:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("l"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(activity).load(R.drawable.profile_interests_literature).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case PLACES:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("p"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(activity).load(R.drawable.profile_interests_places).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;

                }

        }

        @Override
        public int getItemCount() {
            return COUNT;
        }
    }

}
